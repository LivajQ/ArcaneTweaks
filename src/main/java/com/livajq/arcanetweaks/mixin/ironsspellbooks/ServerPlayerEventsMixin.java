package com.livajq.arcanetweaks.mixin.ironsspellbooks;

import io.redspace.ironsspellbooks.player.ServerPlayerEvents;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEvents.class)
public class ServerPlayerEventsMixin {
    
    @Inject(method = "handleOminousEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cancelOminousEffectCheck(EntityJoinLevelEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}