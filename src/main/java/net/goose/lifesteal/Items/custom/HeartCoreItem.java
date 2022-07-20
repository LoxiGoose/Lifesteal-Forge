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

public class HeartCoreItem extends Item {

    public static final Capability<IHeartCap> HEART_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public HeartCoreItem(Properties pProperties){
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand Hand) {

        if(!level.isClientSide()){

            if(!ConfigHolder.SERVER.disableHeartCores.get()){

                ItemStack itemStackheld = player.getItemInHand(Hand);

                float MaxHealth = player.getMaxHealth();

                player.heal((float) (MaxHealth * 0.35));

                itemStackheld.shrink(1);

            }else{
                player.sendSystemMessage(Component.translatable("Heart Cores have been disabled in the configurations."));
            }
        }

        return super.use(level, player, Hand);
    }
}
