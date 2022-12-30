package net.goose.lifesteal.event;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.api.IHeartCap;
import net.goose.lifesteal.api.ILevelCap;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.command.lifestealCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = LifeSteal.MOD_ID)

public class EventHandler {
    @SubscribeEvent
    public static void OnCommandsRegister(RegisterCommandsEvent event) {
        new lifestealCommand(event.getDispatcher());

        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void playerJoinEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        Player newPlayer = event.getEntity();
        ServerPlayer serverPlayer = (ServerPlayer) newPlayer;
        newPlayer.getServer().getAllLevels().forEach((level) -> CapabilityRegistry.getLevel(level).ifPresent(ILevelCap -> {
            if(!serverPlayer.getLevel().isClientSide){
                HashMap hashMap = ILevelCap.getMap();
                BlockPos blockPos = (BlockPos) hashMap.get(serverPlayer.getUUID());

                if(blockPos != null){
                    ILevelCap.removeBannedUUIDanditsBlockPos(serverPlayer.getUUID(), blockPos);
                    if(serverPlayer.getLevel() == level){
                        serverPlayer.connection.teleport(blockPos.getX(), blockPos.getY(), blockPos.getZ(), serverPlayer.getXRot(), serverPlayer.getYRot());
                    }else{
                        serverPlayer.teleportTo(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), serverPlayer.getXRot(), serverPlayer.getYRot());
                    }
                    serverPlayer.jumpFromGround();
                    serverPlayer.syncPacketPositionCodec(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }
            }
        }));

        CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap -> {
            if(!serverPlayer.getLevel().isClientSide){
                IHeartCap.refreshHearts(false);
            }
        });
    }
    @SubscribeEvent
    public static void playerCloneEvent(final PlayerEvent.Clone event) {

        boolean wasDeath = event.isWasDeath();

        LivingEntity oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();
        LivingEntity newPlayer = event.getEntity();

        CapabilityRegistry.getHeart(oldPlayer).ifPresent(oldHeartDifference -> CapabilityRegistry.getHeart(newPlayer).ifPresent(newHeartDifference ->
                newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference())
        ));

        CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));

        if (wasDeath) {
            newPlayer.heal(newPlayer.getMaxHealth());
        }

        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void totemofUndyingEvent(final LivingUseTotemEvent event) {
        if(!event.isCanceled()){
            LivingEntity entityUsed = event.getEntity();
            if (entityUsed instanceof ServerPlayer serverPlayer) {
                AtomicInteger HeartDifference = new AtomicInteger();
                CapabilityRegistry.getHeart(entityUsed).ifPresent(HeartCap -> HeartDifference.set(HeartCap.getHeartDifference()));

                if (HeartDifference.get() >= 20) {
                    ModCriteria.USE_TOTEM_WHILE_20_MAX_HEARTS.trigger(serverPlayer);
                }
            }
        }
    }

    @SubscribeEvent
    public static void deathEvent(final LivingDropsEvent event) {

        LivingEntity killedEntity = event.getEntity();

        if (killedEntity instanceof ServerPlayer || LifeSteal.config.shouldAllMobsGiveHearts.get()) {
            if (!killedEntity.isAlive()) {
                AtomicInteger HeartDifference = new AtomicInteger();

                CapabilityRegistry.getHeart(killedEntity).ifPresent(HeartCap -> HeartDifference.set(HeartCap.getHeartDifference()));

                LivingEntity killerEntity = killedEntity.getLastHurtByMob();

                int amountOfHealthLostUponLoss;

                if (LifeSteal.config.maximumamountofheartsLoseable.get() < 0) {
                    if (20 + HeartDifference.get() - LifeSteal.config.amountOfHealthLostUponLoss.get() >= 0 || LifeSteal.config.playersGainHeartsifKillednoHeart.get()) {
                        amountOfHealthLostUponLoss = LifeSteal.config.amountOfHealthLostUponLoss.get();
                    } else {
                        amountOfHealthLostUponLoss = 20 + HeartDifference.get();
                    }
                } else {
                    if (20 + HeartDifference.get() - LifeSteal.config.amountOfHealthLostUponLoss.get() >= (20 + LifeSteal.config.startingHeartDifference.get()) - LifeSteal.config.maximumamountofheartsLoseable.get() || LifeSteal.config.playersGainHeartsifKillednoHeart.get()) {
                        amountOfHealthLostUponLoss = LifeSteal.config.amountOfHealthLostUponLoss.get();
                    } else {
                        amountOfHealthLostUponLoss = HeartDifference.get() + LifeSteal.config.maximumamountofheartsLoseable.get();
                    }
                }

                if (killerEntity != null) {
                    if (killerEntity != killedEntity) {
                        if (killerEntity instanceof ServerPlayer serverPlayer && !LifeSteal.config.disableLifesteal.get()) {

                            if (LifeSteal.config.playersGainHeartsifKillednoHeart.get() || LifeSteal.config.shouldAllMobsGiveHearts.get()) {
                                CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                            } else {

                                if (!LifeSteal.config.disableHeartLoss.get()) {
                                    if (LifeSteal.config.maximumamountofheartsLoseable.get() > -1) {
                                        if (LifeSteal.config.startingHeartDifference.get() + HeartDifference.get() > -LifeSteal.config.maximumamountofheartsLoseable.get()) {
                                            CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                            CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                                        } else {
                                            serverPlayer.displayClientMessage(Component.translatable("chat.message.lifesteal.no_more_hearts_to_steal"), true);
                                        }

                                    } else {
                                        CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                        CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                                    }
                                } else {
                                    CapabilityRegistry.getHeart(killerEntity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + amountOfHealthLostUponLoss));

                                    CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                                }
                            }

                        }

                        if (!LifeSteal.config.disableLifesteal.get()) {
                            if (!LifeSteal.config.loseHeartsOnlyWhenKilledByMob.get() && LifeSteal.config.loseHeartsOnlyWhenKilledByPlayer.get()) {
                                if (killerEntity instanceof Player) {
                                    CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                                    CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                                }
                            } else {
                                CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                                CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                            }
                        }
                    } else {
                        CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                        CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                    }
                } else {
                    if (!LifeSteal.config.loseHeartsOnlyWhenKilledByMob.get() && !LifeSteal.config.loseHeartsOnlyWhenKilledByPlayer.get()) {
                        CapabilityRegistry.getHeart(killedEntity).ifPresent(oldHeartDifference -> oldHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference() - amountOfHealthLostUponLoss));

                        CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> IHeartCap.refreshHearts(false));
                    }
                }

            }
        }
    }
}
