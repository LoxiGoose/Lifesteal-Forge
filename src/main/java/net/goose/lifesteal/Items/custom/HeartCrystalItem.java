package net.goose.lifesteal.Items.custom;

import net.goose.lifesteal.Configurations.ConfigHolder;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class HeartCrystalItem extends Item {

    public static final Capability<IHeartCap> HEART_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public HeartCrystalItem(Properties pProperties){
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand Hand) {

        if(!level.isClientSide()){

            if(!ConfigHolder.SERVER.disableHeartCrystals.get()){
                player.getCapability(HEART_CAP_CAPABILITY).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + ConfigHolder.SERVER.amountOfHealthLostUponLoss.get()));

                player.getCapability(HEART_CAP_CAPABILITY).ifPresent(IHeartCap::refreshHearts);

                player.getInventory().removeItem(player.getItemInHand(Hand));
            }else{
                player.sendSystemMessage(Component.translatable("Heart Crystals have been disabled in the configurations."));
            }
        }

        return super.use(level, player, Hand);
    }
}
