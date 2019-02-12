package br.kt.welore.attribute.welore

import br.kt.welore.attribute.*
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.regex.Pattern

object MoveSpeedAttribute : Attribute<AttributeInfo>(
        "MoveSpeed",
        "移速",
        1,
        AttributeType.PASSIVE,
        interval = 5
) {
    init {
        super.defaultInfo = AttributeInfo(this, 0.0)
    }


    private val regex = Pattern.compile("[^移速]*移速(?<value>[+-][0-9.]*)%")!!
    override fun readAttribute(lore: String): AttributeInfo? {
        val lore = ChatColor.stripColor(lore)
        val matcher = regex.matcher(lore)
        if (matcher.matches()) {
            val v = matcher.group("value")
            if (v != null && !v.isEmpty()) {
                return AttributeInfo(this, v.toDouble() / 100.0)
            }
        }
        return null
    }


    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        if (p is Player) {
            var speed = (0.2 * (value.value + 1.0)).toFloat()
            if (speed > 1.0) {
                speed = 1f
            }
            p.walkSpeed = speed
        }
    }

}
