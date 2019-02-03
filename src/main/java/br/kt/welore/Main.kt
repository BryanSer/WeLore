@file:Suppress("unused")
@file:JvmName("Main")

package br.kt.welore

import br.kt.welore.attribute.AttributeManager
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        instance = this
        AttributeManager.init()
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
        if (args[0].equals("see", true) && sender is Player) {
            val readAttribute = AttributeManager.readAttribute(sender.itemInHand, sender)
            sender.sendMessage("§6你手上的物品属性如下: ")
            for (attr in readAttribute) {
                sender.sendMessage(attr.toString())
            }
        }
        return false
    }
}