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

    public HeartCap(@Nullable final LivingEntity entity) {
        this.livingEntity = entity;
    }

    @Override
    public int getHeartDifference() {
        System.out.println(livingEntity.getName().getString()+"'s current heart difference is "+HeartDifference);

        return HeartDifference;
    }

    @Override
    public void setHeartDifference(int hearts) {
        this.HeartDifference = hearts;
        System.out.println(livingEntity.getName().getString()+"'s heart difference is now "+HeartDifference);
        refreshhearts();
    }

    @Override
    public void refreshhearts(){
        System.out.println("Refreshing "+livingEntity.getName().getString()+"'s Hearts.");
        var Attribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        Set<AttributeModifier> attributemodifiers = Attribute.getModifiers();

        if(!attributemodifiers.isEmpty()){
            Iterator<AttributeModifier> attributeModifierIterator = attributemodifiers.iterator();

            boolean FoundAttribute = false;

            while (attributeModifierIterator.hasNext()) {

                AttributeModifier attributeModifier = attributeModifierIterator.next();
                if (attributeModifier != null && attributeModifier.getName().equals("LifeStealHealthModifier")) {
                    FoundAttribute = true;
                    System.out.println("Found "+attributeModifier.getName());
                    Attribute.removeModifier(attributeModifier);

                    AttributeModifier newmodifier = new AttributeModifier("LifeStealHealthModifier", HeartDifference, AttributeModifier.Operation.ADDITION);

                    Attribute.addPermanentModifier(newmodifier);
                }
            }

            if(FoundAttribute == false){
                System.out.println("No attributes found");
                AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", HeartDifference, AttributeModifier.Operation.ADDITION);

                Attribute.addPermanentModifier(attributeModifier);
            }
        }else{
            System.out.println("No attributes found");
            AttributeModifier attributeModifier = new AttributeModifier("LifeStealHealthModifier", HeartDifference, AttributeModifier.Operation.ADDITION);

            Attribute.addPermanentModifier(attributeModifier);
        }
        System.out.println("Checking MaxHealth and HeartDifference to determine if Player has lost all hearts");
        System.out.println(livingEntity.getMaxHealth());
        System.out.println(HeartDifference);
        if(livingEntity.getMaxHealth() <= 1 && HeartDifference <= -20){

            if (livingEntity instanceof ServerPlayer serverPlayer){
                if(serverPlayer.gameMode.getGameModeForPlayer() != GameType.SPECTATOR){
                    serverPlayer.gameMode.changeGameModeForPlayer(GameType.SPECTATOR);
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
