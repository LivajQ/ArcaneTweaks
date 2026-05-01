package com.livajq.arcanetweaks.mixin.attributeslib;

import com.livajq.arcanetweaks.mixin.bountifulbaubles.ModEventsAccessor;
import dev.shadowsoffire.attributeslib.impl.AttributeEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttributeEvents.class)
public abstract class AttributeEventsMixin {
    
    @Inject(
            method = "apothCriticalStrike",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/shadowsoffire/placebo/network/PacketDistro;sendToTracking(Lnet/minecraftforge/network/simple/SimpleChannel;Ljava/lang/Object;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V",
                    shift = At.Shift.AFTER
            ),
            remap = false
    )
    private void wrathPendantProc(LivingHurtEvent e, CallbackInfo ci) {
        if (!ModList.get().isLoaded("bountifulbaubles")) return;
        if (e.getSource().getEntity() instanceof Player player) ModEventsAccessor.invokeHandleCriticalHit(player);
    }
}