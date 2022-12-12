package net.goose.lifesteal;

import com.mojang.logging.LogUtils;
import net.goose.lifesteal.Blocks.ModBlocks;
import net.goose.lifesteal.Capability.CapabilityRegistry;
import net.goose.lifesteal.Configurations.ConfigHolder;
import net.goose.lifesteal.Items.ModCreativeModeTab;
import net.goose.lifesteal.Items.ModItems;
import net.goose.lifesteal.Enchantments.ModEnchantments;
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
    private static final Logger LOGGER = LogUtils.getLogger();

    public LifeSteal()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModCreativeModeTab::registerTabItems);
        modEventBus.addListener(ModCreativeModeTab::registerTab);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEnchantments.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(CapabilityRegistry.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("Lifestealers are on the loose!");
    }

}
