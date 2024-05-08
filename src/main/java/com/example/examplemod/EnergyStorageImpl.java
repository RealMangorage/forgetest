package com.example.examplemod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.component.ImmutabilityDefiner;
import net.minecraftforge.items.component.ImmutableProvider;
import net.minecraftforge.items.component.MutableProvider;


public abstract class EnergyStorageImpl<M, IM> implements ImmutabilityDefiner<M, IM> {
    public static final Codec<EnergyStorageImpl.Immutable> ENERGY_STORAGE_CODEC = RecordCodecBuilder.create((p_330401_) -> {
        return p_330401_.group(
                Codec.INT.optionalFieldOf("energy", 0)
                        .forGetter(a -> {
                            return a.energy;
                        }),
                Codec.INT.optionalFieldOf("capacity", 1000)
                        .forGetter(a -> {
                            return a.capacity;
                        }),
                Codec.INT.optionalFieldOf("maxReceive", 1000)
                        .forGetter(a -> {
                            return a.maxReceive;
                        }),
                Codec.INT.optionalFieldOf("maxExtract", 1000)
                        .forGetter(a -> {
                            return a.maxExtract;
                        })
        ).apply(p_330401_, EnergyStorageImpl.Immutable::new);
    });

    public static final StreamCodec<ByteBuf, EnergyStorageImpl.Immutable> ENERGY_STORAGE_STREAM_CODEC = ByteBufCodecs.fromCodec(ENERGY_STORAGE_CODEC);


    public static final class Immutable extends EnergyStorageImpl<Mutable, Immutable> implements MutableProvider<Mutable, ItemStack>, IEnergyStorage {
        private final int energy;
        private final int capacity;
        private final int maxReceive;
        private final int maxExtract;

        public Immutable(int energy, int capacity, int maxReceive, int maxExtract) {
            this.energy = energy;
            this.capacity = capacity;
            this.maxReceive = maxReceive;
            this.maxExtract = maxExtract;
        }

        @Override
        public Mutable mutable(ItemStack stack) {
            return new Mutable(stack, energy, capacity, maxReceive, maxExtract);
        }

        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!this.canReceive()) {
                return 0;
            } else {
                return Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
            }
        }

        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!this.canExtract()) {
                return 0;
            } else {
                return Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
            }
        }

        public int getEnergyStored() {
            return this.energy;
        }

        public int getMaxEnergyStored() {
            return this.capacity;
        }

        public boolean canExtract() {
            return this.maxExtract > 0;
        }

        public boolean canReceive() {
            return this.maxReceive > 0;
        }
    }

    public static final class Mutable extends EnergyStorageImpl<Mutable, Immutable> implements ImmutableProvider<Immutable>, IEnergyStorage {
        private final ItemStack stack;
        private int energy;
        private final int capacity;
        private final int maxReceive;
        private final int maxExtract;


        public Mutable(ItemStack stack, int energy, int capacity, int maxReceive, int maxExtract) {
            this.stack = stack;
            this.energy = energy;
            this.capacity = capacity;
            this.maxReceive = maxReceive;
            this.maxExtract = maxExtract;
        }

        @Override
        public Immutable immutable() {
            return new Immutable(energy, capacity, maxReceive, maxExtract);
        }

        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!this.canReceive()) {
                return 0;
            } else {
                int energyReceived = Math.min(this.capacity - this.energy, Math.min(this.maxReceive, maxReceive));
                if (!simulate) {
                    this.energy += energyReceived;
                }

                return energyReceived;
            }
        }

        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!this.canExtract()) {
                return 0;
            } else {
                int energyExtracted = Math.min(this.energy, Math.min(this.maxExtract, maxExtract));
                if (!simulate) {
                    this.energy -= energyExtracted;
                }

                return energyExtracted;
            }
        }

        public int getEnergyStored() {
            return this.energy;
        }

        public int getMaxEnergyStored() {
            return this.capacity;
        }

        public boolean canExtract() {
            return this.maxExtract > 0;
        }

        public boolean canReceive() {
            return this.maxReceive > 0;
        }

        @Override
        public void finalizeComponent() {
            stack.set(ExampleMod.EXAMPLE_STORAGE.get(), immutable());
        }
    }
}
