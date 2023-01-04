package net.goose.lifesteal.event;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.command.lifestealCommand;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

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
        LivingEntity newPlayer = event.getEntityLiving();
        ServerPlayer serverPlayer = (ServerPlayer) newPlayer;

        newPlayer.getServer().getAllLevels().forEach((level) -> CapabilityRegistry.getLevel(level).ifPresent(ILevelCap ->
                CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap -> IHeartCap.revivedTeleport(level, ILevelCap))));

        CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap -> {
            if (!serverPlayer.getLevel().isClientSide) {
                IHeartCap.refreshHearts(false);
            }
        });
    }

    @SubscribeEvent
    public static void playerCloneEvent(final PlayerEvent.Clone event) {

        boolean wasDeath = event.isWasDeath();

        LivingEntity oldPlayer = event.getOriginal();
        oldPlayer.reviveCaps();
        LivingEntity newPlayer = event.getEntityLiving();

        CapabilityRegistry.getHeart(oldPlayer).ifPresent(oldHeartDifference -> CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap ->
                {
                    IHeartCap.setHeartDifference(oldHeartDifference.getHeartDifference());
                    IHeartCap.refreshHearts(false);
                }
        ));

        if (wasDeath) {
            newPlayer.heal(newPlayer.getMaxHealth());
        }

        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void deathEvent(final LivingDropsEvent event) {

        LivingEntity killedEntity = event.getEntityLiving();

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
                                CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> {
                                    IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                    IHeartCap.refreshHearts(false);
                                });
                            } else {

                                if (!LifeSteal.config.disableHeartLoss.get()) {
                                    if (LifeSteal.config.maximumamountofheartsLoseable.get() > -1) {
                                        if (LifeSteal.config.startingHeartDifference.get() + HeartDifference.get() > -LifeSteal.config.maximumamountofheartsLoseable.get()) {
                                            CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> {
                                                IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                                IHeartCap.refreshHearts(false);
                                            });
                                        } else {
                                            serverPlayer.displayClientMessage(new TranslatableComponent("chat.message.lifesteal.no_more_hearts_to_steal"), true);
                                        }

                                    } else {
                                        CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> {
                                            IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                            IHeartCap.refreshHearts(false);
                                        });
                                    }
                                } else {
                                    CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> {
                                        IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                        IHeartCap.refreshHearts(false);
                                    });
                                }
                            }

                        }

                        if (!LifeSteal.config.disableLifesteal.get()) {
                            if (!LifeSteal.config.loseHeartsOnlyWhenKilledByEntity.get() && LifeSteal.config.loseHeartsOnlyWhenKilledByPlayer.get()) {
                                if (killerEntity instanceof Player) {
                                    CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> {
                                        IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                        IHeartCap.refreshHearts(false);
                                    });
                                }
                            } else {
                                CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> {
                                    IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                    IHeartCap.refreshHearts(false);
                                });
                            }
                        }
                    } else {
                        CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> {
                            IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                            IHeartCap.refreshHearts(false);
                        });
                    }
                } else {
                    if (!LifeSteal.config.loseHeartsOnlyWhenKilledByEntity.get() && !LifeSteal.config.loseHeartsOnlyWhenKilledByPlayer.get()) {
                        CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> {
                            IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                            IHeartCap.refreshHearts(false);
                        });
                    }
                }

            }
        }
    }
}
