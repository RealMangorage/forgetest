package com.example.examplemod;

import com.example.examplemod.api.IEnergyStorageHolder;
import com.example.examplemod.items.BatteryItem;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.component.ImmutableProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    public static final Capability<IEnergyStorageHolder> ENERGY_COMPONENT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
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

        MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::onGather);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEnergyStorage> T cast(IEnergyStorage storage) {
        return (T) storage;
    }


    public void onGather(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof BatteryItem batteryItem) {
            event.addCapability(new ResourceLocation("example:provider"), new MyProvider(event.getObject()));
        }
    }

    public static class MyEnergyStorageCap implements IEnergyStorageHolder {
        private final ItemStack stack;
        private final IEnergyStorage energyStorage;

        public MyEnergyStorageCap(ItemStack stack, IEnergyStorage energyStorage) {
            this.stack = stack;
            this.energyStorage = energyStorage;
        }

        @Override
        public IEnergyStorage getStorage() {
            return energyStorage;
        }

        @SuppressWarnings("unchecked")
        private <T> T cast(DataComponentType<T> componentType, Object value) {
            return (T) value;
        }

        @Override
        @SuppressWarnings("all")
        public void finalizeStorage() {
            ImmutableProvider provider = (ImmutableProvider) energyStorage;
            stack.set(ExampleMod.EXAMPLE_STORAGE.get(), cast(ExampleMod.EXAMPLE_STORAGE.get(), provider.immutable()));
        }
    }

    public static class MyProvider implements ICapabilityProvider {
        private final LazyOptional<IEnergyStorageHolder> holderLazyOptional;

        public MyProvider(ItemStack stack) {
            this.holderLazyOptional = stack.has(ExampleMod.EXAMPLE_STORAGE.get()) ?
                    LazyOptional.of(() -> new MyEnergyStorageCap(stack, stack.get(ExampleMod.EXAMPLE_STORAGE.get()).mutable())) : LazyOptional.empty();
        }


        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
            if (capability == ENERGY_COMPONENT_CAPABILITY)
                return holderLazyOptional.cast();
            return LazyOptional.empty();
        }
    }

}
