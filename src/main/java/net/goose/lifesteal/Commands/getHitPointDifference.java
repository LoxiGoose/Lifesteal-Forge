package net.goose.lifesteal.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.Capability.CapabilityRegistry;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class getHitPointDifference {
    public getHitPointDifference(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal("getHitPointDifference")
                        .requires((commandSource) -> {return commandSource.hasPermission(2);})
                        .then(Commands.argument("Player", EntityArgument.entity()).executes((command) -> {
                            return getHitPoint(command.getSource(), EntityArgument.getEntity(command, "Player"));}
                        )));
    }

    private int getHitPoint(CommandSourceStack source, Entity chosenentity) throws CommandSyntaxException{

        String sourceTextName = source.getTextName();

        if(sourceTextName.matches("Server")){
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> LifeSteal.LOGGER.info(chosenentity.getName().getString() +"'s HitPoint difference is "+ HeartCap.getHeartDifference() + "."));
        }else{
            ServerPlayer playerthatsentcommand = source.getPlayerOrException();

            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> playerthatsentcommand.sendMessage(Component.nullToEmpty(chosenentity.getName().getString() +"'s HitPoint difference is "+ HeartCap.getHeartDifference() + "."), playerthatsentcommand.getUUID()));
        }


        return 1;
    }
}