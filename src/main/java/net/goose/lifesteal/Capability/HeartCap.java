package net.goose.lifesteal.Capability;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.Configurations.ConfigHolder;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class HeartCap implements IHeartCap {
    private final LivingEntity livingEntity;

    private final int defaultheartDifference = ConfigHolder.SERVER.startingHeartDifference.get();
    private int heartDifference = defaultheartDifference;

    private final int maximumheartsGainable = ConfigHolder.SERVER.maximumamountofheartsgainable.get();
    private final int minimumamountofheartscanlose = ConfigHolder.SERVER.minimumamountofheartscanlose.get();
    private final int defaultLives = ConfigHolder.SERVER.amountOfLives.get();
    private int lives = defaultLives;

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
    public void refreshHearts(){

        if(!livingEntity.level.isClientSide){
            AttributeInstance Attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            Set<AttributeModifier> attributemodifiers = Attribute.getModifiers();

            if(maximumheartsGainable > 0){
                if(this.heartDifference - defaultheartDifference >= maximumheartsGainable) {
                    this.heartDifference = maximumheartsGainable + defaultheartDifference;

                    livingEntity.sendMessage(Component.nullToEmpty("You have reached max hearts."), livingEntity.getUUID());
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

            if(livingEntity.getHealth() > livingEntity.getMaxHealth()){
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }

            if(livingEntity.getMaxHealth() <= 1 && this.heartDifference <= -20){

                if(defaultLives > 0 && maximumheartsGainable <= 0 && minimumamountofheartscanlose < 0){
                    if(this.lives <= 0){

                        this.heartDifference = defaultheartDifference;
                        this.lives = defaultLives;

                        refreshHearts();

                        if (livingEntity instanceof ServerPlayer serverPlayer){
                            if(ConfigHolder.SERVER.bannedUponLosingAllHeartsOrLives.get()){

                                @Nullable Component component = Component.nullToEmpty("You have lost all your lives and max hearts, you are now permanently banned till further notice.");

                                UserBanList userbanlist = serverPlayer.getServer().getPlayerList().getBans();

                                serverPlayer.getGameProfile();

                                GameProfile gameprofile = serverPlayer.getGameProfile();

                                UserBanListEntry userbanlistentry = new UserBanListEntry(gameprofile, (Date)null, "Lifesteal", (Date)null, component == null ? null : component.getString());
                                userbanlist.add(userbanlistentry);

                                if (serverPlayer != null) {
                                    serverPlayer.connection.disconnect(Component.nullToEmpty("You have lost all your lives and max hearts, you are now permanently banned till further notice."));
                                }
                            }else if(serverPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR){
                                serverPlayer.setGameMode(GameType.SPECTATOR);

                                livingEntity.sendMessage(Component.nullToEmpty("You have lost all your lives and max hearts. You are now permanently dead."), livingEntity.getUUID());
                            }
                        }
                    }else{
                        this.lives--;

                        this.heartDifference = defaultheartDifference;
                        refreshHearts();

                        livingEntity.sendMessage(Component.nullToEmpty("You have lost a life. Your lives count is now "+ this.lives), livingEntity.getUUID());
                    }
                }else{
                    if (livingEntity instanceof ServerPlayer serverPlayer){

                        this.heartDifference = defaultheartDifference;
                        this.lives = defaultLives;

                        refreshHearts();

                        if(ConfigHolder.SERVER.bannedUponLosingAllHeartsOrLives.get()){

                            @Nullable Component component = Component.nullToEmpty("You have lost all max hearts, you are now permanently banned till further notice.");

                            UserBanList userbanlist = serverPlayer.getServer().getPlayerList().getBans();

                            serverPlayer.getGameProfile();

                            GameProfile gameprofile = serverPlayer.getGameProfile();

                            UserBanListEntry userbanlistentry = new UserBanListEntry(gameprofile, (Date)null, "Lifesteal", (Date)null, component == null ? null : component.getString());
                            userbanlist.add(userbanlistentry);

                            if (serverPlayer != null) {
                                serverPlayer.connection.disconnect(Component.nullToEmpty("You have lost all max hearts, you are now permanently banned till further notice."));
                            }
                        }else if(serverPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR){
                            serverPlayer.setGameMode(GameType.SPECTATOR);

                            livingEntity.sendMessage(Component.nullToEmpty("You have lost all max hearts, you are now permanently dead."), livingEntity.getUUID());
                        }
                    }
                }

            }else if(this.heartDifference + 20 >= (defaultheartDifference + 20) * 2 && defaultLives > 0 && maximumheartsGainable < 0 && minimumamountofheartscanlose
 <= 0 ){
                this.lives++;

                this.heartDifference = defaultheartDifference;

                livingEntity.sendMessage(Component.nullToEmpty("Your lives count has increased to "+ this.lives), livingEntity.getUUID());
                refreshHearts();
            }
        }

    }

    @Override
    public int getLives() {
        return this.lives;
    }

    @Override
    public void setLives(int lives) {if(!livingEntity.level.isClientSide){this.lives = lives;}}

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("heartdifference", getHeartDifference());
        tag.putInt("lives", getLives());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        setHeartDifference(tag.getInt("heartdifference"));
        setLives(tag.getInt("lives"));
    }
}