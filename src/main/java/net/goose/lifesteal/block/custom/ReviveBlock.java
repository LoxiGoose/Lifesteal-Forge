package net.goose.lifesteal.block.custom;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.UUID;

public class ReviveBlock extends SkullBlock {
    public ReviveBlock(Properties pProperties) {
        super(Types.PLAYER,pProperties);
    }
    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
        BlockEntity blockentity = level.getBlockEntity(blockPos);
        if (blockentity instanceof SkullBlockEntity skullblockentity) {
            GameProfile gameprofile = null;
            if (itemStack.hasTag()) {
                CompoundTag compoundtag = itemStack.getTag();
                if (compoundtag.contains("SkullOwner", 10)) {
                    gameprofile = NbtUtils.readGameProfile(compoundtag.getCompound("SkullOwner"));
                } else if (compoundtag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundtag.getString("SkullOwner"))) {
                    gameprofile = new GameProfile((UUID)null, compoundtag.getString("SkullOwner"));
                }
            }
            LifeSteal.LOGGER.info(String.valueOf(gameprofile));
            skullblockentity.setOwner(gameprofile);
        }
    }
}
