package com.example.examplemod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.component.ImmutabilityDefiner;
import net.minecraftforge.items.component.ImmutableProvider;
import net.minecraftforge.items.component.MutableProvider;


public abstract class EnergyStorageImpl<M, IM> implements ImmutabilityDefiner<M, IM> {
    public static final Codec<EnergyStorageImpl.Immutable> ENERGY_STORAGE_CODEC = RecordCodecBuilder.create((p_330401_) -> {
        return p_330401_.group(
                Codec.INT.optionalFieldOf("energy", 0)
                        .forGetter(a -> {
                            return a.getEnergyStored();
                        })
        ).apply(p_330401_, EnergyStorageImpl.Immutable::new);
    });

    public static final StreamCodec<ByteBuf, EnergyStorageImpl.Immutable> ENERGY_STORAGE_STREAM_CODEC = ByteBufCodecs.fromCodec(ENERGY_STORAGE_CODEC);


    public static final class Immutable extends EnergyStorageImpl<Mutable, Immutable> implements MutableProvider<Mutable>, IEnergyStorage {
        private final int energy;
        private final int capacity = 10000;
        private final int maxReceive = 1000;
        private final int maxExtract = 1000;

        public Immutable(int energy) {
            this.energy = energy;
        }
        @Override
        public Mutable mutable() {
            return new Mutable(energy);
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
        protected int energy;
        private final int capacity = 10000;
        private final int maxReceive = 1000;
        private final int maxExtract = 1000;

        public Mutable(int energy) {
            this.energy = energy;
        }

        @Override
        public Immutable immutable() {
            return new Immutable(energy);
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
    }
}
