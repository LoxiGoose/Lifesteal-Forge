package net.goose.lifesteal.capability;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.block.ModBlocks;
import net.goose.lifesteal.configuration.ConfigHolder;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
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
    public int getHeartDifference() {
        return this.heartDifference;
    }

    @Override
    public void setHeartDifference(int hearts) { if (!livingEntity.level.isClientSide){ this.heartDifference = hearts;}}

    @Override
    public void refreshHearts(boolean healtoMax){

        if(!livingEntity.level.isClientSide){
            AttributeInstance Attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            Set<AttributeModifier> attributemodifiers = Attribute.getModifiers();

            if(maximumheartsGainable > 0){
                if(this.heartDifference - defaultheartDifference >= maximumheartsGainable) {
                    this.heartDifference = maximumheartsGainable + defaultheartDifference;
                    if(LifeSteal.config.tellPlayersIfReachedMaxHearts.get()){
                        livingEntity.sendMessage(new TranslatableComponent("chat.message.lifesteal.reached_max_hearts"), livingEntity.getUUID());
                    }
                }
            }

            if(minimumamountofheartscanlose >= 0){
                if(this.heartDifference < defaultheartDifference - minimumamountofheartscanlose){
                    this.heartDifference = defaultheartDifference - minimumamountofheartscanlose;
                }
            }

            if(!attributemodifiers.isEmpty()){
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

                if(!FoundAttribute){

                    AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                    Attribute.addPermanentModifier(attributeModifier);
                }
            }else{

                AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                Attribute.addPermanentModifier(attributeModifier);
            }

            if(livingEntity.getHealth() > livingEntity.getMaxHealth() || healtoMax){
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }

            if(heartDifference >= 20 && livingEntity instanceof ServerPlayer serverPlayer){
                ModCriteria.GET_10_MAX_HEARTS.trigger(serverPlayer);
            }

            if(livingEntity.getMaxHealth() <= 1 && this.heartDifference <= -20){
                if (livingEntity instanceof ServerPlayer serverPlayer){

                    this.heartDifference = defaultheartDifference;
                    refreshHearts(true);

                    if (!livingEntity.level.getServer().isSingleplayer()) {

                        ItemStack playerHead = new ItemStack(ModBlocks.REVIVE_HEAD.get());
                        CompoundTag skullOwner = new CompoundTag();
                        skullOwner.putString("Name", serverPlayer.getName().getString());
                        skullOwner.putUUID("Id", serverPlayer.getUUID());

                        CompoundTag compoundTag = new CompoundTag();
                        compoundTag.put("SkullOwner", skullOwner);
                        playerHead.setTag(compoundTag);
                        serverPlayer.getInventory().add(playerHead);
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