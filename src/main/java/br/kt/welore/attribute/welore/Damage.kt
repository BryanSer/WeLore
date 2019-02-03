package br.kt.welore.attribute.welore

import br.kt.welore.attribute.*
import org.bukkit.ChatColor
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent
import java.util.regex.Pattern

class DamageAttribute : Attribute<AttributeInfo>(
        "Damage",
        "伤害",
        10,
        AttributeType.ATTACK
) {

    override fun readAttribute(lore: String): AttributeInfo? {
        var lore = lore
        if (lore.matches("[^伤害]*伤害[+-][0-9.]*点?".toRegex())) {
            lore = lore.replace("[^0-9.%+-]".toRegex(), "")
            if (lore.contains("%")) {
                return null
            }
            try {
                return AttributeInfo(this, lore.toDouble())
            } catch (e: NumberFormatException) {
            }

        }
        return null
    }

    override fun applyAttribute(p: LivingEntity, value: AttributeInfo, data: AttributeApplyData) {
        val cause = data.data(AttributeManager.NAMESPACE_EVENT, AttributeManager.EVENT_DAMAGECAUSE) as EntityDamageEvent.DamageCause
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && cause != EntityDamageEvent.DamageCause.PROJECTILE) {
            return
        }
        val dmg = (data.data(AttributeManager.NAMESPACE_EVENT, AttributeManager.EVENT_DAMAGE) as Number).toDouble()
        data.set("${AttributeManager.NAMESPACE_EVENT}.${AttributeManager.EVENT_DAMAGE}", dmg + value.value)
    }
}

class DamagePercentAttribute : Attribute<AttributeInfo>(
        "DamagePercent",
        "伤害",
        11,
        AttributeType.ATTACK
) {
    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        val cause = data.data(AttributeManager.NAMESPACE_EVENT, AttributeManager.EVENT_DAMAGECAUSE) as EntityDamageEvent.DamageCause
        if (cause != EntityDamageEvent.DamageCause.ENTITY_ATTACK && cause != EntityDamageEvent.DamageCause.PROJECTILE) {
            return
        }
        var dmg = (data.data(AttributeManager.NAMESPACE_EVENT, AttributeManager.EVENT_DAMAGE) as Number).toDouble()
        dmg *= (1.0 + value.value)
        data.set("${AttributeManager.NAMESPACE_EVENT}.${AttributeManager.EVENT_DAMAGE}", dmg)
    }

    override fun readAttribute(lore: String): AttributeInfo? {
        var lore = lore
        if (lore.matches("伤害[+-][0-9.]*%".toRegex())) {
            lore = lore.replace("[^0-9.%+-]".toRegex(), "")
            if (!lore.contains("%")) {
                return null
            }
            lore = lore.replace("%", "")
            try {
                return AttributeInfo(this, lore.toDouble() / 100.0)
            } catch (e: NumberFormatException) {
            }
        }
        return null
    }
}

object ArmorPenetrationAttribute : Attribute<AttributeInfo>(
        "ArmorPenetration",
        "护甲穿透",
        19,
        AttributeType.ATTACK
) {
    override fun readAttribute(lore: String): AttributeInfo? {
        var lore = lore
        lore = ChatColor.stripColor(lore)
        if (lore.matches("[^护甲穿透]*护甲穿透( )*[+-][0-9.]*点?".toRegex())) {
            lore = lore.replace("[^0-9.%+-]".toRegex(), "")
            if (lore.contains("%")) {
                return null
            }
            try {
                return AttributeInfo(this, lore.toDouble())
            } catch (e: NumberFormatException) {
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        data.set("ArmorPenetration.Value", value.value)
    }
}

object ArmorPenetrationPercentAttribute : Attribute<AttributeInfo>(
        "ArmorPenetrationPercent",
        "护甲穿透",
        19,
        AttributeType.ATTACK
) {
    override fun readAttribute(lore: String): AttributeInfo? {
        var lore = lore
        lore = ChatColor.stripColor(lore)
        if (lore.matches("[^护甲穿透]*护甲穿透( )*[+-][0-9.%]*".toRegex())) {
            lore = lore.replace("[^0-9.%+-]".toRegex(), "")
            if (!lore.contains("%")) {
                return null
            }
            lore = lore.replace("%", "")
            try {
                return AttributeInfo(this, lore.toDouble() / 100.0)
            } catch (e: NumberFormatException) {
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        data.set("ArmorPenetration.Percent", value.value)
    }
}

object DamageBoostAttribute : Attribute<DamageBoostInfo>(
        "DamageBoost",
        "伤害提升",
        5,
        AttributeType.ATTACK
) {
    override fun infoAddFunction(info: AttributeInfo, other: AttributeInfo) {
        val info = info as DamageBoostInfo
        val other = other as DamageBoostInfo
        info.boostData.addAll(other.boostData)
    }

    private val regex = Pattern.compile("[^几率]*(几率(?<X>[0-9.%]*))伤害(?<Y>[+-][0-9.%]*)点?")
    override fun readAttribute(lore: String): DamageBoostInfo? {
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
            return DamageBoostInfo(mutableListOf(BoostData(x, y, rate)))
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        val value = value as DamageBoostInfo
        var dmgboost = 0.0
        var dmgboostr = 0.0
        for (dbi in value.boostData) {
            if (Math.random() < dbi.chance) {
                if (dbi.isRate) {
                    dmgboostr += dbi.value
                } else {
                    dmgboost += dbi.value
                }
            }
        }
        data.set("DamageBoostAttribute.Value", dmgboost)
        data.set("DamageBoostAttribute.Rate", dmgboostr)
    }

}

data class DamageBoostInfo(
        val boostData: MutableList<BoostData>
) : AttributeInfo(DamageBoostAttribute, 0.0) {
    override fun toString(): String {
        return boostData.map(BoostData::toString).toString()
    }
}

data class BoostData(
        val chance: Double,
        val value: Double,
        val isRate: Boolean
) {
    override fun toString(): String =
            if (isRate) "概率: $chance  增幅: $value%"
            else "概率: $chance  增幅: $value"

}

