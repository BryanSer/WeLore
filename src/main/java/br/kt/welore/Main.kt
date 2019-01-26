@file:Suppress("unused")
@file:JvmName("Main")

package br.kt.welore

import br.kt.welore.attribute.AttributeManager
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
}