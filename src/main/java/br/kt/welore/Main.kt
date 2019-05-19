@file:Suppress("unused")
@file:JvmName("Main")

package br.kt.welore

import br.kt.welore.attribute.AttributeManager
import me.clip.placeholderapi.external.EZPlaceholderHook
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        instance = this
        AttributeManager.init()

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            object : EZPlaceholderHook(this, "welore") {
                override fun onPlaceholderRequest(p: Player, params: String): String {
                    val ad = AttributeManager.getAttribute(p)
                    for (v in ad.data.values) {
                        if (v.attribute.displayName.equals(params, true)) {
                            return v.toString()
                        }
                    }
                    return ""
                }
            }.hook()
        }
    }

    companion object {
        private var instance: Main? = null

        @JvmStatic
        fun getInstance(): Main {
            return instance!!
        }

    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty() || args[0].equals("help", true)) {
            return false
        }
        if (args[0].equals("seeme", true) && sender is Player) {
            val readAttribute = AttributeManager.getAttribute(sender)
            sender.sendMessage("§6你身上的属性如下: ")
            for (attr in readAttribute.data.values) {
                sender.sendMessage(attr.toString())
            }
            return true
        }
        if (args[0].equals("see", true) && sender is Player) {
            if (sender.itemInHand == null || sender.itemInHand.amount == 0 || sender.itemInHand.type == Material.AIR) {
                return false
            }
            val readAttribute = AttributeManager.readAttribute(sender.itemInHand, sender)
            sender.sendMessage("§6你手上的物品属性如下: ")
            for (attr in readAttribute) {
                sender.sendMessage(attr.toString())
            }
            return true
        }
        return false
    }
}