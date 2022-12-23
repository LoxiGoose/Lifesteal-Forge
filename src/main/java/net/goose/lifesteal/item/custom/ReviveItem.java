package net.goose.lifesteal.item.custom;

import net.goose.lifesteal.LifeSteal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.UserBanList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ReviveItem extends Item{
    public ReviveItem(Item.Properties pProperties){
        super(pProperties);
    }
    @Override
    public InteractionResult useOn(UseOnContext context){
        if(!context.getLevel().isClientSide){
            BlockPos positionClicked = context.getClickedPos();
            Block block = context.getLevel().getBlockState(positionClicked).getBlock();
            Component playerName = context.getItemInHand().getHoverName();

            UserBanList userBanList = context.getLevel().getServer().getPlayerList().getBans();
            String[] strings = userBanList.getUserList();

            for(String string : strings){
                LifeSteal.LOGGER.info(string);
            }

            LifeSteal.LOGGER.info(context.getItemInHand() + block.toString());
        }
        return InteractionResult.PASS;
    }

}
