package net.goose.lifesteal.capability;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.api.IHeartCap;
import net.goose.lifesteal.api.ILevelCap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HeartCap implements IHeartCap {
    private final LivingEntity livingEntity;

    private final int defaultheartDifference = LifeSteal.config.startingHeartDifference.get();
    private int heartDifference = defaultheartDifference;

    private final int maximumheartsGainable = LifeSteal.config.maximumamountofheartsGainable.get();
    private final int minimumamountofheartscanlose = LifeSteal.config.maximumamountofheartsLoseable.get();

    public HeartCap(@Nullable final LivingEntity entity) {
        this.livingEntity = entity;
    }

    @Override
    public void revivedTeleport(ServerLevel level, ILevelCap iLevelCap, boolean synchronize) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            if (!level.isClientSide) {
                HashMap hashMap = iLevelCap.getMap();
                BlockPos blockPos = (BlockPos) hashMap.get(livingEntity.getUUID());

                if (blockPos != null) {
                    iLevelCap.removeUUIDanditsBlockPos(livingEntity.getUUID(), blockPos);
                    if (serverPlayer.getLevel() == level) {
                        serverPlayer.connection.teleport(blockPos.getX(), blockPos.getY(), blockPos.getZ(), serverPlayer.getXRot(), serverPlayer.getYRot());
                    } else {
                        serverPlayer.teleportTo(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), serverPlayer.getXRot(), serverPlayer.getYRot());
                    }
                    if (serverPlayer.isSpectator()) {
                        serverPlayer.setGameMode(GameType.SURVIVAL);
                    }
                    int tickTime = 600;
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, tickTime, 3));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, tickTime, 3));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, tickTime, 3));
                    if (synchronize) {
                        serverPlayer.jumpFromGround();
                    }
                    ModCriteria.REVIVED.trigger(serverPlayer);
                }
            }
        }
    }

    @Override
    public void revivedTeleport(ServerLevel level, ILevelCap iLevelCap) {
        revivedTeleport(level, iLevelCap, true);
    }

    @Override
    public void spawnPlayerHead(ServerPlayer serverPlayer) {
        Level level = serverPlayer.level;
        BlockPos blockPos = serverPlayer.blockPosition();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (blockEntity != null) {
            blockEntity.setRemoved();
        }
        final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
        BlockState playerHeadState = Blocks.PLAYER_HEAD.defaultBlockState().setValue(ROTATION, Integer.valueOf(Mth.floor((double) ((180.0F + serverPlayer.getYRot()) * 16.0F / 360.0F) + 0.5) & 15));
        level.setBlockAndUpdate(blockPos, playerHeadState);
        SkullBlockEntity playerHeadEntity = new SkullBlockEntity(blockPos, playerHeadState);
        playerHeadEntity.setOwner(serverPlayer.getGameProfile());
        level.setBlockEntity(playerHeadEntity);
    }

    @Override
    public int getHeartDifference() {
        return this.heartDifference;
    }

    @Override
    public void setHeartDifference(int hearts) {
        if (!livingEntity.level.isClientSide) {
            this.heartDifference = hearts;
        }
    }

    @Override
    public void refreshHearts(boolean healtoMax) {

        if (!livingEntity.level.isClientSide) {
            AttributeInstance Attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            Set<AttributeModifier> attributemodifiers = Attribute.getModifiers();

            if (maximumheartsGainable > 0) {
                if (this.heartDifference - defaultheartDifference >= maximumheartsGainable) {
                    this.heartDifference = maximumheartsGainable + defaultheartDifference;
                    if (LifeSteal.config.tellPlayersIfReachedMaxHearts.get()) {
                        livingEntity.sendMessage(new TranslatableComponent("chat.message.lifesteal.reached_max_hearts"), livingEntity.getUUID());
                    }
                }
            }

            if (minimumamountofheartscanlose >= 0) {
                if (this.heartDifference < defaultheartDifference - minimumamountofheartscanlose) {
                    this.heartDifference = defaultheartDifference - minimumamountofheartscanlose;
                }
            }

            if (!attributemodifiers.isEmpty()) {
                Iterator<AttributeModifier> attributeModifierIterator = attributemodifiers.iterator();

                boolean FoundAttribute = false;

                while (attributeModifierIterator.hasNext()) {

                    AttributeModifier attributeModifier = attributeModifierIterator.next();
                    if (attributeModifier != null && attributeModifier.getName().equals("LifeStealHealthModifier")) {
                        FoundAttribute = true;

                        Attribute.removeModifier(attributeModifier);

                        AttributeModifier newmodifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                        Attribute.addPermanentModifier(newmodifier);
                    }
                }

                if (!FoundAttribute) {

                    AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                    Attribute.addPermanentModifier(attributeModifier);
                }
            } else {

                AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                Attribute.addPermanentModifier(attributeModifier);
            }

            if (livingEntity.getHealth() > livingEntity.getMaxHealth() || healtoMax) {
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }

            if (heartDifference >= 20 && livingEntity instanceof ServerPlayer serverPlayer) {
                ModCriteria.GET_10_MAX_HEARTS.trigger(serverPlayer);
            }

            if (livingEntity.getMaxHealth() <= 1 && this.heartDifference <= -20) {
                if (livingEntity instanceof ServerPlayer serverPlayer) {

                    this.heartDifference = defaultheartDifference;
                    refreshHearts(true);

                    if (!livingEntity.level.getServer().isSingleplayer()) {

                        spawnPlayerHead(serverPlayer);
                        serverPlayer.getInventory().dropAll();

                        @Nullable Component component = new TranslatableComponent("bannedmessage.lifesteal.lost_max_hearts");
                        UserBanList userbanlist = serverPlayer.getServer().getPlayerList().getBans();
                        serverPlayer.getGameProfile();
                        GameProfile gameprofile = serverPlayer.getGameProfile();

                        UserBanListEntry userbanlistentry = new UserBanListEntry(gameprofile, null, "Lifesteal", null, component == null ? null : component.getString());
                        userbanlist.add(userbanlistentry);

                        if (serverPlayer != null) {
                            serverPlayer.connection.disconnect(new TranslatableComponent("bannedmessage.lifesteal.lost_max_hearts"));
                        }
                    } else if (serverPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                        serverPlayer.setGameMode(GameType.SPECTATOR);

                        livingEntity.sendMessage(new TranslatableComponent("chat.message.lifesteal.lost_max_hearts"), livingEntity.getUUID());
                    }
                }
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("heartdifference", getHeartDifference());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setHeartDifference(tag.getInt("heartdifference"));
    }
}