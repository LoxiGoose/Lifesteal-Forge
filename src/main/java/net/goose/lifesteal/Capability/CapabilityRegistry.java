package net.goose.lifesteal.Capability;

import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class CapabilityRegistry {
    public static final Capability<IHeartCap> HEART_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static LazyOptional<IHeartCap> getHeart(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HEART_CAP_CAPABILITY);
    }

    public static LazyOptional<IHeartCap> getHeart(final Entity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HEART_CAP_CAPABILITY);
    }
}