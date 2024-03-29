package net.goose.lifesteal.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class LSAdvancementTrigger extends SimpleCriterionTrigger<LSAdvancementTrigger.Instance> {
    public final ResourceLocation resourceLocation;

    public LSAdvancementTrigger(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public LSAdvancementTrigger.Instance createInstance(JsonObject p_230241_1_, EntityPredicate.Composite p_230241_2_, DeserializationContext p_230241_3_) {
        return new LSAdvancementTrigger.Instance(p_230241_2_, resourceLocation);
    }

    public void trigger(ServerPlayer p_192180_1_) {
        this.trigger(p_192180_1_, (p_226308_1_) -> {
            return true;
        });
    }

    @Override
    public ResourceLocation getId() {
        return resourceLocation;
    }


    public static class Instance extends AbstractCriterionTriggerInstance {

        public Instance(EntityPredicate.Composite p_i231507_1_, ResourceLocation res) {
            super(res, p_i231507_1_);
        }

        public static ConstructBeaconTrigger.TriggerInstance forLevel(MinMaxBounds.Ints p_203912_0_) {
            return new ConstructBeaconTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_203912_0_);
        }


        public JsonObject serializeToJson(SerializationContext p_230240_1_) {
            JsonObject lvt_2_1_ = super.serializeToJson(p_230240_1_);
            return lvt_2_1_;
        }
    }
}
