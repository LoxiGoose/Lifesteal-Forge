package net.goose.lifesteal;

import com.mojang.logging.LogUtils;
import net.goose.lifesteal.advancement.LSAdvancementTriggerRegistry;
import net.goose.lifesteal.block.ModBlocks;
import net.goose.lifesteal.capability.CapabilityRegistry;
import net.goose.lifesteal.configuration.ConfigHolder;
import net.goose.lifesteal.event.EventHandler;
import net.goose.lifesteal.item.ModItems;
import net.goose.lifesteal.world.feature.ModConfiguredFeatures;
import net.goose.lifesteal.world.feature.ModPlacedFeatures;
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
public class LifeSteal
{
    public static final String MOD_ID = "lifesteal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LifeSteal()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModConfiguredFeatures.register(modEventBus);
        ModPlacedFeatures.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.register(CapabilityRegistry.EventCapHandler.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("Lifestealers are on the loose!");
        LSAdvancementTriggerRegistry.init();
    }

}
