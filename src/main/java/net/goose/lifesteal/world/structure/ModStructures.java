package net.goose.lifesteal.world.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.world.structure.structures.*;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModStructures {
    public static final DeferredRegister<Structure<?>> STRUCTURES =
            DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, LifeSteal.MOD_ID);

    public static final RegistryObject<Structure<NoFeatureConfig>> MINERS_HOME =
            STRUCTURES.register("miners_home", () -> (new MinersHomeStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> MINERS_HOME_TAIGA =
            STRUCTURES.register("miners_home_taiga", () -> (new MinersHomeTaigaStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> MINERS_RUINED_SHACK =
            STRUCTURES.register("miners_ruined_shack", () -> (new MinersRuinedShackStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> MINERS_SHACK =
            STRUCTURES.register("miners_shack", () -> (new MinersShackStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> MINERS_BROKEN_PORTAL =
            STRUCTURES.register("miners_broken_portal", () -> (new MinersBrokenPortalStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ABANDONED_TRADING_CART =
            STRUCTURES.register("abandoned_trading_cart", () -> (new AbandonedTradingCartStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> COLLAPSED_MINESHAFT =
            STRUCTURES.register("collapsed_mineshaft", () -> (new CollapsedMineshaftStructure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ORE_CART_1 =
            STRUCTURES.register("ore_cart_1", () -> (new OreCart1Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ORE_CART_2 =
            STRUCTURES.register("ore_cart_2", () -> (new OreCart2Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ORE_CART_3 =
            STRUCTURES.register("ore_cart_3", () -> (new OreCart3Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ORE_CART_4 =
            STRUCTURES.register("ore_cart_4", () -> (new OreCart4Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ORE_CART_5 =
            STRUCTURES.register("ore_cart_5", () -> (new OreCart5Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ORE_CART_6 =
            STRUCTURES.register("ore_cart_6", () -> (new OreCart6Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ROBBED_CART_1 =
            STRUCTURES.register("robbed_cart_1", () -> (new RobbedCart1Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ROBBED_CART_2 =
            STRUCTURES.register("robbed_cart_2", () -> (new RobbedCart2Structure(NoFeatureConfig.CODEC)));
    public static final RegistryObject<Structure<NoFeatureConfig>> ROBBED_CART_3 =
            STRUCTURES.register("robbed_cart_3", () -> (new RobbedCart3Structure(NoFeatureConfig.CODEC)));

    /* average distance apart in chunks between spawn attempts */
    /* minimum distance apart in chunks between spawn attempts. MUST BE LESS THAN ABOVE VALUE*/
    /* this modifies the seed of the structure so no two structures always spawn over each-other.
    Make this large and unique. */
    public static void setupStructures() {
        setupMapSpacingAndLand(MINERS_HOME.get(),
                new StructureSeparationSettings(50,35, 1542327654),
                true);
        setupMapSpacingAndLand(MINERS_HOME_TAIGA.get(),
                new StructureSeparationSettings(50,20, 1542327653),
                true);
        setupMapSpacingAndLand(MINERS_RUINED_SHACK.get(),
                new StructureSeparationSettings(50,15, 1542327652),
                true);
        setupMapSpacingAndLand(MINERS_SHACK.get(),
                new StructureSeparationSettings(50,20, 1542327651),
                true);
        setupMapSpacingAndLand(MINERS_BROKEN_PORTAL.get(),
                new StructureSeparationSettings(50,20, 1542327650),
                true);
        setupMapSpacingAndLand(ABANDONED_TRADING_CART.get(),
                new StructureSeparationSettings(100,50, 1542327649),
                true);
        setupMapSpacingAndLand(COLLAPSED_MINESHAFT.get(),
                new StructureSeparationSettings(100,80, 1542327648),
                true);
        setupMapSpacingAndLand(ORE_CART_1.get(),
                new StructureSeparationSettings(50,20, 1542327647),
                true);
        setupMapSpacingAndLand(ORE_CART_2.get(),
                new StructureSeparationSettings(150,120, 1542327646),
                true);
        setupMapSpacingAndLand(ORE_CART_3.get(),
                new StructureSeparationSettings(120,100, 1542327645),
                true);
        setupMapSpacingAndLand(ORE_CART_4.get(),
                new StructureSeparationSettings(1000,750, 1542327644),
                true);
        setupMapSpacingAndLand(ORE_CART_5.get(),
                new StructureSeparationSettings(50,20, 1542327643),
                true);
        setupMapSpacingAndLand(ORE_CART_6.get(),
                new StructureSeparationSettings(50,20, 1542327642),
                true);
        setupMapSpacingAndLand(ROBBED_CART_1.get(),
                new StructureSeparationSettings(40,15, 1542327641),
                true);
        setupMapSpacingAndLand(ROBBED_CART_2.get(),
                new StructureSeparationSettings(40,15, 1542327640),
                true);
        setupMapSpacingAndLand(ROBBED_CART_3.get(),
                new StructureSeparationSettings(40,15, 1542327639),
                true);
    }

    /**
     * Adds the provided structure to the registry, and adds the separation settings.
     * The rarity of the structure is determined based on the values passed into
     * this method in the structureSeparationSettings argument.
     * This method is called by setupStructures above.
     **/
    public static <F extends Structure<?>> void setupMapSpacingAndLand(F structure, StructureSeparationSettings structureSeparationSettings,
                                                                       boolean transformSurroundingLand) {
        //add our structures into the map in Structure class
        Structure.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

        /*
         * Whether surrounding land will be modified automatically to conform to the bottom of the structure.
         * Basically, it adds land at the base of the structure like it does for Villages and Outposts.
         * Doesn't work well on structure that have pieces stacked vertically or change in heights.
         *
         */
        if (transformSurroundingLand) {
            Structure.NOISE_AFFECTING_FEATURES = ImmutableList.<Structure<?>>builder()
                    .addAll(Structure.NOISE_AFFECTING_FEATURES)
                    .add(structure)
                    .build();
        }

        /*
         * This is the map that holds the default spacing of all structures.
         * Always add your structure to here so that other mods can utilize it if needed.
         *
         * However, while it does propagate the spacing to some correct dimensions from this map,
         * it seems it doesn't always work for code made dimensions as they read from this list beforehand.
         *
         * Instead, we will use the WorldEvent.Load event in ModWorldEvents to add the structure
         * spacing from this list into that dimension or to do dimension blacklisting properly.
         * We also use our entry in DimensionStructuresSettings.DEFAULTS in WorldEvent.Load as well.
         *
         * DEFAULTS requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
         */
        DimensionStructuresSettings.DEFAULTS =
                ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
                        .putAll(DimensionStructuresSettings.DEFAULTS)
                        .put(structure, structureSeparationSettings)
                        .build();

        /*
         * There are very few mods that relies on seeing your structure in the
         * noise settings registry before the world is made.
         *
         * You may see some mods add their spacings to DimensionSettings.BUILTIN_OVERWORLD instead of the
         * NOISE_GENERATOR_SETTINGS loop below but that field only applies for the default overworld and
         * won't add to other worldtypes or dimensions (like amplified or Nether).
         * So yeah, don't do DimensionSettings.BUILTIN_OVERWORLD. Use the NOISE_GENERATOR_SETTINGS loop
         * below instead if you must.
         */
        WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
            Map<Structure<?>, StructureSeparationSettings> structureMap =
                    settings.getValue().structureSettings().structureConfig();
            /*
             * Pre-caution in case a mod makes the structure map immutable like datapacks do.
             * I take no chances myself. You never know what another mods does...
             *
             * structureConfig requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
             */
            if (structureMap instanceof ImmutableMap) {
                Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, structureSeparationSettings);
                settings.getValue().structureSettings().structureConfig = tempMap;

            } else {
                structureMap.put(structure, structureSeparationSettings);
            }
        });
    }

    public static void register(IEventBus eventBus) {
        STRUCTURES.register(eventBus);
    }
}