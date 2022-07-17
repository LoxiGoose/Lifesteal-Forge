package net.goose.lifesteal.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

public class setHitPointDifference {

    public static final Capability<IHeartCap> HEART_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static LazyOptional<IHeartCap> getHeart(final LivingEntity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HEART_CAP_CAPABILITY);
    }

    public static LazyOptional<IHeartCap> getHeart(final Entity entity) {
        if (entity == null)
            return LazyOptional.empty();
        return entity.getCapability(HEART_CAP_CAPABILITY);
    }

    public setHitPointDifference(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(
                Commands.literal("setHitPointDifference")
                .requires((commandSource) -> {return commandSource.hasPermission(2);})
                        .then(Commands.argument("Player", EntityArgument.entity())
                                .then(Commands.argument("Amount", IntegerArgumentType.integer()).executes((command) -> {
                                    return setHitPoint(command.getSource(), EntityArgument.getEntity(command, "Player"), IntegerArgumentType.getInteger(command, "Amount"));}
                                ))));
    }

    private int setHitPoint(CommandSourceStack source, Entity chosenentity, int amount) throws CommandSyntaxException{

        LivingEntity playerthatsentcommand = source.getPlayer();

        getHeart(chosenentity).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(amount));


        Component component = Component.translatable("");

        if(chosenentity != playerthatsentcommand){
            playerthatsentcommand.sendSystemMessage(Component.translatable("Set "+ chosenentity.getName().getString() +"'s HitPoint difference to "+amount, component));
        }

        chosenentity.sendSystemMessage(Component.translatable("Your HitPoint difference has been set to "+amount, component));
        getHeart(chosenentity).ifPresent(IHeartCap::refreshHearts);
        return 1;
    }
}
