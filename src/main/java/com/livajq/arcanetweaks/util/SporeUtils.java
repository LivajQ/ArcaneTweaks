package com.livajq.arcanetweaks.util;

import com.Harbinger.Spore.Sitems.BaseWeapons.*;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.livajq.arcanetweaks.Config;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SporeUtils {
    
    public static ImmutableMultimap.Builder<Attribute, AttributeModifier> applySporeArmorAttributes(EquipmentSlot slot, ArmorItem armor, ItemStack stack, Multimap<Attribute, AttributeModifier> modifiers) {
        
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(modifiers);
        
        if (slot != armor.getType().getSlot()) return builder;
        if (armor instanceof SporeBaseArmor) return builder;
        if (!(armor instanceof SporeArmorData data)) return builder;
        
        SporeArmorMutations variant = data.getVariant(stack);
        if (variant == SporeArmorMutations.DEFAULT) return builder;
        
        UUID uuid = UUID.nameUUIDFromBytes(("mutation_" + slot).getBytes());
        
        //double baseArmor = armor.getDefense();
        //double armorValue = data.calculateTrueDefense(stack, baseArmor) + modifyProtection(stack, baseArmor, data);
        //if (armorValue != 0.0D) builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", armorValue, AttributeModifier.Operation.ADDITION));
        //
        //double baseToughness = armor.getToughness();
        //double toughnessValue = data.calculateTrueToughness(stack, baseToughness) + modifyToughness(stack, baseToughness, data);
        //if (toughnessValue != 0.0D) builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor modifier", toughnessValue, AttributeModifier.Operation.ADDITION));
        
        if (variant == SporeArmorMutations.DROWNED) builder.put(ForgeMod.SWIM_SPEED.get(), new AttributeModifier(uuid, "Mutation swim speed", 0.25F, AttributeModifier.Operation.ADDITION));
        
        if (variant == SporeArmorMutations.REINFORCED || variant == SporeArmorMutations.SKELETAL) {
            double amount = (variant == SporeArmorMutations.REINFORCED) ? -0.01D : 0.01D;
            builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Mutation movement speed", amount, AttributeModifier.Operation.ADDITION));
        }
        
        return builder;
    }
    
    public static ImmutableMultimap.Builder<Attribute, AttributeModifier> applySporeWeaponAttributes(EquipmentSlot slot, SwordItem weapon, ItemStack stack, Multimap<Attribute, AttributeModifier> modifiers) {
        
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.putAll(modifiers);
 
        //if (weapon instanceof SporeToolsBaseItem) return builder;
        if (slot != EquipmentSlot.MAINHAND) return builder;
        if (!(weapon instanceof SporeWeaponData data)) return builder;
        
        SporeToolsMutations variant = data.getVariant(stack);
        if (variant == SporeToolsMutations.DEFAULT) return builder;
        
        UUID uuid = UUID.nameUUIDFromBytes(("mutation_" + slot).getBytes());
        
        double baseDamage = getAttributeValue(modifiers, Attributes.ATTACK_DAMAGE);
        double baseSpeed  = getAttributeValue(modifiers, Attributes.ATTACK_SPEED);
        double baseReach  = getAttributeValue(modifiers, ForgeMod.ENTITY_REACH.get());
        
        double newDamage = data.calculateTrueDamage(stack, baseDamage) + data.modifyDamage(stack, baseDamage);
        
        double newSpeed  = baseSpeed + data.modifyRecharge(stack);
        double newReach  = baseReach + data.modifyRange(stack);
        
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid, "Tool modifier", newDamage - baseDamage, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(uuid, "Tool modifier", newSpeed - baseSpeed, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(uuid, "Tool modifier", newReach - baseReach, AttributeModifier.Operation.ADDITION));
        
        return builder;
    }
    
    public static double modifyProtection(ItemStack stack, double value, SporeArmorData data) {
        SporeArmorMutations variant = data.getVariant(stack);
        if (variant == SporeArmorMutations.REINFORCED) return value * 0.2F;
        else if (variant == SporeArmorMutations.SKELETAL) return value * -0.2F;
        else return 0.0F;
    }
    
    public static double modifyToughness(ItemStack stack, double value, SporeArmorData data) {
        return data.getVariant(stack) == SporeArmorMutations.SKELETAL ? 1.0F : 0.0F;
    }
    
    public static float getDamageForMutation(ArrayList<ItemStack> pieces, DamageSource source, float value) {
        float amplifier = 1.0F;
        for (ItemStack piece : pieces) {
            if (!(piece.getItem() instanceof SporeArmorData data)) continue;
            if (data.getVariant(piece) == SporeArmorMutations.CHARRED && source.is(DamageTypeTags.IS_FIRE)) amplifier -= 0.25F;
            else if (data.getVariant(piece) == SporeArmorMutations.DROWNED && source.is(DamageTypeTags.IS_FIRE)) amplifier += 0.25F;
        }
        
        return value * amplifier;
    }
    
    public static Integer getArmorColor(ItemStack stack) {
        if (stack.getItem() instanceof SporeArmorData data) return data.getVariant(stack).getColor();
        else return null;
    }
    
    public static void applyArmorMutationEffects(Player player) {
        Map<Config.SporeMutationEffect, Integer> mutations = new HashMap<>();
        
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (!slot.isArmor()) continue;
            
            ItemStack stack = player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof SporeArmorData data)) continue;
            
            Config.SporeMutationEffect effect = getArmorEffect(stack, data);
            if (effect == null) continue;
            
            mutations.merge(effect, 1, Integer::sum);
        }
        
        applyEffects(player, mutations);
    }
    
    public static void applyWeaponMutationEffects(LivingEntity entity) {
        Map<Config.SporeMutationEffect, Integer> mutations = new HashMap<>();
        
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = entity.getItemInHand(hand);
            if (!(stack.getItem() instanceof SporeWeaponData data)) continue;
            
            Config.SporeMutationEffect effect = getWeaponEffect(stack, data);
            if (effect == null) continue;
            
            mutations.merge(effect, 1, Integer::sum);
        }
        
        applyEffects(entity, mutations);
    }
    
    private static void applyEffects(LivingEntity entity, Map<Config.SporeMutationEffect, Integer> mutations) {
        mutations.forEach((mutation, count) -> {
            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(mutation.id());
            if (effect == null) return;
            
            int amplifier = (mutation.amplifier() + 1) * count - 1;
            entity.addEffect(new MobEffectInstance(effect, mutation.duration(), amplifier, false, true));
        });
    }
    
    private static Config.SporeMutationEffect getArmorEffect(ItemStack stack, SporeArmorData data) {
        SporeArmorMutations variant = data.getVariant(stack);
        if (variant == null) return null;
        
        return switch (variant) {
            case REINFORCED -> Config.sporeMutationEffectReinforced;
            case SKELETAL -> Config.sporeMutationEffectSkeletal;
            case DROWNED -> Config.sporeMutationEffectDrowned;
            case CHARRED -> Config.sporeMutationEffectCharred;
            default -> null;
        };
    }
    
    private static Config.SporeMutationEffect getWeaponEffect(ItemStack stack, SporeWeaponData data) {
        SporeToolsMutations variant = data.getVariant(stack);
        if (variant == null) return null;
        
        return switch (variant) {
            case VAMPIRIC -> Config.sporeMutationEffectVampiric;
            case CALCIFIED -> Config.sporeMutationEffectCalcified;
            case BEZERK -> Config.sporeMutationEffectBezerk;
            case TOXIC -> Config.sporeMutationEffectToxic;
            case ROTTEN -> Config.sporeMutationEffectRotten;
            default -> null;
        };
    }
    
    private static double getAttributeValue(Multimap<Attribute, AttributeModifier> map, Attribute attribute) {
        return map.get(attribute).stream()
                .mapToDouble(AttributeModifier::getAmount)
                .sum();
    }
}