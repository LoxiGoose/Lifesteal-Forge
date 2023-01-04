package net.goose.lifesteal.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.INBTSerializable;


public interface IHeartCap extends INBTSerializable<CompoundTag> {
    void revivedTeleport(ServerLevel level, ILevelCap iLevelCap, boolean synchronize);

    void revivedTeleport(ServerLevel level, ILevelCap iLevelCap);

    void spawnPlayerHead(ServerPlayer serverPlayer);

    int getHeartDifference();

    void setHeartDifference(int hearts);

    void refreshHearts(boolean healtoMax);
}