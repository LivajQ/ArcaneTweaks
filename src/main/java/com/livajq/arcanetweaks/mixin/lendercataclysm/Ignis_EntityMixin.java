package com.livajq.arcanetweaks.mixin.lendercataclysm;

import com.github.L_Ender.cataclysm.entity.AnimationMonster.BossMonsters.Ignis_Entity;
import com.github.L_Ender.cataclysm.world.data.CMWorldData;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.message.UpdateBossEruptionStatus;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ignis_Entity.class)
public class Ignis_EntityMixin {
    
    @Redirect(
            method = "AfterDefeatBoss",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/github/L_Ender/cataclysm/world/data/CMWorldData;setIgnisDefeatedOnce(Z)V"
            ),
            remap = false
    )
    private void cancelIgnisFlag(CMWorldData instance, boolean value) {
        //skip
    }
    
    @Inject(method = "startSeenByPlayer", at = @At("TAIL"))
    private void addEruptionTracking(ServerPlayer player, CallbackInfo ci) {
        arcanetweaks$toggleServerEruptionStatusCopy(true);
    }
    
    @Inject(method = "stopSeenByPlayer", at = @At("TAIL"))
    private void removeEruptionTracking(ServerPlayer player, CallbackInfo ci) {
        arcanetweaks$toggleServerEruptionStatusCopy(false);
    }
    
    @Unique
    private void arcanetweaks$toggleServerEruptionStatusCopy(boolean erupting) {
        Ignis_Entity ignis = (Ignis_Entity)(Object)this;
        
        if (!ignis.level().isClientSide) {
            ACWorldData worldData = ACWorldData.get(ignis.level());
            if (worldData != null) {
                worldData.trackPrimordialBoss(ignis.getId(), erupting);
                AlexsCaves.sendMSGToAll(new UpdateBossEruptionStatus(ignis.getId(), worldData.isPrimordialBossActive(ignis.level())));
            }
        }
        
    }
}