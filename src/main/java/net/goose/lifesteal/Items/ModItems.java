package net.goose.lifesteal.Items;

import net.goose.lifesteal.LifeSteal;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LifeSteal.MOD_ID);

    public static final RegistryObject<Item> HEART_CORE = ITEMS.register("heart_core",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.LIFE_TAB)));

    public static final RegistryObject<Item> RAW_HEARTCORE = ITEMS.register("raw_heart_core",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.LIFE_TAB)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
