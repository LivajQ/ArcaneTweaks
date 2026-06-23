package com.livajq.arcanetweaks.mixin.reskillable;

import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import net.bandit.reskillable.client.Tooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Tooltip.class, remap = false)
public class TooltipMixin {

    @Redirect(method = "onTooltipDisplay", at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/api/spells/ISpellContainer;getSpellAtIndex(I)Lio/redspace/ironsspellbooks/api/spells/SpellData;"))
    private SpellData fixIronsCrash(ISpellContainer instance, int i) {
        return instance != null ? instance.getSpellAtIndex(i) : null;
    }
}
