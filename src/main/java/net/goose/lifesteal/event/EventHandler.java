package net.goose.lifesteal.event;

import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.command.lifestealCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingUseTotemEvent;
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
        Player newPlayer = event.getEntity();
        ServerPlayer serverPlayer = (ServerPlayer) newPlayer;
        newPlayer.getServer().getAllLevels().forEach((level) -> CapabilityRegistry.getLevel(level).ifPresent(ILevelCap ->
                CapabilityRegistry.getHeart(newPlayer).ifPresent(IHeartCap ->
                        IHeartCap.revivedTeleport(level, ILevelCap))));

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
        LivingEntity newPlayer = event.getEntity();

        CapabilityRegistry.getHeart(oldPlayer).ifPresent(oldHeartDifference -> CapabilityRegistry.getHeart(newPlayer).ifPresent(newHeartDifference ->
                {
                    newHeartDifference.setHeartDifference(oldHeartDifference.getHeartDifference());
                    newHeartDifference.refreshHearts(false);
                }
        ));

        if (wasDeath) {
            newPlayer.heal(newPlayer.getMaxHealth());
        }

        oldPlayer.invalidateCaps();
    }

    @SubscribeEvent
    public static void totemofUndyingEvent(final LivingUseTotemEvent event) {
        if (!event.isCanceled()) {
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
        final int maximumheartsGainable = LifeSteal.config.maximumamountofheartsGainable.get();
        final int maximumheartsLoseable = LifeSteal.config.maximumamountofheartsLoseable.get();
        final int startingHitPointDifference = LifeSteal.config.startingHeartDifference.get();
        final int amountOfHealthLostUponLossConfig = LifeSteal.config.amountOfHealthLostUponLoss.get();
        final boolean playersGainHeartsifKillednoHeart = LifeSteal.config.playersGainHeartsifKillednoHeart.get();
        final boolean disableLifesteal = LifeSteal.config.disableLifesteal.get();
        final boolean disableHeartLoss = LifeSteal.config.disableHeartLoss.get();
        final boolean loseHeartsOnlyWhenKilledByEntity = LifeSteal.config.loseHeartsOnlyWhenKilledByEntity.get();
        final boolean loseHeartsOnlyWhenKilledByPlayer = LifeSteal.config.loseHeartsOnlyWhenKilledByPlayer.get();

        LivingEntity killedEntity = event.getEntity();

        if (killedEntity instanceof ServerPlayer) {
            if (!killedEntity.isAlive()) {
                AtomicInteger HeartDifference = new AtomicInteger();

                CapabilityRegistry.getHeart(killedEntity).ifPresent(HeartCap -> HeartDifference.set(HeartCap.getHeartDifference()));

                LivingEntity killerEntity = killedEntity.getLastHurtByMob();
                boolean killerEntityIsPlayer = killerEntity instanceof ServerPlayer;
                ServerPlayer serverPlayer = null;
                if (killerEntityIsPlayer) {
                    serverPlayer = (ServerPlayer) killerEntity;
                }

                int amountOfHealthLostUponLoss;

                if (maximumheartsLoseable < 0) {
                    if (20 + HeartDifference.get() - amountOfHealthLostUponLossConfig >= 0 || playersGainHeartsifKillednoHeart) {
                        amountOfHealthLostUponLoss = amountOfHealthLostUponLossConfig;
                    } else {
                        amountOfHealthLostUponLoss = 20 + HeartDifference.get();
                    }
                } else {
                    if (20 + HeartDifference.get() - amountOfHealthLostUponLossConfig >= (20 + startingHitPointDifference) - maximumheartsLoseable || playersGainHeartsifKillednoHeart) {
                        amountOfHealthLostUponLoss = amountOfHealthLostUponLossConfig;
                    } else {
                        amountOfHealthLostUponLoss = HeartDifference.get() + maximumheartsLoseable;
                    }
                }

                if (killerEntity != null) { // IF THERE IS A KILLER ENTITY
                    if (killerEntity != killedEntity) { // IF IT'S NOT THEMSELVES (Shooting themselves with an arrow lol)
                        // EVERYTHING BELOW THIS COMMENT IS CODE FOR MAKING THE KILLER PERSON'S HEART DIFFERENCE GO UP.
                        if (killerEntityIsPlayer && !disableLifesteal) {

                            if (playersGainHeartsifKillednoHeart) {
                                CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> {
                                    IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                    IHeartCap.refreshHearts(false);
                                });
                            } else {

                                if (!disableHeartLoss) {
                                    if (maximumheartsLoseable > -1) {
                                        if (startingHitPointDifference + HeartDifference.get() > -maximumheartsLoseable) {
                                            CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> {
                                                IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                                IHeartCap.refreshHearts(false);
                                            });
                                        } else {
                                            serverPlayer.sendSystemMessage(Component.translatable("chat.message.lifesteal.no_more_hearts_to_steal"));
                                        }

                                    } else {
                                        CapabilityRegistry.getHeart(killerEntity).ifPresent(IHeartCap -> {
                                            IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                            IHeartCap.refreshHearts(false);
                                        });
                                    }
                                } else {
                                    serverPlayer.sendSystemMessage(Component.translatable("chat.message.lifesteal.no_more_hearts_to_steal"));
                                }
                            }

                        }

                        // EVERYTHING BELOW THIS COMMENT IS CODE FOR LOWERING THE KILLED PERSON'S HEART DIFFERENCE IF THERE WAS A KILLER ENTITY
                        if (!disableHeartLoss) {
                            if (loseHeartsOnlyWhenKilledByPlayer && !loseHeartsOnlyWhenKilledByEntity) {
                                if (killerEntityIsPlayer && !disableLifesteal) {
                                    CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> {
                                        IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                                        IHeartCap.refreshHearts(false);
                                    });
                                }
                            } else {
                                if (disableLifesteal) {
                                    if (!killerEntityIsPlayer) {
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
                        }
                    } else if (!disableHeartLoss) { // IF THIS IS THEMSELVES
                        CapabilityRegistry.getHeart(killedEntity).ifPresent(IHeartCap -> {
                            IHeartCap.setHeartDifference(IHeartCap.getHeartDifference() + amountOfHealthLostUponLoss);
                            IHeartCap.refreshHearts(false);
                        });
                    }
                } else {
                    if (!loseHeartsOnlyWhenKilledByEntity && !loseHeartsOnlyWhenKilledByPlayer && !disableHeartLoss) {
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
