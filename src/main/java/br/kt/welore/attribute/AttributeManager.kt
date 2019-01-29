package br.kt.welore.attribute

import Br.API.Utils
import br.kt.welore.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.inventory.ItemStack
import java.lang.ref.WeakReference
import java.util.*

val cacheTime: Long = 1000
val NAMESPACE_EVENT = "Event"

class AttributeLoadEvent(p: Entity, val data: AttributeData) : EntityEvent(p) {
    companion object {
        val handler: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handler
        }
    }

    override fun getHandlers(): HandlerList {
        return handler
    }

}

object AttributeManager : Listener {

    val registeredAttribute: MutableMap<String, Attribute<in AttributeInfo>> = HashMap()
    val playerAttributeCache: MutableMap<String, AttributeData> = HashMap()

    val sortedAttribute: MutableList<Attribute<in AttributeInfo>> = ArrayList()

    var experimentalCache: Boolean = true//实验性属性缓存功能
    private val attributeCache: MutableMap<String, WeakReference<AttributeInfo>?> = WeakHashMap()

    fun getAttribute(e: AttributeEntity): AttributeData {
        var result: AttributeData? = null
        if (e is Player) {
            result = playerAttributeCache[e.name]
        }
        if (result != null) {
            if (System.currentTimeMillis() - result.cacheTime < cacheTime) {
                val evt = AttributeLoadEvent(e, result.copy())
                Bukkit.getPluginManager().callEvent(evt)
                return evt.data
            }
            playerAttributeCache.remove(e.name)
        }
        result = AttributeData(e, System.currentTimeMillis())
        val items: MutableList<ItemStack> = ArrayList()
        for (item in e.equipment.armorContents) {
            items.add(item)
        }
        items.add(e.equipment.itemInHand)
        out@
        for (item in items) {
            if (!item.hasItemMeta() || !item.itemMeta.hasLore()) {
                continue
            }
            val data: MutableMap<Attribute<AttributeInfo>, AttributeInfo> = HashMap()
            for (lore in item.itemMeta.lore) {
                val readAttribute = readAttribute(lore)
                if (readAttribute != null) {
                    if (!readAttribute.attribute.isApplicable(item)) {
                        continue
                    }
                    if (data.containsKey(readAttribute.attribute)) {
                        readAttribute.attribute.infoAddFunction(data[readAttribute.attribute]!!, readAttribute)
                    } else {
                        data[readAttribute.attribute] = readAttribute
                    }
                }
            }
            for (v in data.values) {
                if (!v.checkLimit(e)) {
                    continue@out
                }
            }
            for ((attr, value) in data) {
                if (result.data.containsKey(attr.name)) {
                    attr.infoAddFunction(result.data[attr.name]!!, value)
                } else {
                    result.data[attr.name] = value
                }
            }
        }
        if (e is Player) {
            playerAttributeCache[e.name] = result
        }
        val evt = AttributeLoadEvent(e, result.copy())
        Bukkit.getPluginManager().callEvent(evt)
        return evt.data
    }

    fun getShooter(e: Projectile): LivingEntity? {
        val s = e.shooter
        if (s == null || s !is LivingEntity) {
            return null
        }
        return s
    }

    @EventHandler
    fun onDamage(evt: EntityDamageByEntityEvent) {
        var damager = evt.damager
        if (damager is Projectile) {
            damager = getShooter(damager)
        }
        if (damager == null || damager !is LivingEntity) {
            return
        }
        if (evt.entity !is LivingEntity)
            return
        val entity: LivingEntity = evt.entity as LivingEntity
        val data = AttributeDamageApplyData(getAttribute(entity), getAttribute(damager))
        data.data["$NAMESPACE_EVENT.Damage"] = evt.damage
        //call event
        for (attr in sortedAttribute) {
            if (attr.type.contains(AttributeType.ATTACK)) {
                attr.apply(damager, data.damager(attr) ?: continue, data)
            }
            if (attr.type.contains(AttributeType.DEFENCE)) {
                attr.apply(entity, data.entity(attr) ?: continue, data)
            }
        }
    }

    data class AttributeInfoCache(val info: AttributeInfo?, val contains: Boolean)

    private fun getCache(lore: String): AttributeInfoCache {
        val wr = attributeCache[lore]
        if (wr != null) {
            val get = wr.get()
            if (get === null) {
                attributeCache.remove(lore)
            }
            return AttributeInfoCache(get, true)
        }
        return AttributeInfoCache(null, false)
    }

    private fun putCache(lore: String, ai: AttributeInfo?) {
        if (ai == null) {
            attributeCache[lore] = null
        } else {
            attributeCache[lore] = WeakReference(ai)
        }
    }


    @JvmStatic
    fun init() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance())

        sortedAttribute.addAll(registeredAttribute.values)
        sortedAttribute.sortBy {
            -it.priority
        }

        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), object : Runnable {
            val lastCall: MutableMap<String, Int> = HashMap()

            init {
                for (attr in sortedAttribute) {
                    if (attr.type.contains(AttributeType.PASSIVE)) {
                        lastCall[attr.name] = 0
                    }
                }
            }

            override fun run() {
                val active: MutableList<Attribute<AttributeInfo>> = ArrayList()
                for (attr in sortedAttribute) {
                    if (attr.type.contains(AttributeType.PASSIVE)) {
                        var t = lastCall[attr.name] ?: 0 + 1
                        if (t >= attr.interval) {
                            active.add(attr)
                            t = 0
                        }
                        lastCall[attr.name] = t
                    }
                }
                if (!active.isEmpty())
                    for (p in Utils.getOnlinePlayers()) {
                        val adata = getAttribute(p)
                        val data = AttributeApplyData(adata)
                        for (attr in active) {
                            attr.apply(p, adata.data[attr.name] ?: continue, data)
                        }
                    }

            }
        }, 1, 1)
    }

    @JvmStatic
    fun readAttribute(attr: String): AttributeInfo? {
        val lore = attr.replace(Regex("§."), "")
        var result: AttributeInfo? = null
        if (experimentalCache) {
            val (cache, contains) = getCache(lore)
            if (contains) {
                return cache
            }
        }
        if (result == null) {
            for (a in sortedAttribute) {
                result = a.readAttribute(lore) as AttributeInfo?
                if (result != null) {
                    break
                }
            }
        }
        if (experimentalCache) {
            putCache(lore, result)
        }
        return result
    }
}
