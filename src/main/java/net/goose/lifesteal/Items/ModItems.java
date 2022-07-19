package net.goose.lifesteal.Items;

import net.goose.lifesteal.Items.custom.HeartCrystalItem;
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

    public static final RegistryObject<Item> HEART_FRAGMENT = ITEMS.register("heart_fragment",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.LIFE_TAB)));

    public static final RegistryObject<Item> HEART_CRYSTAL = ITEMS.register("heart_crystal",
            () -> new HeartCrystalItem(new Item.Properties().stacksTo(1).fireResistant().tab(ModCreativeModeTab.LIFE_TAB)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
