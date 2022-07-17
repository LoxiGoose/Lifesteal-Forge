package net.goose.lifesteal.Configurations;

import io.netty.util.Attribute;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public final ForgeConfigSpec.IntValue AmountOfLives;
    public final ForgeConfigSpec.IntValue StartingHeartDifference;

    public Config(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        this.StartingHeartDifference = buildInt(builder, "Starting Heart Difference:", "all", 0, -19, Integer.MAX_VALUE, "This value modifies how many hearts you'll start at in a world. 2 would mean 1 extra heart, -2 would mean 1 less heart. If you have lives enabled, you'll gain a life when you get max hearts double your starting hearts. EX: If 3 hearts is your starting value, you'll gain a life if you get 3 more hearts. ");
        this.AmountOfLives = buildInt(builder, "AmountOfLifes:", "all", -1, -1, Integer.MAX_VALUE,  "When a player loses all their max hearts, they will lose a life and get 20 more max hearts. When they lose all their lives, they permanently die. You can gain a life if your max hearts are double your starting hearts. Setting this to -1 will disable this feature.");

    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, String catagory, int defaultValue, int min, int max, String comment){
        return (ForgeConfigSpec.IntValue) builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, String catagory, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

}


