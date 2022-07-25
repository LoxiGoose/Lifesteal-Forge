package net.goose.lifesteal.world.feature;

import net.goose.lifesteal.LifeSteal;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModPlacedFeatures {

    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, LifeSteal.MOD_ID);

    public static final RegistryObject<PlacedFeature> HEART_ORE_PLACED = PLACED_FEATURES.register("heart_ore_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.HEART_ORE.getHolder().get(),
                    commonOrePlacement(10,
                            HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));

    public static final RegistryObject<PlacedFeature> NETHER_HEART_ORE_PLACED = PLACED_FEATURES.register("nether_heart_ore_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.NETHER_HEART_ORE.getHolder().get(),
                    commonOrePlacement(12,
                            HeightRangePlacement.triangle(VerticalAnchor.aboveBottom(-80), VerticalAnchor.aboveBottom(80)))));

    public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    public static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }

    public static void register(IEventBus eventbus){
            PLACED_FEATURES.register(eventbus);
    }

}
