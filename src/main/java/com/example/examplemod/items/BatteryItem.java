package com.example.examplemod.items;

import com.example.examplemod.EnergyStorageImpl;
import com.example.examplemod.ExampleMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BatteryItem extends Item {
    public BatteryItem() {
        super(new Properties()
                .component(ExampleMod.EXAMPLE_STORAGE.get(), ExampleMod.cast(new EnergyStorageImpl(0)))
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
        if (stack.has(ExampleMod.EXAMPLE_STORAGE.get())) {
            stack.modify(ExampleMod.EXAMPLE_STORAGE.get(), e -> {
                e.receiveEnergy(10, false);
            });
        }
    }
}
