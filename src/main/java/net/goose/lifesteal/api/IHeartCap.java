package net.goose.lifesteal.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;


public interface IHeartCap extends INBTSerializable<CompoundTag> {
    int getHeartDifference();

    void setHeartDifference(int hearts);

    void refreshHearts();
}