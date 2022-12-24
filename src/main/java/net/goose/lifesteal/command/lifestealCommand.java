package net.goose.lifesteal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHeartCap;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.item.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

public class lifestealCommand {
    public lifestealCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ls")
                        .then(Commands.literal("withdraw")
                                .requires((commandSource) -> commandSource.hasPermission(Commands.LEVEL_ALL))
                                .executes((command) -> withdraw(command.getSource(), 1))
                                .then(Commands.argument("Amount", IntegerArgumentType.integer())
                                        .executes((command) -> withdraw(command.getSource(), IntegerArgumentType.getInteger(command, "Amount")))))
                        .then(Commands.literal("get-hitpoints")
                                .requires((commandSource) -> commandSource.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .executes((command) -> getHitPoint(command.getSource()))
                                .then(Commands.argument("Player", EntityArgument.entity())
                                        .executes((command) -> getHitPoint(command.getSource(), EntityArgument.getEntity(command, "Player"))))
                        )
                        .then(Commands.literal("set-hitpoints")
                                .requires((commandSource) -> commandSource.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.argument("Amount", IntegerArgumentType.integer())
                                        .executes((command) -> setHitPoint(command.getSource(), IntegerArgumentType.getInteger(command, "Amount"))))
                                .then(Commands.argument("Player", EntityArgument.entity())
                                        .then(Commands.argument("Amount", IntegerArgumentType.integer())
                                                .executes((command) -> setHitPoint(command.getSource(), EntityArgument.getEntity(command, "Player"), IntegerArgumentType.getInteger(command, "Amount")))))));
    }

    private int withdraw(CommandSourceStack source, int amount) throws CommandSyntaxException {
        if (source.isPlayer()) {
            final int maximumheartsLoseable = LifeSteal.config.maximumamountofheartsLoseable.get();
            final int startingHitPointDifference = LifeSteal.config.startingHeartDifference.get();

            LivingEntity playerthatsentcommand = source.getPlayer();
            if (playerthatsentcommand instanceof Player player) {
                String advancementUsed = (String) LifeSteal.config.advancementUsedForWithdrawing.get();

                if (source.getPlayer().getAdvancements().getOrStartProgress(Advancement.Builder.advancement().build(new ResourceLocation(advancementUsed))).isDone() || advancementUsed.isEmpty()) {
                    AtomicInteger heartDifference = new AtomicInteger();
                    CapabilityRegistry.getHeart(playerthatsentcommand).ifPresent(HeartCap -> heartDifference.set(HeartCap.getHeartDifference() - (LifeSteal.config.HeartCrystalAmountGain.get() * amount)));
                    if (maximumheartsLoseable >= 0) {
                        if (heartDifference.get() < startingHitPointDifference - maximumheartsLoseable) {
                            player.displayClientMessage(Component.translatable("gui.lifesteal.can't_withdraw_less_than_minimum"), true);
                            return 1;
                        }
                    }
                    CapabilityRegistry.getHeart(playerthatsentcommand).ifPresent(HeartCap -> HeartCap.setHeartDifference(heartDifference.get()));
                    CapabilityRegistry.getHeart(playerthatsentcommand).ifPresent(IHeartCap::refreshHearts);

                    ItemStack heartCrystal = new ItemStack(ModItems.HEART_CRYSTAL.get(), amount);
                    CompoundTag compoundTag = heartCrystal.getOrCreateTagElement("lifesteal");
                    compoundTag.putBoolean("Fresh", false);
                    heartCrystal.setHoverName(Component.translatable("item.lifesteal.heart_crystal.unnatural"));
                    player.getInventory().add(heartCrystal);
                } else {
                    String text = (String) LifeSteal.config.textUsedForRequirementOnWithdrawing.get();
                    if (!text.isEmpty()) {
                        player.displayClientMessage(Component.literal((String) LifeSteal.config.textUsedForRequirementOnWithdrawing.get()), true);
                    }
                }
            }
        }
        return 1;
    }

    private int getHitPoint(CommandSourceStack source) throws CommandSyntaxException {
        if (source.isPlayer()) {
            LivingEntity playerthatsentcommand = source.getPlayer();
            CapabilityRegistry.getHeart(playerthatsentcommand).ifPresent(HeartCap -> playerthatsentcommand.sendSystemMessage(Component.translatable("Your HitPoint difference is " + HeartCap.getHeartDifference() + ".")));
        }
        return 1;
    }

    private int getHitPoint(CommandSourceStack source, Entity chosenentity) throws CommandSyntaxException {

        LivingEntity playerthatsentcommand = source.getPlayer();

        if (!source.isPlayer()) {
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> LifeSteal.LOGGER.info(chosenentity.getName().getString() + "'s HitPoint difference is " + HeartCap.getHeartDifference() + "."));
        } else {
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> playerthatsentcommand.sendSystemMessage(Component.translatable(chosenentity.getName().getString() + "'s HitPoint difference is " + HeartCap.getHeartDifference() + ".")));
        }
        return 1;
    }

    private int setHitPoint(CommandSourceStack source, int amount) throws CommandSyntaxException {
        if (source.isPlayer()) {
            LivingEntity playerthatsentcommand = source.getPlayer();
            CapabilityRegistry.getHeart(playerthatsentcommand).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(amount));
            CapabilityRegistry.getHeart(playerthatsentcommand).ifPresent(IHeartCap::refreshHearts);

            playerthatsentcommand.sendSystemMessage(Component.translatable("Your HitPoint difference has been set to " + amount));
        }
        return 1;
    }

    private int setHitPoint(CommandSourceStack source, Entity chosenentity, int amount) throws CommandSyntaxException {

        LivingEntity playerthatsentcommand = source.getPlayer();

        CapabilityRegistry.getHeart(chosenentity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(amount));
        CapabilityRegistry.getHeart(chosenentity).ifPresent(IHeartCap::refreshHearts);

        if (chosenentity != playerthatsentcommand && source.isPlayer()) {
            playerthatsentcommand.sendSystemMessage(Component.translatable("Set " + chosenentity.getName().getString() + "'s HitPoint difference to " + amount));
        } else if (!source.isPlayer()) {
            LifeSteal.LOGGER.info("Set " + chosenentity.getName().getString() + "'s HitPoint difference to " + amount);
        }

        if (LifeSteal.config.tellPlayersIfHitPointChanged.get()) {
            chosenentity.sendSystemMessage(Component.translatable("Your HitPoint difference has been set to " + amount));
        }

        chosenentity.sendSystemMessage(Component.translatable("Your HitPoint difference has been set to " + amount));
        return 1;
    }
}
