package net.goose.lifesteal.item;

import net.goose.lifesteal.item.custom.HeartCoreItem;
import net.goose.lifesteal.item.custom.HeartCrystalItem;
import net.goose.lifesteal.LifeSteal;
import net.goose.lifesteal.item.custom.ReviveItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LifeSteal.MOD_ID);

    public static final RegistryObject<Item> HEART_CORE = ITEMS.register("heart_core",
            () -> new HeartCoreItem(new Item.Properties().food(HeartCoreItem.HeartCore)));

    public static final RegistryObject<Item> HEART_FRAGMENT = ITEMS.register("heart_fragment",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HEART_CRYSTAL = ITEMS.register("heart_crystal",
            () -> new HeartCrystalItem(new Item.Properties().stacksTo(1).fireResistant().food(HeartCrystalItem.HeartCrystal)));
    /*public static final RegistryObject<Item> REVIVE_ITEM = ITEMS.register("revive_item",
            () -> new ReviveItem(new Item.Properties().stacksTo(1).fireResistant()));*/

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
