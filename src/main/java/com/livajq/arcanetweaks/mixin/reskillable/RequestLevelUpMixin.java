package com.livajq.arcanetweaks.mixin.reskillable;

import com.livajq.arcanetweaks.util.MajruszsUtils;
import com.livajq.arcanetweaks.util.ReskillableUtils;
import net.bandit.reskillable.common.capabilities.SkillModel;
import net.bandit.reskillable.common.commands.skills.Skill;
import net.bandit.reskillable.common.network.RequestLevelUp;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(value = RequestLevelUp.class, remap = false)
public class RequestLevelUpMixin {

    @Shadow @Final
    private int skillIndex;

    @Shadow
    private static Skill getSkillSafe(int idx) {
        throw new AssertionError();
    }

    @Inject(method = "lambda$handle$1", at = @At(value = "INVOKE", target = "Lnet/bandit/reskillable/common/network/RequestLevelUp;deductXp(Lnet/minecraft/server/level/ServerPlayer;I)V"), cancellable = true)
    public void checkGameStageLimit(NetworkEvent.Context ctx, CallbackInfo ci) {
        ServerPlayer player = ctx.getSender();

        if (player == null || player.isCreative()) return;
        if (!MajruszsUtils.isLoaded()) return;

        SkillModel model = SkillModel.get(player);
        Skill skill = getSkillSafe(skillIndex);

        int level = model.getSkillLevel(skill);
        int maxAllowed = ReskillableUtils.getMaxLevelForGamestage(player);

        if (level >= maxAllowed) {
            player.closeContainer();
            player.displayClientMessage(Component.literal("Maximum level for this mode is " + maxAllowed).withStyle(ChatFormatting.RED), true);
            ci.cancel();
        }
    }
}
