package net.goose.lifesteal.Capability;

import net.goose.lifesteal.Configurations.Config;
import net.goose.lifesteal.Configurations.ConfigHolder;
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
                int amountOfHealthLossUponLoss = ConfigHolder.SERVER.amountOfHealthLostUponLoss.get();
                if(!ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByPlayer.get()){
                    getHeart(oldPlayer).ifPresent(oldHeartDifference -> getHeart(newPlayer).ifPresent(newHeartDifference ->
                            newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLossUponLoss)
                    ));
                }else{

                    var KillerEntity = oldPlayer.getLastHurtByMob();

                    if(KillerEntity instanceof Player){
                        var damageSource = oldPlayer.getLastDamageSource();

                        if(damageSource == null){
                            getHeart(oldPlayer).ifPresent(oldHeartDifference -> getHeart(newPlayer).ifPresent(newHeartDifference ->
                                    newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLossUponLoss)
                            ));
                        }else if(damageSource.getEntity() instanceof Player){
                            getHeart(oldPlayer).ifPresent(oldHeartDifference -> getHeart(newPlayer).ifPresent(newHeartDifference ->
                                    newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLossUponLoss)
                            ));
                        }
                    }

                }
            }

            getHeart(newPlayer).ifPresent(newHeartDifference ->
                    newHeartDifference.refreshHearts()
            );
        }

        @SubscribeEvent
        public static void deathEvent(LivingDeathEvent event){

            var killedEntity = event.getEntity();

            if(killedEntity instanceof Player || ConfigHolder.SERVER.shouldAllMobsGiveHearts.get()){
                var killerEntity = killedEntity.getLastHurtByMob();

                if(killerEntity != null){

                    if(killerEntity instanceof Player){
                        var damageSource = killedEntity.getLastDamageSource();
                        int maximumHeartsGainable = ConfigHolder.SERVER.maximumamountofheartsgainable.get();
                        int amountOfHealthLostUponLoss = ConfigHolder.SERVER.amountOfHealthLostUponLoss.get();

                        if(damageSource == null){

                            if(maximumHeartsGainable > 0){

                            }else{
                                getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                getHeart(killerEntity).ifPresent(newHeartDifference ->
                                        newHeartDifference.refreshHearts()
                                );
                            }

                        }else if(damageSource.getEntity() instanceof Player){
                                getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

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