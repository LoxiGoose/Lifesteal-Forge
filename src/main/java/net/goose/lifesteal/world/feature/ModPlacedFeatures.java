package net.goose.lifesteal.World.feature;

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
                    commonOrePlacement(6, // VeinsPerChunk
                            HeightRangePlacement.triangle(VerticalAnchor.absolute(-50), VerticalAnchor.absolute(70)))));
    public static final RegistryObject<PlacedFeature> NETHER_HEART_ORE_PLACED = PLACED_FEATURES.register("nether_heart_ore_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.NETHER_HEART_ORE.getHolder().get(), commonOrePlacement(6, // VeinsPerChunk
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(20), VerticalAnchor.absolute(100)))));

    public static final RegistryObject<PlacedFeature> HEART_GEODE_PLACED = PLACED_FEATURES.register("heart_geode_placed",
            () -> new PlacedFeature(ModConfiguredFeatures.HEART_GEODE.getHolder().get(), List.of(
                    RarityFilter.onAverageOnceEvery(35), InSquarePlacement.spread(),
                    HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.absolute(50)),
                    BiomeFilter.biome())));
    public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
        return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_) {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    public static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_) {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }
    public static void register(IEventBus eventBus){
        PLACED_FEATURES.register(eventBus);
    }
}
