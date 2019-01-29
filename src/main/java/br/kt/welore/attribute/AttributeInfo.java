package br.kt.welore.attribute;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AttributeInfo {
    Attribute<AttributeInfo> attribute;
    double value;

    public AttributeInfo(Attribute<? extends AttributeInfo> attribute, double value) {
        this.attribute = (Attribute<AttributeInfo>) attribute;
        this.value = value;
    }

    public boolean checkLimit(LivingEntity p) {
        if (p instanceof Player) {
            if (this instanceof Limit && !((Limit) this).checkLimit((Player) p)) {
                return false;
            }
        }
        return true;
    }

    public boolean checkRandom(LivingEntity p) {
        if (this instanceof Probability && !((Probability) this).randomCast(p)) {
            return false;
        }
        return true;
    }

    public void add(AttributeInfo other) {
        attribute.infoAddFunction(this, other);
    }

}
