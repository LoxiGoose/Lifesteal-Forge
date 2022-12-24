package net.goose.lifesteal.advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;

public class LSAdvancementTriggerRegistry {

    public static LSAdvancementTrigger GET_10_MAX_HEARTS = new LSAdvancementTrigger(new ResourceLocation("lifesteal:get_10_max_hearts"));

    public static void init(){
        CriteriaTriggers.register(GET_10_MAX_HEARTS);
    }

}
