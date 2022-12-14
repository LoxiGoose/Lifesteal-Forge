package net.goose.lifesteal.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.Capability.CapabilityRegistry;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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

        LivingEntity playerthatsentcommand = source.getPlayer();

        if(!source.isPlayer()){
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> LifeSteal.LOGGER.info(chosenentity.getName().getString() +"'s HitPoint difference is "+ HeartCap.getHeartDifference() + "."));
        }else{
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> playerthatsentcommand.sendSystemMessage(Component.translatable(chosenentity.getName().getString() +"'s HitPoint difference is "+ HeartCap.getHeartDifference() + ".")));
        }


        return 1;
    }
}
