package net.light.stalkermod;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

public class EmissionSaveData extends PersistentState {

    public int savedTimer = -1;
    public boolean savedIsEmissionDamage = false;
    public int savedPostEffectTimer = -1;

    public EmissionSaveData() {}

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        nbt.putInt("EmissionTimer", this.savedTimer);
        nbt.putBoolean("IsEmissionDamage", this.savedIsEmissionDamage);
        nbt.putInt("PostEffectTimer", this.savedPostEffectTimer);
        return nbt;
    }

    public static EmissionSaveData readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        EmissionSaveData data = new EmissionSaveData();
        data.savedTimer = nbt.getInt("EmissionTimer");
        data.savedIsEmissionDamage = nbt.getBoolean("IsEmissionDamage");
        data.savedPostEffectTimer = nbt.getInt("PostEffectTimer");
        return data;
    }

    public static EmissionSaveData get(ServerWorld world) {
        PersistentStateManager manager = world.getServer().getOverworld().getPersistentStateManager();
        Type<EmissionSaveData> type = new Type<>(
                EmissionSaveData::new,
                EmissionSaveData::readNbt,
                null
        );
        return manager.getOrCreate(type, "stalker_emission");
    }
}