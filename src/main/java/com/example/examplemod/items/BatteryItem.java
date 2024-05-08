package com.example.examplemod.items;

import com.example.examplemod.EnergyStorageImpl;
import com.example.examplemod.ExampleMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;

public class BatteryItem extends Item {
    public BatteryItem() {
        super(new Properties()
                .component(ExampleMod.EXAMPLE_STORAGE.get(), new EnergyStorageImpl.Immutable(0, 10000, 1000, 1000))
                .stacksTo(1)
        );
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.has(ExampleMod.EXAMPLE_STORAGE.get());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return Mth.color(0, 22, 255);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (stack.has(ExampleMod.EXAMPLE_STORAGE.get())) {
            var storage = stack.get(ExampleMod.EXAMPLE_STORAGE.get());
            return Mth.clamp(Math.round(13.0F - (float) (storage.getMaxEnergyStored() - storage.getEnergyStored()) * 13.0F / (float)storage.getMaxEnergyStored()), 0, 13);
        }
        return 0;
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        if (level.isClientSide()) return;
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
            energy.receiveEnergy(10, false);
            energy.finalizeComponent();

            var storage = stack.get(ExampleMod.EXAMPLE_STORAGE.get());
            stack.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("Energy: %s / %s".formatted(storage.getEnergyStored(), storage.getMaxEnergyStored())).withStyle(ChatFormatting.WHITE))));
        });

    }
}
