package net.goose.lifesteal.world.gen;

import net.goose.lifesteal.world.structure.ModStructures;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ModStructureGeneration {
    public static void generateStructures(final BiomeLoadingEvent event) {
        RegistryKey<Biome> key = RegistryKey.create(Registry.BIOME_REGISTRY, event.getName());
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);

        String biomeName = event.getName().toString();

        if(types.contains(BiomeDictionary.Type.OVERWORLD)){
            List<Supplier<StructureFeature <?, ?>>> structures = event.getGeneration().getStructures();

            if(types.contains(BiomeDictionary.Type.PLAINS) || biomeName.matches("minecraft:forest")) {
                structures.add(() -> ModStructures.MINERS_HOME.get().configured(IFeatureConfig.NONE));
            }

            if(types.contains(BiomeDictionary.Type.PLAINS) || biomeName.matches("minecraft:taiga")) {
                structures.add(() -> ModStructures.MINERS_HOME_TAIGA.get().configured(IFeatureConfig.NONE));
            }

            if(types.contains(BiomeDictionary.Type.PLAINS) || biomeName.matches("minecraft:taiga") || biomeName.matches("minecraft:forest")) {
                structures.add(() -> ModStructures.MINERS_SHACK.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.MINERS_RUINED_SHACK.get().configured(IFeatureConfig.NONE));
            }

            if(types.contains(BiomeDictionary.Type.HILLS)){
                structures.add(() -> ModStructures.COLLAPSED_MINESHAFT.get().configured(IFeatureConfig.NONE));
            }

            if(types.contains(BiomeDictionary.Type.FOREST)){
                structures.add(() -> ModStructures.ABANDONED_TRADING_CART.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ORE_CART_1.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ORE_CART_2.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ORE_CART_3.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ORE_CART_4.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ORE_CART_5.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ORE_CART_6.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ROBBED_CART_1.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ROBBED_CART_2.get().configured(IFeatureConfig.NONE));
                structures.add(() -> ModStructures.ROBBED_CART_3.get().configured(IFeatureConfig.NONE));
            }

            structures.add(()-> ModStructures.MINERS_BROKEN_PORTAL.get().configured(IFeatureConfig.NONE));
        }
    }
}