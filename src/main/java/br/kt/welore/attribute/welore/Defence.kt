package br.kt.welore.attribute.welore

import br.kt.welore.Main
import br.kt.welore.attribute.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class DefenceAttribute : Attribute<AttributeInfo>(
        "Defence",
        "防御",
        20,
        arrayOf(AttributeType.DEFENCE)
) {
    override fun readAttribute(lore: String): AttributeInfo? {
        var lore = lore
        if (lore.matches("[^防御]*防御( )*[+-][0-9.]*]点?".toRegex())) {
            lore = lore.replace("[^0-9.+-]".toRegex(), "")
            try {
                return AttributeInfo(this, lore.toDouble())
            } catch (e: NumberFormatException) {
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        val cause = data.data(AttributeManager.NAMESPACE_EVENT, AttributeManager.EVENT_DAMAGECAUSE) as EntityDamageEvent.DamageCause
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && cause != EntityDamageEvent.DamageCause.PROJECTILE) {
            return
        }
        if (p is Player) {

        }
        val app = data["ArmorPenetration.Percent"] as Double
        val apv = data["ArmorPenetration.Value"] as Double
        var def = value.value
        def *= (1.0 - app)
        def -= apv
        if (def < 0) {
            def = 0.0
        }
        var dmg = (data.data(AttributeManager.NAMESPACE_EVENT, AttributeManager.EVENT_DAMAGE) as Number).toDouble()
        dmg -= def
        if (dmg < 0)
            dmg = 0.0
        data["${AttributeManager.NAMESPACE_EVENT}.${AttributeManager.EVENT_DAMAGE}"] = dmg
    }

}

class ResistanceAttribute : Attribute<AttributeInfo>(
        "Resistance",
        "抗性",
        19,
        arrayOf(AttributeType.DEFENCE)
), Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance())

    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onDamage(evt: EntityDamageEvent) {
        val d = res.remove(evt.entity.entityId) ?: return
        var dmg = evt.damage * (1 - d)
        if (dmg < 0) {
            dmg = 0.0
        }
        evt.damage = dmg
    }


    override fun readAttribute(lore: String): AttributeInfo? {
        var lore = lore
        lore = ChatColor.stripColor(lore)
        if (lore.matches("[^抗性]*抗性( )*[+-][0-9.%]*]".toRegex())) {
            lore = lore.replace("[^0-9.+-]".toRegex(), "")
            try {
                return AttributeInfo(this, lore.toDouble() / 100.0)
            } catch (e: NumberFormatException) {
            }
        }
        return null
    }

    val res = mutableMapOf<Int, Double>()

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        res[p.entityId] = value.value
//        var dmg = (data.data(AttributeManager.NAMESPACE_EVENT, AttributeManager.EVENT_DAMAGE) as Number).toDouble()
//        dmg *= (1 - value.value)
//        if (dmg < 0)
//            dmg = 0.0
//        data.set("${AttributeManager.NAMESPACE_EVENT}.${AttributeManager.EVENT_DAMAGE}", dmg)
    }

}