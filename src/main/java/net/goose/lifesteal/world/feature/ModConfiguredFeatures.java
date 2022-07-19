package net.goose.lifesteal.world.feature;

import com.google.common.base.Suppliers;
import net.goose.lifesteal.Blocks.ModBlocks;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
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
            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_HEART_ORE.get().defaultBlockState())));
   // public static final Supplier<List<OreConfiguration.TargetBlockState>> END_HEART_ORES = Suppliers.memoize(() -> List.of(
     //       OreConfiguration.target(new BlockMatchTest(Blocks.END_STONE), ModBlocks.ENDSTONE_HEART_ORE.get().defaultBlockState())));
    public static final Supplier<List<OreConfiguration.TargetBlockState>> NETHERRACK_HEART_ORES = Suppliers.memoize(() -> List.of(
            OreConfiguration.target(OreFeatures.NETHER_ORE_REPLACEABLES, ModBlocks.NETHERRACK_HEART_ORE.get().defaultBlockState())));


    public static final RegistryObject<ConfiguredFeature<?, ?>> HEART_ORE = CONFIGURED_FEATURES.register("heart_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(OVERWORLD_HEART_ORES.get(), 8)));

    public static final RegistryObject<ConfiguredFeature<?, ?>> NETHER_HEART_ORE = CONFIGURED_FEATURES.register("nether_heart_ore",
            () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(NETHERRACK_HEART_ORES.get(), 12)));

   // public static final RegistryObject<ConfiguredFeature<?, ?>> END_HEART_ORE = CONFIGURED_FEATURES.register("end_heart_ore",
        //    () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(END_HEART_ORES.get(), 10)));

    public static void register(IEventBus eventbus){
        CONFIGURED_FEATURES.register(eventbus);
    }
}
