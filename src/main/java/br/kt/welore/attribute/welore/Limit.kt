package br.kt.welore.attribute.welore

import Br.API.Utils
import br.kt.welore.attribute.*
import org.black_ixx.playerpoints.PlayerPoints
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Pattern

object PointLimit : Attribute<AttributeInfo>(
        "PointLimit",
        "点卷限制",
        1,
        arrayOf(AttributeType.ATTRIBUTE)
) {
    val API = PlayerPoints.getPlugin(PlayerPoints::class.java).api
    val regex = Pattern.compile("[^点卷限制]*点卷限制(?<value>[0-9]*)点.*")
    override fun readAttribute(lore: String): AttributeInfo? {
        val lore = ChatColor.stripColor(lore)
        val matcher = regex.matcher(lore)
        if (matcher.matches()) {
            val group = matcher.group("value")
            if (group != null && !group.isEmpty()) {
                return object : AttributeInfo(
                        this,
                        group.toDouble()
                ), Limit {
                    override fun getLimitType(): LimitType = LimitType.POINT
                    override fun checkLimit(p: Player): Boolean = API.look(p.name) >= this.value

                }
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
    }

}


object LevelLimit : Attribute<AttributeInfo>(
        "LevelLimit",
        "等级限制",
        1,
        arrayOf(AttributeType.ATTRIBUTE)
) {
    val regex = Pattern.compile("[^等级限制]*等级限制(?<value>[0-9]*)级.*")
    override fun readAttribute(lore: String): AttributeInfo? {
        val lore = ChatColor.stripColor(lore)
        val matcher = regex.matcher(lore)
        if (matcher.matches()) {
            val group = matcher.group("value")
            if (group != null && !group.isEmpty()) {
                return object : AttributeInfo(
                        this,
                        group.toDouble()
                ), Limit {
                    override fun getLimitType(): LimitType = LimitType.LEVEL
                    override fun checkLimit(p: Player): Boolean = p.level >= this.value
                }
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
    }

}

object MoneyLimit : Attribute<AttributeInfo>(
        "MoneyLimit",
        "金钱限制",
        1,
        arrayOf(AttributeType.ATTRIBUTE)
) {
    val regex = Pattern.compile("[^金钱限制]*金钱限制(?<value>[0-9.]*)金币.*")
    override fun readAttribute(lore: String): AttributeInfo? {
        val lore = ChatColor.stripColor(lore)
        val matcher = regex.matcher(lore)
        if (matcher.matches()) {
            val group = matcher.group("value")
            if (group != null && !group.isEmpty()) {
                return object : AttributeInfo(
                        this,
                        group.toDouble()
                ), Limit {
                    override fun getLimitType(): LimitType = LimitType.MONEY
                    override fun checkLimit(p: Player): Boolean = Utils.getEconomy().has(p.name, this.value)
                }
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
    }

}

object PermissionLimit : Attribute<PermissionInfo>(
        "PermissionLimit",
        "权限限制",
        1,
        arrayOf(AttributeType.ATTRIBUTE)
) {
    override fun readAttribute(lore: String): PermissionInfo? {
        val lore = ChatColor.stripColor(lore)
        if (lore.contains("权限限制")) {
            val per = lore.split("限限制".toRegex(), 2)[1]
            return PermissionInfo(per)
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
    }

}


data class PermissionInfo(
        val permission: String
) : AttributeInfo(
        PermissionLimit, 0.0
), Limit {
    override fun getLimitType(): LimitType = LimitType.PERMISSION

    override fun checkLimit(p: Player): Boolean = p.hasPermission(permission)
    override fun toString(): String {
        return "权限限制: $permission"
    }
}