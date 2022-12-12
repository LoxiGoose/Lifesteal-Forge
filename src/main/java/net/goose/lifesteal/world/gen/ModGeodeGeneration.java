package net.goose.lifesteal.world.gen;

import net.goose.lifesteal.world.feature.ModPlacedFeatures;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;

public class ModGeodeGeneration {
    public static void generateGeodes(BiomeLoadingEvent event) {
        List<Holder<PlacedFeature>> base = event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES);

        if (!doesBiomeMatch(event.getName(), Biomes.BASALT_DELTAS) && !doesBiomeMatch(event.getName(), Biomes.CRIMSON_FOREST) && !doesBiomeMatch(event.getName(), Biomes.NETHER_WASTES) && !doesBiomeMatch(event.getName(), Biomes.SOUL_SAND_VALLEY) && !doesBiomeMatch(event.getName(), Biomes.WARPED_FOREST)){
            base.add(ModPlacedFeatures.HEART_GEODE_PLACED);
        }
    }
    public static boolean doesBiomeMatch(ResourceLocation biomeNameIn, ResourceKey<Biome> key) {
        return biomeNameIn.toString().matches(key.location().toString());
    }
}
