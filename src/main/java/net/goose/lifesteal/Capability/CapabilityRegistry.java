package net.goose.lifesteal.Capability;

import net.goose.lifesteal.Commands.getHitPointDifference;
import net.goose.lifesteal.Commands.getLives;
import net.goose.lifesteal.Commands.setHitPointDifference;
import net.goose.lifesteal.Commands.setLives;
import net.goose.lifesteal.Configurations.ConfigHolder;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHeartCap;
import net.goose.lifesteal.enchantment.ModEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.concurrent.atomic.AtomicInteger;

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



    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = LifeSteal.MOD_ID)

    public static class EventHandler {

        @SubscribeEvent
        public static void OnCommandsRegister(RegisterCommandsEvent event){
            new getHitPointDifference(event.getDispatcher());
            new setHitPointDifference(event.getDispatcher());
            new getLives(event.getDispatcher());
            new setLives(event.getDispatcher());

            ConfigCommand.register(event.getDispatcher());
        }

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
        public static void playerJoinEvent(PlayerEvent.PlayerLoggedInEvent event){
            LivingEntity newPlayer = event.getEntityLiving();

            getHeart(newPlayer).ifPresent(IHeartCap::refreshHearts);
        }

        @SubscribeEvent
        public static void livingDamageEvent(LivingDamageEvent event){

            if(!ConfigHolder.SERVER.disableEnchantments.get()){
                Entity Attacker = event.getSource().getEntity();

                if(Attacker != null){

                    if(Attacker instanceof LivingEntity _Attacker){

                        float damage = event.getAmount();

                        int level = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.LIFESTEAL.get(), _Attacker);

                        if(level > 0){

                            damage *= ((float) level / (float) ModEnchantments.LIFESTEAL.get().getMaxLevel()) * 0.5f;
                            _Attacker.heal(damage);

                        }

                    }

                }
            }

        }

        @SubscribeEvent
        public static void playerCloneEvent(PlayerEvent.Clone event){

            boolean wasDeath = event.isWasDeath();

            LivingEntity oldPlayer = event.getOriginal();
            oldPlayer.reviveCaps();
            LivingEntity newPlayer = event.getEntityLiving();

            getHeart(oldPlayer).ifPresent(oldHeartDifference -> getHeart(newPlayer).ifPresent(newHeartDifference ->
                    newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference())
            ));

            getHeart(newPlayer).ifPresent(IHeartCap::refreshHearts);

            if(wasDeath){
                newPlayer.heal(newPlayer.getMaxHealth());
            }

            oldPlayer.invalidateCaps();
        }

        @SubscribeEvent
        public static void deathEvent(LivingDeathEvent event){

            LivingEntity killedEntity = event.getEntityLiving();

            if(killedEntity instanceof Player || ConfigHolder.SERVER.shouldAllMobsGiveHearts.get()) {
                LivingEntity killerEntity = killedEntity.getLastHurtByMob();

                int amountOfHealthLostUponLoss = ConfigHolder.SERVER.amountOfHealthLostUponLoss.get();

                if (killerEntity != null) {

                    if (!killedEntity.isAlive()) {

                        if(killerEntity != killedEntity){
                            if (killerEntity instanceof Player && !ConfigHolder.SERVER.disableLifesteal.get()) {

                                AtomicInteger HeartDifference = new AtomicInteger();

                                getHeart(killedEntity).ifPresent(HeartCap -> HeartDifference.set(HeartCap.getHeartDifference()));

                                if (ConfigHolder.SERVER.playersGainHeartsifKillednoHeart.get() || ConfigHolder.SERVER.shouldAllMobsGiveHearts.get()) {
                                    getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                    getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                                } else {

                                    if (!ConfigHolder.SERVER.disableHeartLoss.get()) {
                                        if (ConfigHolder.SERVER.minimumamountofheartscanhave.get() > -1) {
                                            if (ConfigHolder.SERVER.startingHeartDifference.get() + HeartDifference.get() > -ConfigHolder.SERVER.minimumamountofheartscanhave.get()) {
                                                getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                                getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                                            } else {
                                                killerEntity.sendMessage(Component.nullToEmpty("This player doesn't have any hearts you can steal."), killerEntity.getUUID());
                                            }

                                        } else {
                                            getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                            getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                                        }
                                    } else {
                                        getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                        getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                                    }
                                }

                            }

                            if(!ConfigHolder.SERVER.disableLifesteal.get()){
                                if(!ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByMob.get() && ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByPlayer.get()){
                                    if(killerEntity instanceof Player){
                                        getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));
                                        getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                                    }
                                }else{
                                    getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));
                                    getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                                }
                            }
                        }else{
                            getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));
                            getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                        }

                    }
                }else{
                    if (!ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByMob.get() && !ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByPlayer.get()) {
                        getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));
                        getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                    }
                }

            }
        }
    }
}