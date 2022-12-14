
package net.goose.lifesteal.Commands;

import com.mojang.brigadier.CommandDispatcher;
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
public class getLives {

    public getLives(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(
                Commands.literal("getLives")
                        .requires((commandSource) -> {return commandSource.hasPermission(2);})
                        .then(Commands.argument("Player", EntityArgument.entity()).executes((command) -> {
                            return getLive(command.getSource(), EntityArgument.getEntity(command, "Player"));}
                        )));
    }

    private int getLive(CommandSource source, Entity chosenentity) throws CommandSyntaxException{

        String sourceTextName = source.getTextName();

        if(sourceTextName.matches("Server")){
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> LifeSteal.LOGGER.info(chosenentity.getName().getString() +" has "+ HeartCap.getLives() + " lives."));
        }else{
            LivingEntity playerthatsentcommand = source.getPlayerOrException();
            CapabilityRegistry.getHeart(chosenentity).ifPresent(HeartCap -> playerthatsentcommand.sendMessage(ITextComponent.nullToEmpty(chosenentity.getName().getString() +" has "+ HeartCap.getLives() + " lives."), playerthatsentcommand.getUUID()));
        }


        return 1;
    }
}