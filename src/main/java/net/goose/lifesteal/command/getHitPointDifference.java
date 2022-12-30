package net.goose.lifesteal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class getHitPointDifference {

    public getHitPointDifference(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(
                Commands.literal("getHitPointDifference")
                        .requires((commandSource) -> {return commandSource.hasPermission(2);})
                        .then(Commands.argument("Player", EntityArgument.entity()).executes((command) -> {
                            return getHitPoint(command.getSource(), EntityArgument.getEntity(command, "Player"));}
                        )));
    }

    private int getHitPoint(CommandSource source, Entity chosenentity) throws CommandSyntaxException{

        String sourceTextName = source.getTextName();

        if(sourceTextName.matches("Server")){
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> LifeSteal.LOGGER.info(chosenentity.getName().getString() +"'s HitPoint difference is "+ HeartCap.getHeartDifference() + "."));
        }else{
            ServerPlayerEntity playerthatsentcommand = source.getPlayerOrException();

            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> playerthatsentcommand.sendMessage(ITextComponent.nullToEmpty(chosenentity.getName().getString() +"'s HitPoint difference is "+ HeartCap.getHeartDifference() + "."), playerthatsentcommand.getUUID()));
        }


        return 1;
    }
}