package net.goose.lifesteal.World.feature;

import com.google.common.base.Suppliers;
import net.goose.lifesteal.Blocks.ModBlocks;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.core.Registry;
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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ModConfiguredFeatures {
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES =
            DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, LifeSteal.MOD_ID);

    public static final Supplier<List<OreConfiguration.TargetBlockState>> OVERWORLD_HEART_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.HEART_ORE.get().defaultBlockState()),
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_HEART_ORE.get().defaultBlockState())
    ));

    public static final Supplier<List<OreConfiguration.TargetBlockState>> NETHER_HEART_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, ModBlocks.NETHERRACK_HEART_ORE.get().defaultBlockState())
    ));

    public static final RegistryObject<ConfiguredFeature<?, ?>> HEART_ORE = CONFIGURED_FEATURES.register("heart_ore",
            () -> new ConfiguredFeature<>(Feature.SCATTERED_ORE, new OreConfiguration(OVERWORLD_HEART_ORES.get(), 12)));
    public static final RegistryObject<ConfiguredFeature<?, ?>> NETHER_HEART_ORE = CONFIGURED_FEATURES.register("nether_heart_ore",
            () -> new ConfiguredFeature<>(Feature.SCATTERED_ORE, new OreConfiguration(OVERWORLD_HEART_ORES.get(), 14)));

    public static final RegistryObject<ConfiguredFeature<?, ?>> HEART_GEODE = CONFIGURED_FEATURES.register("heart_geode",
            () -> new ConfiguredFeature<>(Feature.GEODE,
                    new GeodeConfiguration(new GeodeBlockSettings(
                            BlockStateProvider.simple(Blocks.AIR),
                            BlockStateProvider.simple(Blocks.DEEPSLATE),
                            BlockStateProvider.simple(ModBlocks.HEART_ORE.get()),
                            BlockStateProvider.simple(Blocks.DIRT),
                            BlockStateProvider.simple(Blocks.SMOOTH_BASALT),
                            List.of(Blocks.CALCITE.defaultBlockState()),
                            BlockTags.FEATURES_CANNOT_REPLACE , BlockTags.GEODE_INVALID_BLOCKS),
                            new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D),
                            new GeodeCrackSettings(0.95D, 2.0D, 2), 0.35D, 0.083D,
                            true, UniformInt.of(4, 6),
                            UniformInt.of(3, 4), UniformInt.of(1, 2),
                            -16, 16, 0.05D, 1)));
    public static void register(IEventBus eventBus){
        CONFIGURED_FEATURES.register(eventBus);
    }
}
