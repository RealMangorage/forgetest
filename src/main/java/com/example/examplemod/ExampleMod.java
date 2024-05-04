package com.example.examplemod;

import com.example.examplemod.items.BatteryItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    public static final String MODID = "examplemod";

    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE.key(), MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<BatteryItem> BATTERY_ITEM = ITEMS.register("battery", BatteryItem::new);

    public static final RegistryObject<DataComponentType<Integer>> EXAMPLE_COMPONENT = COMPONENT_TYPES.register("test", () -> DataComponentType.<Integer>builder()
            .persistent(ExtraCodecs.intRange(0, Integer.MAX_VALUE))
            .networkSynchronized(ByteBufCodecs.VAR_INT)
            .build()
    );
    public static final RegistryObject<DataComponentType<EnergyStorageImpl.Immutable>> EXAMPLE_STORAGE = COMPONENT_TYPES.register("storage", () -> DataComponentType.<EnergyStorageImpl.Immutable>builder()
            .persistent(EnergyStorageImpl.ENERGY_STORAGE_CODEC)
            .networkSynchronized(EnergyStorageImpl.ENERGY_STORAGE_STREAM_CODEC)
            .cacheEncoding()
            .build()
    );

    public ExampleMod()
    {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        COMPONENT_TYPES.register(modBus);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEnergyStorage> T cast(IEnergyStorage storage) {
        return (T) storage;
    }

    public void onHit(AttackEntityEvent event) {

    }


}
