package br.kt.welore.attribute.welore

import br.kt.welore.Main
import br.kt.welore.attribute.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerExpChangeEvent
import java.util.regex.Pattern

object ExpAttribute : Attribute<AttributeInfo>(
        "Exp",
        "经验",
        1,
        arrayOf(AttributeType.PASSIVE),
        interval = 5
), Listener {
    init {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance())
    }

    val regex = Pattern.compile("[^经验]*经验(?<value>[+-][0-9.]*)%")
    val data = HashMap<String, Double>()

    override fun onDisable() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onExp(evt: PlayerExpChangeEvent) {
        val d = data[evt.player.name]
        if (d != null) {
            evt.amount = (evt.amount * (1 + d)).toInt()
        }
    }

    override fun readAttribute(lore: String): AttributeInfo? {
        val lore = ChatColor.stripColor(lore)
        val matcher = regex.matcher(lore)
        if (matcher.matches()) {
            val g = matcher.group("value")
            if (g != null && !g.isEmpty()) {
                return AttributeInfo(this, g.toDouble() / 100.0)
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        if (p is Player)
            this.data[p.name] = value.value
    }

}