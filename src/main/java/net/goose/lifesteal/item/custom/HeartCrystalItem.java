package net.goose.lifesteal.item.custom;

import net.goose.lifesteal.Configurations.ConfigHolder;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class HeartCrystalItem extends Item {

    public static final FoodProperties HeartCrystal = (new FoodProperties.Builder()).alwaysEat().build();

    public static final Capability<IHeartCap> HEART_CAP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    public HeartCrystalItem(Properties pProperties){
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack item, Level level, LivingEntity entity) {

        if(!level.isClientSide() && entity instanceof ServerPlayer serverPlayer){

            if(!ConfigHolder.SERVER.disableHeartCrystals.get()){

                serverPlayer.getCapability(HEART_CAP_CAPABILITY).ifPresent(newHeartDifference -> newHeartDifference.setHeartDifference(newHeartDifference.getHeartDifference() + ConfigHolder.SERVER.HeartCrystalAmountGain.get()));

                serverPlayer.getCapability(HEART_CAP_CAPABILITY).ifPresent(IHeartCap::refreshHearts);

                // Formula, for every hit point, increase duration of the regeneration by 50 ticks: TickDuration = MaxHealth * 50
                int tickTime = (int) (serverPlayer.getMaxHealth() * 50) / 4;
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, tickTime, 3));

            }else{
                serverPlayer.displayClientMessage(Component.translatable("Heart Crystals have been disabled in the configurations"), true);
                    item.shrink(-1);
                    serverPlayer.containerMenu.broadcastChanges();

            }
        }
        return super.finishUsingItem(item, level, entity);
    }
}
