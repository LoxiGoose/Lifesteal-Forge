package net.goose.lifesteal.Items.custom;

import net.goose.lifesteal.Configurations.ConfigHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class HeartCoreItem extends Item {

    public static final Food HeartCore = (new Food.Builder()).alwaysEat().build();

    public HeartCoreItem(Properties pProperties){
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack item, World level, LivingEntity entity) {

        if(!level.isClientSide() && entity instanceof ServerPlayerEntity){

            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) entity;

            if(!ConfigHolder.SERVER.disableHeartCores.get() && entity.getHealth() < entity.getMaxHealth()){

                float maxHealth = entity.getMaxHealth();
                float amountThatWillBeHealed = (float) (maxHealth * ConfigHolder.SERVER.HeartCoreHeal.get());
                float differenceInHealth = entity.getMaxHealth() - entity.getHealth();

                if(differenceInHealth <= amountThatWillBeHealed){
                    amountThatWillBeHealed = differenceInHealth;
                }

                int oldDuration = 0;
                if(entity.hasEffect(Effects.REGENERATION)){
                    EffectInstance mobEffect = entity.getEffect(Effects.REGENERATION);

                    oldDuration = mobEffect.getDuration();
                }

                int tickTime = (int) ((amountThatWillBeHealed * 50) / 2) + oldDuration;
                entity.addEffect(new EffectInstance(Effects.REGENERATION, tickTime, 1));
            }else{

                if(ConfigHolder.SERVER.disableHeartCores.get()){
                    serverPlayer.displayClientMessage(ITextComponent.nullToEmpty("Heart Cores have been disabled in the configurations"), true);
                    item.shrink(-1);
                    serverPlayer.containerMenu.broadcastChanges();
                }else{
                    serverPlayer.displayClientMessage(ITextComponent.nullToEmpty("You are already at max health"), true);
                    item.shrink(-1);
                    serverPlayer.containerMenu.broadcastChanges();
                }

            }
        }
        return super.finishUsingItem(item, level, entity);
    }
}