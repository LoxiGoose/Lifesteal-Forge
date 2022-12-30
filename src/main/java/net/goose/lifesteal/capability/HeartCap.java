package net.goose.lifesteal.capability;

import com.mojang.authlib.GameProfile;
import net.goose.lifesteal.advancement.LSAdvancementTriggerRegistry;
import net.goose.lifesteal.configuration.ConfigHolder;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.management.BanList;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class HeartCap implements IHeartCap {
    private final LivingEntity livingEntity;

    private final int defaultheartDifference = ConfigHolder.SERVER.startingHeartDifference.get();
    private int heartDifference = defaultheartDifference;

    private final int maximumheartsGainable = ConfigHolder.SERVER.maximumamountofheartsgainable.get();
    private final int minimumamountofheartscanlose = ConfigHolder.SERVER.minimumamountofheartsloseable.get();
    private final int defaultLives = ConfigHolder.SERVER.amountOfLives.get();
    private int lives = defaultLives;

    public HeartCap(){
        this.livingEntity = null;
    }

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
            Set<AttributeModifier> attributemodifiers = livingEntity.getAttribute(Attributes.MAX_HEALTH).getModifiers();

            if(maximumheartsGainable > 0){
                if(this.heartDifference - defaultheartDifference >= maximumheartsGainable) {
                    this.heartDifference = maximumheartsGainable + defaultheartDifference;

                    livingEntity.sendMessage(ITextComponent.nullToEmpty("You have reached max hearts."), livingEntity.getUUID());
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

                        livingEntity.getAttribute(Attributes.MAX_HEALTH).removeModifier(attributeModifier);

                        AttributeModifier newmodifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                        livingEntity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(newmodifier);
                    }
                }

                if(!FoundAttribute){

                    AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                    livingEntity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(attributeModifier);
                }
            }else{

                AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.heartDifference, AttributeModifier.Operation.ADDITION);

                livingEntity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(attributeModifier);
            }

            if(livingEntity.getHealth() > livingEntity.getMaxHealth()){
                livingEntity.setHealth(livingEntity.getMaxHealth());
            }

            if(heartDifference >= 20 && livingEntity instanceof ServerPlayerEntity){
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) livingEntity;
                LSAdvancementTriggerRegistry.GET_10_MAX_HEARTS.trigger(serverPlayer);
            }

            if(livingEntity.getMaxHealth() <= 1 && this.heartDifference <= -20){


                if(defaultLives > 0 && maximumheartsGainable <= 0 && minimumamountofheartscanlose < 0){
                    if(this.lives <= 0){
                        if (livingEntity instanceof ServerPlayerEntity){

                            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) livingEntity;

                            this.heartDifference = defaultheartDifference;
                            this.lives = defaultLives;

                            refreshHearts();

                            if(ConfigHolder.SERVER.bannedUponLosingAllHeartsOrLives.get()){

                                @Nullable ITextComponent component = ITextComponent.nullToEmpty("You have lost all your lives and max hearts, you are now permanently banned till further notice.");

                                BanList userbanlist = serverPlayerEntity.getServer().getPlayerList().getBans();

                                serverPlayerEntity.getGameProfile();

                                GameProfile gameprofile = serverPlayerEntity.getGameProfile();

                                ProfileBanEntry profileBanEntry = new ProfileBanEntry(gameprofile, null, "Lifesteal", null, component == null ? null : component.getString());
                                userbanlist.add(profileBanEntry);

                                if (serverPlayerEntity != null) {
                                    serverPlayerEntity.connection.disconnect(ITextComponent.nullToEmpty("You have lost all your lives and max hearts, you are now permanently banned till further notice."));
                                }
                            }else if(serverPlayerEntity.gameMode.getGameModeForPlayer() != GameType.SPECTATOR){
                                serverPlayerEntity.setGameMode(GameType.SPECTATOR);

                                livingEntity.sendMessage(ITextComponent.nullToEmpty("You have lost all your lives and max hearts. You are now permanently dead."), livingEntity.getUUID());
                            }
                        }
                    }else{
                        this.lives--;

                        this.heartDifference = defaultheartDifference;
                        refreshHearts();

                        livingEntity.sendMessage(ITextComponent.nullToEmpty("You have lost a life. Your lives count is now "+ this.lives), livingEntity.getUUID());
                    }
                }else{
                    if (livingEntity instanceof ServerPlayerEntity){
                        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) livingEntity;

                        this.heartDifference = defaultheartDifference;
                        this.lives = defaultLives;
                        
                        refreshHearts();

                        if(ConfigHolder.SERVER.bannedUponLosingAllHeartsOrLives.get()){

                            @Nullable ITextComponent component = ITextComponent.nullToEmpty("You have lost all max hearts, you are now permanently banned till further notice.");

                            BanList userbanlist = serverPlayerEntity.getServer().getPlayerList().getBans();

                            serverPlayerEntity.getGameProfile();

                            GameProfile gameprofile = serverPlayerEntity.getGameProfile();

                            ProfileBanEntry profileBanEntry = new ProfileBanEntry(gameprofile, null, "Lifesteal", null, component == null ? null : component.getString());
                            userbanlist.add(profileBanEntry);

                            if (serverPlayerEntity != null) {
                                serverPlayerEntity.connection.disconnect(ITextComponent.nullToEmpty("You have lost all max hearts, you are now permanently banned till further notice."));
                            }
                        }else if(serverPlayerEntity.gameMode.getGameModeForPlayer() != GameType.SPECTATOR){
                            serverPlayerEntity.setGameMode(GameType.SPECTATOR);

                            livingEntity.sendMessage(ITextComponent.nullToEmpty("You have lost all max hearts. You are now permanently dead."), livingEntity.getUUID());
                        }
                    }
                }

            }else if(this.heartDifference + 20 >= (defaultheartDifference + 20) * 2 && defaultLives > 0 && maximumheartsGainable < 0 && minimumamountofheartscanlose <= 0 ){
                this.lives++;

                this.heartDifference = defaultheartDifference;

                livingEntity.sendMessage(ITextComponent.nullToEmpty("Your lives count has increased to "+ this.lives), livingEntity.getUUID());
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

    @Nonnull
    @Override
    public CompoundNBT serializeNBT() {

        CompoundNBT tag = new CompoundNBT();

        tag.putInt("heartdifference", getHeartDifference());
        tag.putInt("lives", getLives());
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull final CompoundNBT nbt) {
        setHeartDifference(nbt.getInt("heartdifference"));
        setLives(nbt.getInt("lives"));
    }

}