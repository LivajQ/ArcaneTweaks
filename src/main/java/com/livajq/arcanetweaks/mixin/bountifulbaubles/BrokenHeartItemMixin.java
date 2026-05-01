package com.livajq.arcanetweaks.mixin.bountifulbaubles;

import com.jinqinxixi.bountifulbaubles.item.Baubles.BrokenHeartItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrokenHeartItem.class)
public abstract class BrokenHeartItemMixin {
    
    @Inject(method = "onLivingHurt", at = @At("HEAD"), cancellable = true, remap = false)
    private static void cancelLivingHurt(LivingHurtEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}