package br.kt.welore.attribute.welore;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.kt.welore.attribute.Attribute;
import br.kt.welore.attribute.AttributeApplyData;
import br.kt.welore.attribute.AttributeInfo;
import br.kt.welore.attribute.AttributeType;

public class DamageAttribute extends Attribute<DamageAttribute.DamageAttributeInfo> {
    public class DamageAttributeInfo extends AttributeInfo {

        private boolean percent = false;

        public DamageAttributeInfo(double value, boolean percent) {
            super(DamageAttribute.this, value);
            this.percent = percent;
        }


        public boolean isPercent() {
            return percent;
        }
    }

    public DamageAttribute() {
        super("Damage", "伤害", 10, new AttributeType[]{AttributeType.ATTACK}, 1);
    }

    @Nullable
    @Override
    public DamageAttributeInfo readAttribute(@NotNull String lore) {
        lore = ChatColor.stripColor(lore);
        if (lore.matches("[^伤害]*伤害( )*[+-][0-9.%]]")) {
            lore = lore.replaceAll("[^0-9.%+-]", "");

        }
        return null;
    }

    @Override
    public void applyAttribute(@NotNull LivingEntity p, @NotNull DamageAttributeInfo value, @NotNull AttributeApplyData data) {

    }
}
