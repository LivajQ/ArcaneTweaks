package com.livajq.arcanetweaks.mixin.eeeabsmobs;

import com.eeeab.eeeabsmobs.sever.world.portal.PortalStructureHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PortalStructureHelper.class)
public class PortalStructureHelperMixin {

    @Inject(method = "placePortalBlocks", at = @At("TAIL"), remap = false)
    private static void placePortalBlocks(ServerLevel level, BlockPos startPos, CallbackInfo ci) {
        level.playSound(null, startPos, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}