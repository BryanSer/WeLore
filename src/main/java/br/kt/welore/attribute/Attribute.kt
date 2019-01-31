@file:Suppress("unused")

package br.kt.welore.attribute

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

typealias AttributeEntity = LivingEntity

abstract class Attribute<out T : AttributeInfo>(
        val name: String,
        val displayName: String,
        val priority: Int,
        vararg val type: AttributeType,
        val interval: Int = 5//默认重复间隔 作为PASSIVE时
) {

    open fun isApplicable(item: ItemStack): Boolean = true

    open fun infoAddFunction(info: AttributeInfo, other: AttributeInfo) {
        info.value += other.value
    }

    abstract fun readAttribute(lore: String): T?

    open fun apply(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        if (data.jumpApply) {
            return
        }
        this.applyAttribute(p, value, data)
    }

    abstract fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData)
}

open class AttributeApplyData(
        val entityAttribute: AttributeData,
        val data: MutableMap<String, Any?> = HashMap(),
        var cancel: Boolean = false,
        var jumpApply: Boolean = false
) {
    operator fun get(path: String): Any? {
        return data[path]
    }

    fun data(namespace: String, key: String): Any? {
        return data["$namespace.$key"]
    }

    fun set(path: String, value: Any?) {
        data[path] = value
    }

    fun entity(namespace: String, key: String): Any? {
        return data["Entity.$namespace.$key"]
    }

    private fun <T : AttributeInfo> entity(attr: String): T? {
        return entityAttribute.data[attr] as T
    }

    fun entity(attr: Attribute<out AttributeInfo>): AttributeInfo? = entity(attr.name)
}

class AttributeDamageApplyData(
        entityAttribute: AttributeData,
        val damagerAttribute: AttributeData
) : AttributeApplyData(entityAttribute) {


    fun damager(namespace: String, key: String): Any? {
        return data["Damager.$namespace.$key"]
    }

    private fun <T : AttributeInfo> damager(attr: String): T? {
        return damagerAttribute.data[attr] as T
    }

    fun damager(attr: Attribute<out AttributeInfo>): AttributeInfo? = damager(attr.name)
}

data class AttributeData(
        val holder: AttributeEntity,
        val cacheTime: Long,
        val data: MutableMap<String, AttributeInfo> = HashMap()
)


interface Limit {
    fun getLimitType(): LimitType
    fun checkLimit(p: Player): Boolean
}


interface Probability {
    fun getProbability(): Double

    fun randomCast(p: AttributeEntity): Boolean = Math.random() < this.getProbability()
}

enum class LimitType {
    PERMISSION,
    LEVEL,
    MONEY,
    POINT,
    USED;
}

enum class AttributeType {
    ATTACK,
    DEFENCE,
    PASSIVE,
    ATTRIBUTE,
    COSTUME;
}