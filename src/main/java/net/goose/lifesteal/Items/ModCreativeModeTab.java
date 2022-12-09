package net.goose.lifesteal.Items;

import net.goose.lifesteal.Blocks.ModBlocks;
import net.goose.lifesteal.Enchantments.ModEnchantments;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static void registerTabItems(final CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == CreativeModeTabs.FOOD_AND_DRINKS){
            event.registerSimple(CreativeModeTabs.FOOD_AND_DRINKS, ModItems.HEART_CRYSTAL.get(), ModItems.HEART_CORE.get());
        }
        if(event.getTab() == CreativeModeTabs.COLORED_BLOCKS){
            event.registerSimple(CreativeModeTabs.COLORED_BLOCKS, ModBlocks.HEART_CORE_BLOCK.get());
        }
        if(event.getTab() == CreativeModeTabs.INGREDIENTS){
            event.registerSimple(CreativeModeTabs.INGREDIENTS, ModBlocks.HEART_CORE_BLOCK.get(), ModItems.HEART_FRAGMENT.get());
        }
        if(event.getTab() == CreativeModeTabs.NATURAL_BLOCKS){
            event.registerSimple(CreativeModeTabs.NATURAL_BLOCKS, ModBlocks.HEART_ORE.get(), ModBlocks.NETHERRACK_HEART_ORE.get(), ModBlocks.DEEPSLATE_HEART_ORE.get());
        }
    }

}
