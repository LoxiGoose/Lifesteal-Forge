package net.goose.lifesteal.capability;

import net.goose.lifesteal.api.IHeartCap;
import net.goose.lifesteal.api.ILevelCap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityRegistry {
    public static final Capability<IHeartCap> HEART_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<ILevelCap> LEVEL_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
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

    public static LazyOptional<ILevelCap> getLevel(final Level level){
        if(level == null)
            return LazyOptional.empty();
        return level.getCapability(LEVEL_CAP_CAPABILITY);
    }

    public static class EventCapHandler {
        @SubscribeEvent
        public static void attachentityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                HeartCapAttacher.attach(event);
            }
        }

        @SubscribeEvent
        public static void attachlevelCapabilities(final AttachCapabilitiesEvent<Level> event) {
            if (event.getObject() instanceof Level) {
                LevelCapAttacher.attach(event);
            }
        }

        @SubscribeEvent
        public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
            event.register(IHeartCap.class);
        }
    }
}