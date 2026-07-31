package com.livajq.arcanetweaks.mixin.majruszlibrary;

import com.majruszlibrary.entity.AttributeHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(AttributeHandler.class)
public abstract class AttributeHandlerMixin {
    
    @Shadow
    @Final
    Supplier<Attribute> attribute;
    
    @Inject(
            method = "apply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addTransientModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"
            ),
            remap = false,
            cancellable = true
    )
    private void patchPermanentModifier(LivingEntity entity, CallbackInfoReturnable<AttributeHandler> cir) {
        AttributeHandler self = (AttributeHandler)(Object)this;
        
        AttributeInstance inst = entity.getAttribute(attribute.get());
        if (inst == null) return;
       
        inst.removeModifier(self.getUUID());
 
        inst.addPermanentModifier(self.createAttribute());

        cir.cancel();
    }
}
