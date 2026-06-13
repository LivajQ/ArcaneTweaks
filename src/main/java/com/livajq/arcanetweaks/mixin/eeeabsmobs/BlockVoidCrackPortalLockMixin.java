package com.livajq.arcanetweaks.mixin.eeeabsmobs;

import com.eeeab.eeeabsmobs.sever.block.BlockVoidCrackPortalLock;
import com.eeeab.eeeabsmobs.sever.world.portal.PortalStructureHelper;
import com.livajq.arcanetweaks.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockVoidCrackPortalLock.class)
public class BlockVoidCrackPortalLockMixin {
    
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void useCheck(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        Item heldItem = player.getItemInHand(hand).getItem();
        Item requiredItem = ForgeRegistries.ITEMS.getValue(Config.starlightPortalItem);
        if (heldItem == requiredItem && !player.isShiftKeyDown()) {
            cir.setReturnValue(!level.isClientSide && PortalStructureHelper.tryActivatePortalFromLock(level, pos) ? InteractionResult.SUCCESS : InteractionResult.sidedSuccess(level.isClientSide));
        }
        else {
            if (level.isClientSide) player.displayClientMessage(Component.translatable("arcanetweaks.starlight_portal_fail"), true);
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}