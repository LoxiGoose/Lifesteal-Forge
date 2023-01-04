package net.goose.lifesteal.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.UUID;

public interface ILevelCap extends INBTSerializable<CompoundTag> {
    HashMap getMap();

    void setUUIDanditsBlockPos(UUID uuid, BlockPos blockPos);

    void removeUUIDanditsBlockPos(UUID uuid, BlockPos blockPos);
}
