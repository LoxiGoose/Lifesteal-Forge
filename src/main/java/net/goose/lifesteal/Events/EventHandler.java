package net.goose.lifesteal.Events;

import net.goose.lifesteal.Capability.CapabilityRegistry;
import net.goose.lifesteal.Capability.HeartCapAttacher;
import net.goose.lifesteal.Commands.getHitPointDifference;
import net.goose.lifesteal.Commands.getLives;
import net.goose.lifesteal.Commands.setHitPointDifference;
import net.goose.lifesteal.Commands.setLives;
import net.goose.lifesteal.Configurations.ConfigHolder;
import net.goose.lifesteal.Enchantments.ModEnchantments;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = LifeSteal.MOD_ID)

public class EventHandler {
    @SubscribeEvent
    public static void OnCommandsRegister(RegisterCommandsEvent event){
        new getHitPointDifference(event.getDispatcher());
        new setHitPointDifference(event.getDispatcher());
        new getLives(event.getDispatcher());
        new setLives(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }
    @SubscribeEvent
    public static void playerJoinEvent(final PlayerEvent.PlayerLoggedInEvent event){
        Player newPlayer = event.getEntity();

        CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap::refreshHearts);
    }

    @SubscribeEvent
    public static void livingDamageEvent(final LivingDamageEvent event){

        if(!ConfigHolder.SERVER.disableEnchantments.get()){
            Entity Attacker = event.getSource().getEntity();

            if(Attacker != null){

                if(Attacker instanceof LivingEntity _Attacker){

                    float damage = event.getAmount();

                    int level = _Attacker.getMainHandItem().getEnchantmentLevel(ModEnchantments.LIFESTEAL.get());

                    if(level > 0){
                        damage *= ((float) level / (float) ModEnchantments.LIFESTEAL.get().getMaxLevel()) * 0.5f;
                        _Attacker.heal(damage);
                    }

                }

            }
        }

    }

    @SubscribeEvent
    public static void playerCloneEvent(final PlayerEvent.Clone event){

        boolean wasDeath = event.isWasDeath();

        LivingEntity oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();
        LivingEntity newPlayer = event.getEntity();

        CapabilityRegistry.getHeart(oldPlayer).ifPresent(oldHeartDifference -> CapabilityRegistry.getHeart(newPlayer).ifPresent(newHeartDifference ->
                newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference())
        ));

        CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap::refreshHearts);

        if(wasDeath){
            newPlayer.heal(newPlayer.getMaxHealth());
        }

        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void deathEvent(final LivingDeathEvent event){

        LivingEntity killedEntity = event.getEntity();

        if(killedEntity instanceof ServerPlayer || ConfigHolder.SERVER.shouldAllMobsGiveHearts.get()) {
            if(!killedEntity.isAlive()){
                AtomicInteger HeartDifference = new AtomicInteger();

                CapabilityRegistry.getHeart(killedEntity).ifPresent(HeartCap -> HeartDifference.set(HeartCap.getHeartDifference()));

                LivingEntity killerEntity = killedEntity.getLastHurtByMob();

                int amountOfHealthLostUponLoss;

                if(ConfigHolder.SERVER.maximumamountofheartsloseable.get() < 0 ){
                    if(20 + HeartDifference.get() - ConfigHolder.SERVER.amountOfHealthLostUponLoss.get() >= 0 || ConfigHolder.SERVER.playersGainHeartsifKillednoHeart.get()){
                        amountOfHealthLostUponLoss = ConfigHolder.SERVER.amountOfHealthLostUponLoss.get();
                    }else{
                        amountOfHealthLostUponLoss = 20 + HeartDifference.get();
                    }
                }else {
                    if (20 + HeartDifference.get() - ConfigHolder.SERVER.amountOfHealthLostUponLoss.get() >= (20 + ConfigHolder.SERVER.startingHeartDifference.get()) - ConfigHolder.SERVER.maximumamountofheartsloseable.get() || ConfigHolder.SERVER.playersGainHeartsifKillednoHeart.get()) {
                        amountOfHealthLostUponLoss = ConfigHolder.SERVER.amountOfHealthLostUponLoss.get();
                    } else {
                        amountOfHealthLostUponLoss = HeartDifference.get() + ConfigHolder.SERVER.maximumamountofheartsloseable.get();
                    }
                }

                if (killerEntity != null) {
                    if(killerEntity != killedEntity){
                        if (killerEntity instanceof ServerPlayer serverPlayer && !ConfigHolder.SERVER.disableLifesteal.get()) {

                            if (ConfigHolder.SERVER.playersGainHeartsifKillednoHeart.get() || ConfigHolder.SERVER.shouldAllMobsGiveHearts.get()) {
                                CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                            } else {

                                if (!ConfigHolder.SERVER.disableHeartLoss.get()) {
                                    if (ConfigHolder.SERVER.maximumamountofheartsloseable.get() > -1) {
                                        if (ConfigHolder.SERVER.startingHeartDifference.get() + HeartDifference.get() > -ConfigHolder.SERVER.maximumamountofheartsloseable.get()) {
                                            CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                            CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                                        } else {
                                            serverPlayer.displayClientMessage(Component.translatable("This player doesn't have any hearts you can steal."), true);
                                        }

                                    } else {
                                        CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                        CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                                    }
                                } else {
                                    CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                    CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap::refreshHearts);
                                }
                            }

                        }

                        if(!ConfigHolder.SERVER.disableLifesteal.get()){
                            if(!ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByMob.get() && ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByPlayer.get()){
                                if(killerEntity instanceof Player){
                                    CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                                    CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                                }
                            }else{
                                CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                                CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                            }
                        }
                    }else {
                        CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                        CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                    }
                }else{
                    if (!ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByMob.get() && !ConfigHolder.SERVER.loseHeartsOnlyWhenKilledByPlayer.get()) {
                        CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                        CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap::refreshHearts);
                    }
                }

            }
        }
    }
}
