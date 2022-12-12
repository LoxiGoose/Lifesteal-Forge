package net.goose.lifesteal.Enchantments;

import net.goose.lifesteal.Items.ModItems;
import net.goose.lifesteal.LifeSteal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, LifeSteal.MOD_ID);

    public static RegistryObject<Enchantment> LIFESTEAL = registerEnchantment("lifesteal",
            ()-> new LifeStealEnchantment(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND) {});

    public static <T extends Enchantment>RegistryObject<T> registerEnchantment(String name, Supplier<T> enchantment){
        RegistryObject<T> toReturn = ENCHANTMENTS.register(name, enchantment);
        return toReturn;
    };

    public static void register(IEventBus eventBus){
        ENCHANTMENTS.register(eventBus);
    }
}
