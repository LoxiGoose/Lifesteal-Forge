package net.goose.lifesteal.Configurations;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public final ForgeConfigSpec.IntValue amountOfLives;
    public final ForgeConfigSpec.IntValue startingHeartDifference;
    public final ForgeConfigSpec.BooleanValue shouldAllMobsGiveHearts;
    public final ForgeConfigSpec.BooleanValue loseHeartsOnlyWhenKilledByPlayer;
    public final ForgeConfigSpec.IntValue amountOfHealthLostUponLoss;
    public final ForgeConfigSpec.IntValue maximumamountofheartsgainable;

    //public final ForgeConfigSpec.BooleanValue bannedUponLosingAllHeartsOrLives;

    public Config(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        this.startingHeartDifference = buildInt(builder, "Starting Heart Difference:",  0, -19, Integer.MAX_VALUE, "This value modifies how many hearts you'll start at in a world. 2 would mean 1 extra heart, -2 would mean 1 less heart. If you have lives enabled, you'll gain a life when you get max hearts double your starting hearts. EX: If 3 hearts is your starting value, you'll gain a life if you get 3 more hearts. ");
        this.amountOfLives = buildInt(builder, "Amount of Lives:",  -1, -1, Integer.MAX_VALUE,  "When a player loses all their max hearts, they will lose a life and their max hearts will reset. When they lose all their lives, they permanently die. You can gain a life if your max hearts are double your starting hearts. Setting this to -1 will disable this feature.");
        this.shouldAllMobsGiveHearts = buildBoolean(builder, "Killing any Mobs Gives Hearts:",  false, "When this is false, you can only gain hearts from killing players. Otherwise, any mob will give you hearts.");
        this.loseHeartsOnlyWhenKilledByPlayer = buildBoolean(builder, "Lose Hearts Only When Killed By a Player:",  false, "When this is false, you will lose hearts when killed by anything. Otherwise, you can only lose max hearts when killed by a player.");
        this.amountOfHealthLostUponLoss =  buildInt(builder, "Amount of HitPoints/Health Lost/Given Upon Death/Kill:",  2, 1, Integer.MAX_VALUE, "This values modifies the amount of hit points that should be lost when you die. The same also applies when you gain max health from lifestealing. 2 hit points = 1 health.");
        //this.bannedUponLosingAllHeartsOrLives = buildBoolean(builder, "Should Players get Banned When They Lose all Lives/Hearts:", false, "When this is false, players will go into spectator mode. Otherwise, players will get banned.");
        this.maximumamountofheartsgainable = buildInt(builder, "Maximum Amount of Health/Hitpoints a Player can get:",  -1, -1, Integer.MAX_VALUE, "WARNING: THIS IS INCOMPATIBLE WITH LIVES. IF YOU ENABLE BOTH, LIVES WILL AUTOMATICALLY BE DISABLED. This values makes a limit for how many hit points a player can get. 2 hit points = 1 heart. Set this to less than 1 to disable the feature.");
    }

    private static ForgeConfigSpec.IntValue buildInt(ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment){
        return builder.comment(comment).translation(name).defineInRange(name, defaultValue, min, max);
    }

    private static ForgeConfigSpec.BooleanValue buildBoolean(ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment){
        return builder.comment(comment).translation(name).define(name, defaultValue);
    }

}


