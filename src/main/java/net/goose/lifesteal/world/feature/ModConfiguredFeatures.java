package net.goose.lifesteal.world.feature;

import net.goose.lifesteal.Blocks.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public class ModConfiguredFeatures {

    public static final List<OreConfiguration.TargetBlockState> OVERWORLD_HEART_ORES = List.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.HEART_ORE.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_HEART_ORE.get().defaultBlockState()));

    public static final List<OreConfiguration.TargetBlockState> NETHERRACK_HEART_ORES = List.of(
            OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, ModBlocks.NETHERRACK_HEART_ORE.get().defaultBlockState()));


    public static final Holder<ConfiguredFeature<OreConfiguration, ?>> HEART_ORE = FeatureUtils.register("heart_ore",
            Feature.SCATTERED_ORE, new OreConfiguration(OVERWORLD_HEART_ORES, 12)); // Max Vein Size

    public static final Holder<ConfiguredFeature<OreConfiguration, ?>> NETHER_HEART_ORE = FeatureUtils.register("nether_heart_ore",
            Feature.SCATTERED_ORE, new OreConfiguration(NETHERRACK_HEART_ORES, 14)); // Max Vein Size

    public static final Holder<ConfiguredFeature<GeodeConfiguration, ?>> HEART_GEODE = FeatureUtils.register("heart_geode",
            Feature.GEODE,
                    new GeodeConfiguration(new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
                            BlockStateProvider.simple(Blocks.DEEPSLATE),
                            BlockStateProvider.simple(ModBlocks.HEART_ORE.get()),
                            BlockStateProvider.simple(Blocks.DIRT),
                            BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                            List.of(Blocks.CALCITE.defaultBlockState()),
                            BlockTags.FEATURES_CANNOT_REPLACE , BlockTags.GEODE_INVALID_BLOCKS),
                            new GeodeLayerSettings(1.7D, 1.2D, 2.5D, 3.5D),
                            new GeodeCrackSettings(0.25D, 1.5D, 1), 0.5D, 0.1D,
                            true, UniformInt.of(3, 8),
                            UniformInt.of(2, 6), UniformInt.of(1, 2),
                            -18, 18, 0.075D, 1));


}