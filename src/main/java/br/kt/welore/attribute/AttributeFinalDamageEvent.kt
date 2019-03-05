package br.kt.welore.attribute

import org.bukkit.entity.LivingEntity
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDamageEvent

class AttributeFinalDamageEvent(
        val entity: LivingEntity,
        val damager: LivingEntity?,
        val event: EntityDamageEvent,
        val data: AttributeApplyData
) : Event(), Cancellable {

    var cancel = false
    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }

    override fun isCancelled(): Boolean = cancel

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