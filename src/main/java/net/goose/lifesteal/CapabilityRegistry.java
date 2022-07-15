package net.goose.lifesteal;

import net.goose.lifesteal.api.HeartCapAttacher;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class CapabilityRegistry {

    public static final Capability<IHeartCap> HEART_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static LazyOptional<IHeartCap> getHeart(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HEART_CAP_CAPABILITY);
    }

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = "lifesteal")
    public static class EventHandler{

        @SubscribeEvent
        public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                HeartCapAttacher.attach(event);
            }
        }

        @SubscribeEvent
        public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
            event.register(IHeartCap.class);
        }

        @SubscribeEvent
        public static void PlayerCloneEvent(PlayerEvent.Clone event){


            boolean WasDeath = event.isWasDeath();

            var OldPlayer = event.getOriginal();
            var NewPlayer = event.getEntity();

            if(WasDeath == true) {
                System.out.println("Worked");

                getHeart(OldPlayer).ifPresent(oldHeartDifference -> getHeart(NewPlayer).ifPresent(newHeartDifference ->
                        newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - 2)
                ));
            }else{
                getHeart(OldPlayer).ifPresent(oldHeartDifference -> getHeart(NewPlayer).ifPresent(newHeartDifference ->
                        newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference())
                ));
            }
        }

        @SubscribeEvent
        public static void DeathEvent(LivingDeathEvent event){

            var KilledEntity = event.getEntity();

            Capability<IHeartCap> HeartCapability = CapabilityManager.get(new CapabilityToken<>(){});


            var PlayerCapability = KilledEntity.getCapability(HeartCapability);

            String KilledEntityType = KilledEntity.getType().toString();
            String ExpectedEntityType = "entity.minecraft.player";

            if(KilledEntityType.matches(ExpectedEntityType)){
                var DamageSource = KilledEntity.getLastDamageSource();
                var KillerEntity = KilledEntity.getLastHurtByMob();

                if(DamageSource != null && KillerEntity != null){

                    var LastMobToAttackType = KillerEntity.getType().toString();

                    if(LastMobToAttackType.matches(ExpectedEntityType)){



                    }

                }


            }

        }
    }
}