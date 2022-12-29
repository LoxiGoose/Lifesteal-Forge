package net.goose.lifesteal.item.custom;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.api.ILevelCap;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.capability.HeartCap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.LifeCycle;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ReviveCrystalItem extends Item {
    public ReviveCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        if (!useOnContext.getLevel().isClientSide) {
            Level level = useOnContext.getLevel();
            Player player = useOnContext.getPlayer();

            if(level.getServer().isSingleplayer()){
                player.displayClientMessage(Component.translatable("gui.lifesteal.singleplayer"), true);
                return super.useOn(useOnContext);
            }

            ItemStack itemStack = useOnContext.getItemInHand();
            BlockPos blockPos = useOnContext.getClickedPos();
            Block block = level.getBlockState(blockPos).getBlock();

            if (block == Blocks.PLAYER_HEAD || block == Blocks.PLAYER_WALL_HEAD) {
                BlockEntity blockEntity = level.getBlockEntity(blockPos);
                CompoundTag compoundTag = blockEntity.getUpdateTag();

                GameProfile gameprofile;
                if (compoundTag != null) {
                    if (compoundTag.contains("SkullOwner", 10)) {
                        gameprofile = NbtUtils.readGameProfile(compoundTag.getCompound("SkullOwner"));
                    } else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
                        gameprofile = new GameProfile(null, compoundTag.getString("SkullOwner"));
                    } else {
                        gameprofile = null;
                    }
                } else {
                    gameprofile = null;
                }
                if (gameprofile != null) {
                    UserBanList userBanList = level.getServer().getPlayerList().getBans();

                    if (userBanList.isBanned(gameprofile)) {
                        level.removeBlock(blockPos, true);
                        Entity entity = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                        entity.setPos(blockPos.getCenter());
                        level.addFreshEntity(entity);
                        userBanList.remove(gameprofile);
                        itemStack.shrink(1);

                        CapabilityRegistry.getLevel(level).ifPresent(ILevelCap ->
                                ILevelCap.setBannedUUIDanditsBlockPos(gameprofile.getId(), blockPos));

                        if(!LifeSteal.config.silentlyRevivePlayer.get()){
                            MutableComponent mutableComponent = Component.translatable("chat.message.lifesteal.revived_player");
                            MutableComponent mutableComponent1 = Component.literal(gameprofile.getName());
                            String combinedMessage = ChatFormatting.YELLOW + mutableComponent1.getString() + mutableComponent.getString();
                            PlayerList playerlist = level.getServer().getPlayerList();
                            List<ServerPlayer> playerList = playerlist.getPlayers();
                            for (ServerPlayer serverPlayer : playerList) {
                                serverPlayer.getCamera().sendSystemMessage(Component.literal(combinedMessage));
                            }
                        }else{
                            player.displayClientMessage(Component.translatable("gui.lifesteal.unbanned"), true);
                        }
                    }else{
                        player.displayClientMessage(Component.translatable("gui.lifesteal.already_unbanned"), true);
                    }
                }
            }else{
                player.displayClientMessage(Component.translatable("gui.lifesteal.invaild_revive_block"), true);
            }
        }
        return super.useOn(useOnContext);
    }
}
