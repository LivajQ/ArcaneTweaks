package com.livajq.arcanetweaks.mixin.alexscaves;

import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ACWorldData.class)
public abstract class ACWorldDataMixin {
    
    @Shadow
    private Set<Integer> trackedLuxtructosaurusIds;
    
    @Inject(
            method = "isPrimordialBossActive",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void arcane$allowAnyBoss(Level level, CallbackInfoReturnable<Boolean> cir) {
        for (int id : trackedLuxtructosaurusIds) {
            Entity e = level.getEntity(id);
            
            if (e instanceof LivingEntity entity) {
                if (entity.isAlive() && isLoadedInWorld(entity)) cir.setReturnValue(true);
            }
        }
    }
    
    @Unique
    private boolean isLoadedInWorld(LivingEntity entity) {
        return entity.level().hasChunk(SectionPos.blockToSectionCoord(entity.getX()), SectionPos.blockToSectionCoord(entity.getZ()));
    }
}
