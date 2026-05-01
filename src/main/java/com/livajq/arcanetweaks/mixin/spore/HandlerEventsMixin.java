package com.livajq.arcanetweaks.mixin.spore;

import com.Harbinger.Spore.sEvents.HandlerEvents;
import com.livajq.arcanetweaks.util.MajruszsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HandlerEvents.class)
public abstract class HandlerEventsMixin {
    
    @ModifyVariable(
            method = "onServerTick",
            at = @At(value = "STORE", ordinal = 0),
            name = "postProto",
            remap = false
    )
    private static boolean isMasterMode(boolean original) {
        return MajruszsUtils.isLoaded() && MajruszsUtils.isMasterMode(null);
    }
}