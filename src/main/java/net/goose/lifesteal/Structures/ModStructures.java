package net.goose.lifesteal.Structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.Structures.structures.*;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModStructures {
    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time so we don't have to handle it ourselves.
     *
     * HOWEVER, do note that Deferred Registries only work for anything that is a Forge Registry. This means that
     * configured structures and configured features need to be registered directly to BuiltinRegistries as there
     * is no Deferred Registry system for them.
     */
    public static final DeferredRegister<StructureFeature<?>> STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, LifeSteal.MOD_ID);

    public static void register(IEventBus eventBus){
        STRUCTURE.register(eventBus);
    }
    /**
     * Registers the structure itself and sets what its path is. In this case, the
     * structure will have the resourcelocation of structure_tutorial:run_down_house.
     *
     * It is always a good idea to register your Structures so that other mods and datapacks can
     * use them too directly from the registries. It great for mod/datapacks compatibility.
     *
     * IMPORTANT: Once you have set the name for your structure below and distributed your mod,
     *   changing the structure's registry name or removing the structure may cause log spam.
     *   This log spam won't break your worlds as forge already fixed the Mojang bug of removed structures wrecking worlds.
     *   https://github.com/MinecraftForge/MinecraftForge/commit/56e538e8a9f1b8e6ff847b9d2385484c48849b8d
     *
     *   However, users might not know that and think you are to blame for issues that doesn't exist.
     *   So it is best to keep your structure names the same as long as you can instead of changing them frequently.
     */
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> MINERS_HOME = STRUCTURE.register("miners_home", () -> (new MinersHomeStructure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> MINERS_BROKEN_PORTAL = STRUCTURE.register("miners_broken_portal", () -> (new MinersBrokenPortalStructure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> MINERS_HOME_TAIGA = STRUCTURE.register("miners_home_taiga", () -> (new MinersHomeTaigaStructure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> MINERS_SHACK = STRUCTURE.register("miners_shack", () -> (new MinersShackStructure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> MINERS_RUINED_SHACK = STRUCTURE.register("miners_ruined_shack", () -> (new MinersRuinedShackStructure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> COLLAPSED_MINESHAFT = STRUCTURE.register("collapsed_mineshaft", () -> (new CollapsedMineshaftStructure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ABANDONED_TRADING_CART = STRUCTURE.register("abandoned_trading_cart", () -> (new AbandonedTradingCartStructure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ORE_CART_1 = STRUCTURE.register("ore_cart_1", () -> (new OreCart1Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ORE_CART_2 = STRUCTURE.register("ore_cart_2", () -> (new OreCart2Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ORE_CART_3 = STRUCTURE.register("ore_cart_3", () -> (new OreCart3Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ORE_CART_4 = STRUCTURE.register("ore_cart_4", () -> (new OreCart4Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ORE_CART_5 = STRUCTURE.register("ore_cart_5", () -> (new OreCart5Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ORE_CART_6 = STRUCTURE.register("ore_cart_6", () -> (new OreCart6Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ROBBED_CART_1 = STRUCTURE.register("robbed_cart_1", () -> (new RobbedCart1Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ROBBED_CART_2 = STRUCTURE.register("robbed_cart_2", () -> (new RobbedCart2Structure(NoneFeatureConfiguration.CODEC)));
    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> ROBBED_CART_3 = STRUCTURE.register("robbed_cart_3", () -> (new RobbedCart3Structure(NoneFeatureConfiguration.CODEC)));


    /**
     * This is where we set the rarity of your structures and determine if land conforms to it.
     * See the comments in below for more details.
     */
    public static void setupStructures() {
        setupMapSpacingAndLand(MINERS_HOME.get(),
                new StructureFeatureConfiguration(50,35, 1542327654),
                true);
        setupMapSpacingAndLand(MINERS_HOME_TAIGA.get(),
                new StructureFeatureConfiguration(50,20, 1542327653),
                true);
        setupMapSpacingAndLand(MINERS_RUINED_SHACK.get(),
                new StructureFeatureConfiguration(50,15, 1542327652),
                true);
        setupMapSpacingAndLand(MINERS_SHACK.get(),
                new StructureFeatureConfiguration(50,20, 1542327651),
                true);
        setupMapSpacingAndLand(MINERS_BROKEN_PORTAL.get(),
                new StructureFeatureConfiguration(50,20, 1542327650),
                true);
        setupMapSpacingAndLand(ABANDONED_TRADING_CART.get(),
                new StructureFeatureConfiguration(100,50, 1542327649),
                true);
        setupMapSpacingAndLand(COLLAPSED_MINESHAFT.get(),
                new StructureFeatureConfiguration(100,80, 1542327648),
                true);
        setupMapSpacingAndLand(ORE_CART_1.get(),
                new StructureFeatureConfiguration(50,20, 1542327647),
                true);
        setupMapSpacingAndLand(ORE_CART_2.get(),
                new StructureFeatureConfiguration(150,120, 1542327646),
                true);
        setupMapSpacingAndLand(ORE_CART_3.get(),
                new StructureFeatureConfiguration(120,100, 1542327645),
                true);
        setupMapSpacingAndLand(ORE_CART_4.get(),
                new StructureFeatureConfiguration(1000,750, 1542327644),
                true);
        setupMapSpacingAndLand(ORE_CART_5.get(),
                new StructureFeatureConfiguration(50,20, 1542327643),
                true);
        setupMapSpacingAndLand(ORE_CART_6.get(),
                new StructureFeatureConfiguration(50,20, 1542327642),
                true);
        setupMapSpacingAndLand(ROBBED_CART_1.get(),
                new StructureFeatureConfiguration(40,15, 1542327641),
                true);
        setupMapSpacingAndLand(ROBBED_CART_2.get(),
                new StructureFeatureConfiguration(40,15, 1542327640),
                true);
        setupMapSpacingAndLand(ROBBED_CART_3.get(),
                new StructureFeatureConfiguration(40,15, 1542327639),
                true);


        // Add more structures here and so on
    }

    /**
     * Adds the provided structure to the registry, and adds the separation settings.
     * The rarity of the structure is determined based on the values passed into
     * this method in the StructureFeatureConfiguration argument.
     * This method is called by setupStructures above.
     */
    public static <F extends StructureFeature<?>> void setupMapSpacingAndLand(
            F structure,
            StructureFeatureConfiguration StructureFeatureConfiguration,
            boolean transformSurroundingLand)
    {
        /*
         * We need to add our structures into the map in StructureFeature class
         * alongside vanilla structures or else it will cause errors.
         *
         * If the registration is setup properly for the structure,
         * getRegistryName() should never return null.
         */
        StructureFeature.STRUCTURES_REGISTRY.put(structure.getRegistryName().toString(), structure);

        /*
         * Whether surrounding land will be modified automatically to conform to the bottom of the structure.
         * Basically, it adds land at the base of the structure like it does for Villages and Outposts.
         * Doesn't work well on structure that have pieces stacked vertically or change in heights.
         *
         * Note: The air space this method will create will be filled with water if the structure is below sealevel.
         * This means this is best for structure above sealevel so keep that in mind.
         *
         * NOISE_AFFECTING_FEATURES requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
         */
        if(transformSurroundingLand){
            StructureFeature.NOISE_AFFECTING_FEATURES =
                    ImmutableList.<StructureFeature<?>>builder()
                            .addAll(StructureFeature.NOISE_AFFECTING_FEATURES)
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
         * Instead, we will use the WorldEvent.Load event in StructureTutorialMain to add the structure
         * spacing from this list into that dimension or to do dimension blacklisting properly.
         * We also use our entry in DimensionStructuresSettings.DEFAULTS in WorldEvent.Load as well.
         *
         * DEFAULTS requires AccessTransformer  (See resources/META-INF/accesstransformer.cfg)
         */
        StructureSettings.DEFAULTS =
                ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
                        .putAll(StructureSettings.DEFAULTS)
                        .put(structure, StructureFeatureConfiguration)
                        .build();


        /*
         * There are very few mods that relies on seeing your structure in the noise settings registry before the world is made.
         *
         * You may see some mods add their spacings to DimensionSettings.BUILTIN_OVERWORLD instead of the NOISE_GENERATOR_SETTINGS loop below but
         * that field only applies for the default overworld and won't add to other worldtypes or dimensions (like amplified or Nether).
         * So yeah, don't do DimensionSettings.BUILTIN_OVERWORLD. Use the NOISE_GENERATOR_SETTINGS loop below instead if you must.
         */
        BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().forEach(settings -> {
            Map<StructureFeature<?>, StructureFeatureConfiguration> structureMap = settings.getValue().structureSettings().structureConfig();

            /*
             * Pre-caution in case a mod makes the structure map immutable like datapacks do.
             * I take no chances myself. You never know what another mods does...
             *
             * structureConfig requires AccessTransformer (See resources/META-INF/accesstransformer.cfg)
             */
            if(structureMap instanceof ImmutableMap){
                Map<StructureFeature<?>, StructureFeatureConfiguration> tempMap = new HashMap<>(structureMap);
                tempMap.put(structure, StructureFeatureConfiguration);
                settings.getValue().structureSettings().structureConfig = tempMap;
            }
            else{
                structureMap.put(structure, StructureFeatureConfiguration);
            }
        });
    }
}
