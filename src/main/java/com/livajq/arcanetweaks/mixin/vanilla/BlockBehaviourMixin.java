package com.livajq.arcanetweaks.mixin.vanilla;

import cn.leolezury.eternalstarlight.common.data.ESDimensions;
import cn.leolezury.eternalstarlight.common.world.ESTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    
    @Inject(method = "entityInside", at = @At("HEAD"))
    private void teleportStarlight(BlockState state, Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        Block self = (Block)(Object)this;
        if (!self.getClass().getName().equals("com.eeeab.eeeabsmobs.sever.block.BlockVoidCrackPortal")) return;
        
        if (!(entity instanceof ServerPlayer)) return;
        
        ServerLevel currentLevel = (ServerLevel) level;
        ResourceKey<Level> destination = currentLevel.dimension() == ESDimensions.STARLIGHT_KEY
                ? Level.OVERWORLD : ESDimensions.STARLIGHT_KEY;
        
        ServerLevel destinationLevel = currentLevel.getServer().getLevel(destination);
        if (destinationLevel == null) return;
        
        ESTeleporter teleporter = new ESTeleporter(entity, pos, destinationLevel);
        if (!teleporter.isValid()) return;
        
        entity.setPortalCooldown();
        
        Entity result = entity.changeDimension(destinationLevel, teleporter);
        if (result != null) {
            result.setPortalCooldown();
            destinationLevel.getChunkSource().addRegionTicket(
                    TicketType.PORTAL,
                    new ChunkPos(teleporter.getTarget()),
                    3,
                    teleporter.getTarget()
            );
        }
    }
}