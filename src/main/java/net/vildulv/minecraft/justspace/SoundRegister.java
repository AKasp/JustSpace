package net.vildulv.minecraft.justspace;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SoundRegister {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, justspace.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> STATIC_TERMINAL = SOUND_EVENTS.register(
            "static_terminal", // must match the resource location on the next line
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(justspace.MODID, "static_terminal")));

    public static final DeferredHolder<SoundEvent, SoundEvent> VOID_MANTA_IDLE = SOUND_EVENTS.register(
            "void_manta_idle", // must match the resource location on the next line
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(justspace.MODID, "void_manta_idle")));


}
