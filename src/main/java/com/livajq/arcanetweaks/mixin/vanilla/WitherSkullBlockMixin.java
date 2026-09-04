package com.livajq.arcanetweaks.mixin.vanilla;

import com.estie.arcanecontent.init.ArcaneSounds;
import com.livajq.arcanetweaks.ArcaneTweaks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WitherSkullBlock.class)
public abstract class WitherSkullBlockMixin {
    
    @Inject(
            method = "checkSpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"
            ),
            cancellable = true
    )
    private static void onWitherWouldSpawn(Level level, BlockPos pos, SkullBlockEntity be, CallbackInfo ci) {
        if (level.isClientSide) return;
        
        level.playSound(
                null,
                pos,
                ArcaneSounds.MRKRABS_SADGE.get(),
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
        
        AABB area = new AABB(pos).inflate(16);
        List<Player> players = level.getEntitiesOfClass(Player.class, area);
        
        Component msg = Component.translatable(ArcaneTweaks.MODID + ".wither_summon_tip").withStyle(style -> style.withColor(ChatFormatting.GOLD));
        
        for (Player p : players) {
            p.sendSystemMessage(msg);
        }
        
        ci.cancel();
    }
}
