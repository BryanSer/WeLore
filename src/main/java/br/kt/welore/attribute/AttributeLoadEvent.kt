package br.kt.welore.attribute

import org.bukkit.entity.Entity
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityEvent

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