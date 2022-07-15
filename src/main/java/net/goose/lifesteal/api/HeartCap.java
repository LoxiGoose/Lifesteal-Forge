package net.goose.lifesteal.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class HeartCap implements IHeartCap {
    private final LivingEntity livingEntity;
    private int HeartDifference = 0;

    public HeartCap(@Nullable final LivingEntity entity) {
        System.out.println("WE HAVE CONNECTED :DDD");
        System.out.println(entity);
        this.livingEntity = entity;
    }

    @Override
    public int getHeartDifference() {
        System.out.println(11);
        return HeartDifference;
    }

    @Override
    public void setHeartDifference(int hearts) {
        System.out.println(1);
        this.HeartDifference = hearts;

        refreshhearts();
    }

    @Override
    public void refreshhearts(){
        System.out.println(111);
        var Attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        Set<AttributeModifier> attributemodifiers = Attribute.getModifiers();

        if(!attributemodifiers.isEmpty()){
            Iterator<AttributeModifier> attributeModifierIterator = attributemodifiers.iterator();

            boolean FoundAttribute = false;
            while (attributeModifierIterator.hasNext()) {

                AttributeModifier attributeModifier = attributeModifierIterator.next();
                if (attributeModifier != null && attributeModifier.getName().equals("LifeStealHealthModifier")) {
                    FoundAttribute = true;

                    Attribute.removeModifier(attributeModifier);

                    AttributeModifier newmodifier = new AttributeModifier("LifeStealHealthModifier", HeartDifference, AttributeModifier.Operation.ADDITION);

                    Attribute.addPermanentModifier(newmodifier);
                }
            }

            if(FoundAttribute == false){
                AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", HeartDifference, AttributeModifier.Operation.ADDITION);

                Attribute.addPermanentModifier(attributeModifier);
            }
        }else{
            AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", HeartDifference, AttributeModifier.Operation.ADDITION);

            Attribute.addPermanentModifier(attributeModifier);
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("heartdifference", getHeartDifference());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setHeartDifference(tag.getInt("heartdifference"));
    }
}
