package com.example.examplemod.api;

import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergyStorageHolder {
    IEnergyStorage getStorage();
    void finalizeStorage();
}
