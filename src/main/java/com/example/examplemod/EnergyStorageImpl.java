package com.example.examplemod;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageImpl implements IEnergyStorage {
    public static final Codec<EnergyStorageImpl> ENERGY_STORAGE_CODEC = ExtraCodecs.POSITIVE_INT.xmap(EnergyStorageImpl::new, (b) -> {
        return b.energy;
    });
    public static final StreamCodec<ByteBuf, EnergyStorageImpl> ENERGY_STORAGE_STREAM_CODEC = ByteBufCodecs.fromCodec(ENERGY_STORAGE_CODEC);

    protected int energy;
    protected final int capacity = 10000;
    protected final int maxReceive = 1000;
    protected final int maxExtract = 1000;

    public EnergyStorageImpl(int energy) {
        this.energy = Math.max(0, Math.min(capacity, energy));
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
        return true;
    }

    public boolean canReceive() {
        return true;
    }
}
