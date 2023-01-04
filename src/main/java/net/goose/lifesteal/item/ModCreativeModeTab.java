package net.goose.lifesteal.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ModCreativeModeTab {

    public static final CreativeModeTab LIFESTEAL_TAB = new CreativeModeTab("lifesteal") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.HEART_CRYSTAL.get());
        }

    };
}