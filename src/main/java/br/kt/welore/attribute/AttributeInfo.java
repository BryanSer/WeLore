package br.kt.welore.attribute;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AttributeInfo implements Cloneable {
    Attribute attribute;
    double value;

    public AttributeInfo(Attribute attribute, double value) {
        this.attribute = attribute;
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

    public Attribute<AttributeInfo> getAttribute() {
        return attribute;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.attribute.getDisplayName() + ":  " + this.value;
    }


    //public AttributeInfo copy() {
    //    return new AttributeInfo(this.attribute, this.value);
    //}

    @Override
    protected AttributeInfo clone() {
        try {
            return (AttributeInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
