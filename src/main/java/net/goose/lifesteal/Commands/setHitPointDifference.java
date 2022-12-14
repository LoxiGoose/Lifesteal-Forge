package net.goose.lifesteal.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.Capability.CapabilityRegistry;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
public class setHitPointDifference {

    public setHitPointDifference(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(
                Commands.literal("setHitPointDifference")
                        .requires((commandSource) -> {return commandSource.hasPermission(2);})
                        .then(Commands.argument("Player", EntityArgument.entity())
                                .then(Commands.argument("Amount", IntegerArgumentType.integer()).executes((command) -> {
                                    return setHitPoint(command.getSource(), EntityArgument.getEntity(command, "Player"), IntegerArgumentType.getInteger(command, "Amount"));}
                                ))));
    }

    private int setHitPoint(CommandSource source, Entity chosenentity, int amount) throws CommandSyntaxException{

        String sourceTextName = source.getTextName();

        CapabilityRegistry.getHeart(chosenentity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(amount));
        CapabilityRegistry.getHeart(chosenentity).ifPresent(IHeartCap::refreshHearts);

        if(sourceTextName.matches("Server")){
            LifeSteal.LOGGER.info("Set "+ chosenentity.getName().getString() +"'s HitPoint difference to "+amount);
        }else{
            LivingEntity playerthatsentcommand = source.getPlayerOrException();

            if(chosenentity != playerthatsentcommand){
                playerthatsentcommand.sendMessage(ITextComponent.nullToEmpty("Set "+ chosenentity.getName().getString() +"'s HitPoint difference to "+amount), playerthatsentcommand.getUUID());
            }
        }

        chosenentity.sendMessage(ITextComponent.nullToEmpty("Your HitPoint difference has been set to "+amount), chosenentity.getUUID());
        return 1;
    }
}