package com.livajq.arcanetweaks.mixin.eeeabsmobs;

import com.eeeab.eeeabsmobs.sever.item.util.ModArmorMaterial;
import net.minecraft.world.item.ArmorMaterial;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModArmorMaterial.class)
public abstract class ModArmorMaterialMixin implements ArmorMaterial {
    
    @Shadow
    @Final
    private String name;
    
    @Overwrite
    public String getName() {
        return name;
    }
}