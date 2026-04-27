package com.livajq.arcanetweaks.mixin.spartanweaponry;

import com.bawnorton.mixinsquared.TargetHandler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = Mob.class, priority = 1500)
public abstract class MobMixinSquared extends LivingEntity {
    protected MobMixinSquared(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // Mixin into the Mixin from SpartansWeaponry
    // For some reason, it complains about accessing a RandomSource from multiple threads, so we use the entity's RandomSource instead
    @TargetHandler(
            mixin = "com.oblivioussp.spartanweaponry.mixin.MobMixin",
            name = "attemptReplacingMainHandItemRandom"
    )
    @Redirect(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/oblivioussp/spartanweaponry/util/ItemRandomizer;generate(Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/item/ItemStack;"
            )
    )
    private ItemStack redirectItemRandomizer(Level level, List<Item> items) {
        return arcaneTweaks$generate(this.random, items);
    }


    @Unique
    private static ItemStack arcaneTweaks$generate(RandomSource random, List<Item> items) {
        float weaponRand = random.nextFloat();
        float divider = 1.0F / (float)items.size();
        int idx = Mth.floor(weaponRand / divider);
        idx = idx > items.size() - 1 ? items.size() - 1 : idx;
        return new ItemStack(items.get(idx));
    }
}
