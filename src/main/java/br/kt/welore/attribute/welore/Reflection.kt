package br.kt.welore.attribute.welore

import br.kt.welore.attribute.*
import org.bukkit.ChatColor
import java.util.regex.Pattern

object ReflectionAttribute : Attribute<ReflectionInfo>(
        "Reflection",
        "反弹",
        25,
        arrayOf(AttributeType.DEFENCE)
) {

    override fun infoAddFunction(info: AttributeInfo, other: AttributeInfo) {
        val info = info as ReflectionInfo
        val other = other as ReflectionInfo
        info.refData.addAll(other.refData)
    }

    private val regex = Pattern.compile("[^几率]*(几率(?<X>[0-9.%]*))?反弹(?<Y>[0-9.%]*)[^反弹]*")
    override fun readAttribute(lore: String): ReflectionInfo? {
        val lore = ChatColor.stripColor(lore)
        val entire = regex.matcher(lore)
        if (entire.matches()) {
            var x = 1.0
            var y = 0.0
            var sx = entire.group("X")
            if (sx != null && !sx.isEmpty()) {
                sx = sx.replace("%", "")
                x = sx.toDouble() / 100.0
            }
            var sy = entire.group("Y")
            var rate = false
            if (sy != null && !sy.isEmpty()) {
                if (sy.contains("%")) {
                    rate = true
                    sy = sy.replace("%", "")
                }
                y = sy.toDouble()
                if (rate) {
                    y /= 100.0
                }
            }
            return ReflectionInfo(mutableListOf(RefData(x, y, rate)))
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        val value = value as ReflectionInfo
        val damage = data["${AttributeManager.NAMESPACE_EVENT}.${AttributeManager.EVENT_DAMAGE}"] as Double
        var refdmg = 0.0
        for (ref in value.refData) {
            if (Math.random() < ref.chance) {
                refdmg += if (ref.isRate) {
                    ref.value * damage
                } else {
                    ref.value
                }
            }
        }
        data.set("ReflectionAttribute.RefDamage", refdmg)
    }


}

data class ReflectionInfo(
        val refData: MutableList<RefData>
) : AttributeInfo(ReflectionAttribute, 0.0) {
    override fun toString(): String {
        return refData.map(RefData::toString).toString()
    }
}

data class RefData(
        val chance: Double,
        val value: Double,
        val isRate: Boolean
) {
    override fun toString(): String =
            if (isRate) "概率: $chance  反弹: $isRate%"
            else "概率: $chance  反弹: $isRate"
}

