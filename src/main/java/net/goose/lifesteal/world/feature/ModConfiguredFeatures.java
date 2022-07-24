package net.goose.lifesteal.world.feature;

import net.goose.lifesteal.Blocks.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.List;

public class ModConfiguredFeatures {

    public static final List<OreConfiguration.TargetBlockState> OVERWORLD_HEART_ORES = List.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.HEART_ORE.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_HEART_ORE.get().defaultBlockState()));

    public static final List<OreConfiguration.TargetBlockState> NETHERRACK_HEART_ORES = List.of(
            OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, ModBlocks.NETHERRACK_HEART_ORE.get().defaultBlockState()));


    public static final Holder<ConfiguredFeature<OreConfiguration, ?>> HEART_ORE = FeatureUtils.register("heart_ore",
            Feature.ORE, new OreConfiguration(OVERWORLD_HEART_ORES, 10));

    public static final Holder<ConfiguredFeature<OreConfiguration, ?>> NETHER_HEART_ORE = FeatureUtils.register("nether_heart_ore",
            Feature.ORE, new OreConfiguration(NETHERRACK_HEART_ORES, 12));

}