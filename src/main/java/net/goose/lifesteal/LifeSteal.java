package net.goose.lifesteal;

import com.mojang.logging.LogUtils;
import net.goose.lifesteal.advancement.ModCriteria;
import net.goose.lifesteal.block.ModBlocks;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.configuration.Config;
import net.goose.lifesteal.configuration.ConfigHolder;
import net.goose.lifesteal.event.EventHandler;
import net.goose.lifesteal.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LifeSteal.MOD_ID)
public class LifeSteal {
    // Directly reference a slf4j logger
    public static final String MOD_ID = "lifesteal";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static Config config;

    public LifeSteal() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);
        config = ConfigHolder.SERVER;
        // Register the setup method for modloading

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(CapabilityRegistry.EventCapHandler.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("Lifestealers are on the loose!");
        ModCriteria.init();
    }
}
