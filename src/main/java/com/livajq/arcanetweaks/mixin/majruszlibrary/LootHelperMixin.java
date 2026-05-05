package com.livajq.arcanetweaks.mixin.majruszlibrary;

import com.majruszlibrary.item.LootHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootHelper.class)
public abstract class LootHelperMixin {
    
    @Inject(method = "toGiftParams", at = @At("HEAD"), cancellable = true, remap = false)
    private static void fixNullEntity(Entity entity, CallbackInfoReturnable<LootParams> cir) {
        if (entity == null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            
            ServerLevel level = server.overworld();
            Entity dummy = new Zombie(level);

            LootParams params = new LootParams.Builder(level)
                    .withParameter(LootContextParams.THIS_ENTITY, dummy)
                    .withParameter(LootContextParams.ORIGIN, dummy.position())
                    .withLuck(0.0f)
                    .create(LootContextParamSets.GIFT);
            
            cir.setReturnValue(params);
        }
    }
}
