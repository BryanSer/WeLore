package br.kt.welore.attribute.welore

import br.kt.welore.attribute.*
import org.bukkit.ChatColor

object DodgeAttribute : Attribute<AttributeInfo>(
        "Dodge",
        "伤害赦免",
        5,
        AttributeType.DEFENCE
) {
    override fun readAttribute(lore: String): AttributeInfo? {
        var lore = lore
        lore = ChatColor.stripColor(lore)
        if (lore.matches("[^几率]*几率[0-9.]*%伤害赦免[^伤害赦免]*".toRegex())) {
            lore = lore.replace("[^0-9.]".toRegex(), "")
            try {
                return AttributeInfo(this, lore.toDouble() / 100.0)
            } catch (e: NumberFormatException) {
            }
        }
        return null
    }

    override fun applyAttribute(p: AttributeEntity, value: AttributeInfo, data: AttributeApplyData) {
        if (Math.random() < value.value) {
            data.cancel = true
            data.jumpApply = true
        }
    }
}