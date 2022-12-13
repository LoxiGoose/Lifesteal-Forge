package net.goose.lifesteal.Items.custom;

import net.goose.lifesteal.Configurations.ConfigHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class HeartCoreItem extends Item {

    public static final FoodProperties HeartCore = (new FoodProperties.Builder()).alwaysEat().build();

    public HeartCoreItem(Properties pProperties){
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack item, Level level, LivingEntity entity) {

        if(!level.isClientSide() && entity instanceof ServerPlayer serverPlayer){

            if(!ConfigHolder.SERVER.disableHeartCores.get() && entity.getHealth() < entity.getMaxHealth()){

                float MaxHealth = entity.getMaxHealth();
                float AmountThatWillBeHealed = (float) (MaxHealth * ConfigHolder.SERVER.HeartCoreHeal.get());

                int TickTime = (int) (AmountThatWillBeHealed * 50) / 2;
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, TickTime, 1));

            }else{

                if(ConfigHolder.SERVER.disableHeartCores.get()){
                    entity.sendSystemMessage(Component.translatable("Heart Cores have been disabled in the configurations."));
                        item.shrink(-1);
                        serverPlayer.containerMenu.broadcastChanges();
                }else{
                    entity.sendSystemMessage(Component.translatable("You are already at max health."));
                        item.shrink(-1);
                        serverPlayer.containerMenu.broadcastChanges();
                }

            }
        }
        return super.finishUsingItem(item, level, entity);
    }
}
