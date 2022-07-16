package net.goose.lifesteal.Capability;

import com.mojang.realmsclient.dto.RealmsWorldOptions;
import net.goose.lifesteal.api.IHeartCap;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.apache.logging.log4j.core.jmx.Server;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class HeartCap implements IHeartCap {
    private final LivingEntity livingEntity;
    private int HeartDifference = 0;
    private int Lives = 1;
    private boolean ToggleLives = false;

    public HeartCap(@Nullable final LivingEntity entity) {
        this.livingEntity = entity;
    }

    @Override
    public int getHeartDifference() {
        return this.HeartDifference;
    }

    @Override
    public void setHeartDifference(int hearts) {this.HeartDifference = hearts;}

    @Override
    public void refreshhearts(){

        var Attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        Set<AttributeModifier> attributemodifiers = Attribute.getModifiers();

        if(!attributemodifiers.isEmpty()){
            Iterator<AttributeModifier> attributeModifierIterator = attributemodifiers.iterator();

            boolean FoundAttribute = false;

            while (attributeModifierIterator.hasNext()) {

                AttributeModifier attributeModifier = attributeModifierIterator.next();
                if (attributeModifier != null && attributeModifier.getName().equals("LifeStealHealthModifier")) {
                    FoundAttribute = true;

                    Attribute.removeModifier(attributeModifier);

                    AttributeModifier newmodifier = new AttributeModifier("LifeStealHealthModifier", this.HeartDifference, AttributeModifier.Operation.ADDITION);

                    Attribute.addPermanentModifier(newmodifier);
                }
            }

            if(FoundAttribute == false){
                AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.HeartDifference, AttributeModifier.Operation.ADDITION);

                Attribute.addPermanentModifier(attributeModifier);
            }
        }else{
            AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", this.HeartDifference, AttributeModifier.Operation.ADDITION);

            Attribute.addPermanentModifier(attributeModifier);
        }

        if(livingEntity.getMaxHealth() <= 1 && this.HeartDifference <= -20){

            if(ToggleLives){
                if(this.Lives <= 0){
                    if (livingEntity instanceof ServerPlayer serverPlayer){
                        if(serverPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR){
                            serverPlayer.gameMode.changeGameModeForPlayer(GameType.SPECTATOR);

                            Component component = Component.translatable("");
                            livingEntity.sendSystemMessage(Component.translatable("You have lost all your lives and max hearts. You are now permanently dead.", component));
                        }
                    }
                }else{
                    this.Lives--;

                    this.HeartDifference = 0;
                    refreshhearts();

                    Component component = Component.translatable("");
                    livingEntity.sendSystemMessage(Component.translatable("You have lost a life. Your lives count is now "+ this.Lives, component));
                }
            }else{
                if (livingEntity instanceof ServerPlayer serverPlayer){
                    if(serverPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR){
                        serverPlayer.gameMode.changeGameModeForPlayer(GameType.SPECTATOR);

                        Component component = Component.translatable("");
                        livingEntity.sendSystemMessage(Component.translatable("You have lost all max hearts, you are now permanently dead.", component));
                    }
                }
            }


        }else if(livingEntity.getMaxHealth() >= 40 && this.HeartDifference >= 20 && ToggleLives ){
            this.Lives++;

            this.HeartDifference = 0;
            refreshhearts();

            Component component = Component.translatable("");
            livingEntity.sendSystemMessage(Component.translatable("You have earned an extra life. Your lives count is now "+ this.Lives, component));
        }

    }

    @Override
    public int getLives() {
        return this.Lives;
    }

    @Override
    public void setLives(int lives) {this.Lives = lives;}

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
