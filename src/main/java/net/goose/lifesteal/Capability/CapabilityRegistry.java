package net.goose.lifesteal.Capability;

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
        public static void playerCloneEvent(PlayerEvent.Clone event){

            boolean wasDeath = event.isWasDeath();

            var oldPlayer = event.getOriginal();
            oldPlayer.revive();
            var newPlayer = event.getEntity();

            if(wasDeath) {

                getHeart(oldPlayer).ifPresent(oldHeartDifference -> getHeart(newPlayer).ifPresent(newHeartDifference ->
                        newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - 2)
                ));

                getHeart(oldPlayer).ifPresent(oldLives -> getHeart(newPlayer).ifPresent(newLives ->
                        newLives.setLives(oldLives.getLives())
                ));


                getHeart(newPlayer).ifPresent(newHeartDifference ->
                        newHeartDifference.refreshHearts()
                );
            }else{
                getHeart(newPlayer).ifPresent(newHeartDifference ->
                        newHeartDifference.refreshHearts()
                );
            }
        }

        @SubscribeEvent
        public static void deathEvent(LivingDeathEvent event){

            var killedEntity = event.getEntity();

            String killedEntityType = killedEntity.getType().toString();
            String expectedEntityType = "entity.minecraft.player";


            if(killedEntityType.matches(expectedEntityType)){
                var killerEntity = killedEntity.getLastHurtByMob();
                var damageSource = killedEntity.getLastDamageSource();

                if(killerEntity != null){

                    var killerEntityType = killerEntity.getType().toString();

                    if(killerEntityType.matches(expectedEntityType)){

                        if(damageSource == null){
                            getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + 2));

                            getHeart(killerEntity).ifPresent(newHeartDifference ->
                                    newHeartDifference.refreshHearts()
                            );
                        }else if(damageSource.getEntity() == killerEntity){
                                getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + 2));

                                getHeart(killerEntity).ifPresent(newHeartDifference ->
                                        newHeartDifference.refreshHearts()
                                );
                            }

                        }
                    }

           }
        }
    }
}