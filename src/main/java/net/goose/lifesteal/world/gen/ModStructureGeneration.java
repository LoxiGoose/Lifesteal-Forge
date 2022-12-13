package net.goose.lifesteal.world.gen;

import net.goose.lifesteal.Structures.ModStructures;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.*;
import java.util.function.Supplier;

public class ModStructureGeneration {
    public static void generateStructures(final BiomeLoadingEvent event) {
        ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, event.getName());
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);

        String biomeName = event.getName().toString();

        if(types.contains(BiomeDictionary.Type.OVERWORLD)){
            List<Supplier<ConfiguredStructureFeature<?, ?>>> structures = event.getGeneration().getStructures();

            if(types.contains(BiomeDictionary.Type.PLAINS) || biomeName.matches("minecraft:forest")) {
                structures.add(() -> ModStructures.MINERS_HOME.get().configured(FeatureConfiguration.NONE));
            }

            if(types.contains(BiomeDictionary.Type.PLAINS) || biomeName.matches("minecraft:taiga")) {
                structures.add(() -> ModStructures.MINERS_HOME_TAIGA.get().configured(FeatureConfiguration.NONE));
            }

            if(types.contains(BiomeDictionary.Type.PLAINS) || biomeName.matches("minecraft:taiga") || biomeName.matches("minecraft:forest")) {
                structures.add(() -> ModStructures.MINERS_SHACK.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.MINERS_RUINED_SHACK.get().configured(FeatureConfiguration.NONE));
            }

            if(types.contains(BiomeDictionary.Type.HILLS)){
                structures.add(() -> ModStructures.COLLAPSED_MINESHAFT.get().configured(FeatureConfiguration.NONE));
            }

            if(types.contains(BiomeDictionary.Type.FOREST)){
                structures.add(() -> ModStructures.ABANDONED_TRADING_CART.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ORE_CART_1.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ORE_CART_2.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ORE_CART_3.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ORE_CART_4.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ORE_CART_5.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ORE_CART_6.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ROBBED_CART_1.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ROBBED_CART_2.get().configured(FeatureConfiguration.NONE));
                structures.add(() -> ModStructures.ROBBED_CART_3.get().configured(FeatureConfiguration.NONE));
            }

            structures.add(()-> ModStructures.MINERS_BROKEN_PORTAL.get().configured(FeatureConfiguration.NONE));
        }
    }
}