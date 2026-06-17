package com.livajq.arcanetweaks;

import com.Polarice3.Goety.utils.MathHelper;
import com.livajq.arcanetweaks.common.item.BlockItemOfSilly;
import com.livajq.arcanetweaks.mobs.MobStats;
import net.bandit.reskillable.common.commands.skills.SkillAttributeBonus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // =========================================================
    // Definitions
    // =========================================================
    
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRA_ALLIES;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRA_PLANT_SURFACES;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRA_PLANT_SURFACES_WATER;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOOD_TEMPERATURE_IMMUNITY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOOD_THIRST_IMMUNITY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> FOOD_EXHAUSTION_IMMUNITY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_ATTRIBUTE_MODIFIERS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_FREEZE_IMMUNITY;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EMI_RECIPE_CATEGORY_BLACKLIST;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EMI_RECIPE_WHITELIST;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DEATH_MESSAGES;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> DRAGON_NUKE_IMMUNE;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> LOST_CITIES_DOORS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> VILLAGER_BOOK_BLACKLIST;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENCHANTMENT_TIERS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> MOB_REPLACEMENTS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_SILLY_EFFECT_WEIGHT;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_SILLY_POSITIVE_EFFECTS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_SILLY_NEGATIVE_EFFECTS;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOCK_SILLY_ITEMS;
    
    private static final ForgeConfigSpec.ConfigValue<String> RITUAL_END_BIOMETAG;
    private static final ForgeConfigSpec.ConfigValue<String> RITUAL_ADEPT_NETHER_BIOMETAG;
    private static final ForgeConfigSpec.ConfigValue<String> RITUAL_EXPERT_NETHER_BIOMETAG;
    private static final ForgeConfigSpec.ConfigValue<String> APOSTLE_SUPPERBOSS_BIOME;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_ATTACK_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_GATHERING_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_MINING_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_FARMING_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_BUILDING_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_DEFENSE_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_AGILITY_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> RESKILLABLE_MAGIC_BONUS;
    private static final ForgeConfigSpec.ConfigValue<String> ENCHANTMENT_SECONDARY_COST_RANGE;
    private static final ForgeConfigSpec.ConfigValue<String> ENCHANTMENT_SECONDARY_COST_ITEM;
    private static final ForgeConfigSpec.ConfigValue<String> TRADING_COST_ITEM;
    private static final ForgeConfigSpec.ConfigValue<String> STARLIGHT_PORTAL_ITEM;
    
    private static final ForgeConfigSpec.BooleanValue HARDCORE_ICON_VISIBLE;
    private static final ForgeConfigSpec.BooleanValue NARRATOR_KEYBIND;
    private static final ForgeConfigSpec.BooleanValue BLOCK_SILLY_ENABLED;
    
    private static final ForgeConfigSpec.DoubleValue OBLITERATOR_DAMAGE_CAP;
    private static final ForgeConfigSpec.DoubleValue OBLITERATOR_GROUND_NUKE_DAMAGE_FLAT;
    private static final ForgeConfigSpec.DoubleValue OBLITERATOR_GROUND_NUKE_DAMAGE_PERCENT;
    private static final ForgeConfigSpec.DoubleValue RESISTANCE_AMOUNT;
    private static final ForgeConfigSpec.DoubleValue FIRE_RESISTANCE_AMOUNT;
    private static final ForgeConfigSpec.DoubleValue ICE_RESISTANCE_AMOUNT;
    private static final ForgeConfigSpec.DoubleValue LIGHTNING_RESISTANCE_AMOUNT;
    private static final ForgeConfigSpec.DoubleValue HARDCORE_ICON_POSX;
    private static final ForgeConfigSpec.DoubleValue HARDCORE_ICON_POSY;
    private static final ForgeConfigSpec.DoubleValue SEA_SERPENT_REACH;
    private static final ForgeConfigSpec.DoubleValue SOUL_FRACTURE_STRENGTH;
    private static final ForgeConfigSpec.DoubleValue BLOCK_SILLY_CHANCE;
    
    private static final ForgeConfigSpec.IntValue WORLDGEN_TYPE;
    private static final ForgeConfigSpec.IntValue HARDCORE_LIVES_COUNT;
    private static final ForgeConfigSpec.IntValue HARDCORE_ICON_SIZE;
    private static final ForgeConfigSpec.IntValue DRAGON_NUKE_COLOR_FIRE;
    private static final ForgeConfigSpec.IntValue DRAGON_NUKE_COLOR_ICE;
    private static final ForgeConfigSpec.IntValue DRAGON_NUKE_COLOR_LIGHTNING;
    private static final ForgeConfigSpec.IntValue BLOCK_STAMINA_CONSUME_BASE;
    private static final ForgeConfigSpec.IntValue BLOCK_STAMINA_CONSUME_EXTRA;
    private static final ForgeConfigSpec.IntValue BLOCK_STAMINA_CONSUME_MAX;
    private static final ForgeConfigSpec.IntValue BLOCK_BREAK_COOLDOWN_BASE;
    private static final ForgeConfigSpec.IntValue BLOCK_BREAK_COOLDOWN_EXTRA;
    private static final ForgeConfigSpec.IntValue BLOCK_BREAK_COOLDOWN_MAX;
    private static final ForgeConfigSpec.IntValue PARRY_WIND_UP;
    private static final ForgeConfigSpec.IntValue PARRY_WINDOW;
    private static final ForgeConfigSpec.IntValue PARRY_COOLDOWN_FAIL;
    private static final ForgeConfigSpec.IntValue PARRY_COOLDOWN_SUCCESS;
    private static final ForgeConfigSpec.IntValue GAMESTAGE_SKILL_CAP_NORMAL;
    private static final ForgeConfigSpec.IntValue GAMESTAGE_SKILL_CAP_EXPERT;
    private static final ForgeConfigSpec.IntValue GAMESTAGE_SKILL_CAP_MASTER;
    private static final ForgeConfigSpec.IntValue BLOCK_SILLY_GIVE_ITEMS_ROLLS;
    private static final ForgeConfigSpec.IntValue BLOCK_SILLY_STEAL_ITEMS_ROLLS;
    private static final ForgeConfigSpec.IntValue BLOCK_SILLY_VISUALS_RADIUS;
    private static final ForgeConfigSpec.IntValue BLOCK_SILLY_EFFECT_ROLLS;
   
    static {
        BUILDER.push("Mobs");
        
        BUILDER.push("Dread Mobs");
        EXTRA_ALLIES = BUILDER
                .comment("Entities treated as allies by dread mobs")
                .defineListAllowEmpty(
                        List.of("dreadMobAllies"),
                        List.of(
                                "minecraft:villager",
                                "minecraft:wolf"
                        ),
                        o -> o instanceof String
                );
        BUILDER.pop();
        
        BUILDER.push("Mob freeze immunity");
        MOB_FREEZE_IMMUNITY = BUILDER
                .comment("Mobs immune to the freeze effect")
                .defineListAllowEmpty(
                        List.of("mobFreezeImmunity"),
                        List.of(
                                "minecraft:villager",
                                "minecraft:wolf"
                        ),
                        o -> o instanceof String
                );
        
        BUILDER.pop();
        
        BUILDER.push("Mob attribute modifiers");
        BUILDER.comment("Attribute (and tick speed) multipliers for mobs. 1.0 is default (100%) scaling");
        
        MOB_ATTRIBUTE_MODIFIERS = BUILDER
                .comment("Format: id - stat1=val stat2=val stat3=val",
                        "Example: iceandfire:dread_beast - attack=1.3 armor=0.5 health=1.2 speed=2.0 follow=1.35 tick=1.25"
                )
                .defineListAllowEmpty(
                        List.of("mobAttributeModifiers"),
                        List.of(
                                "iceandfire:dread_beast - attack=1.3 armor=0.5 health=1.2 speed=2.0 follow=1.35",
                                "minecraft:wolf - health=2.0"
                        ),
                        o -> o instanceof String
                );

        BUILDER.pop();
        
        BUILDER.push("Dragons");
        
        DRAGON_NUKE_COLOR_FIRE = BUILDER.comment("Fire dragon nuke color").defineInRange("dragonNukeColorFire", 0xD30E0E, 0x000000, 0xFFFFFF);
        DRAGON_NUKE_COLOR_ICE = BUILDER.comment("Ice dragon nuke color").defineInRange("dragonNukeColorIce", 0x0AB8EA, 0x000000, 0xFFFFFF);
        DRAGON_NUKE_COLOR_LIGHTNING = BUILDER.comment("Lightning dragon nuke color").defineInRange("dragonNukeColorLightning", 0xFF00EA, 0x000000, 0xFFFFFF);
        
        DRAGON_NUKE_IMMUNE = BUILDER
                .comment("Mobs immune to dragon nuke"
                )
                .defineListAllowEmpty(
                        List.of("dragonNukeImmune"),
                        List.of(
                                "minecraft:villager",
                                "minecraft:wolf"
                        ),
                        o -> o instanceof String
                );
        
        BUILDER.pop();

        BUILDER.push("Mob replacements");
        BUILDER.comment("Can be used to add a chance for an entity to be replaced with a different entity on the first spawn");

        MOB_REPLACEMENTS = BUILDER
                .comment("Format: oldID;newID;chance",
                        "Example: minecraft:skeleton;minecraft:zombie;0.1"
                )
                .defineListAllowEmpty(
                        List.of("mobReplacements"),
                        List.of(
                                "minecraft:skeleton;minecraft:zombie;0.1",
                                "minecraft:enderman;minecraft:creeper;0.05"
                        ),
                        o -> o instanceof String
                );

        BUILDER.pop();
        
        SEA_SERPENT_REACH = BUILDER.comment("Attack reach bonus for sea serpents. 0.5 = 50% extra reach etc.").defineInRange("seaSerpentReach", 1.0D, -1000.0D, 1000.0D);
        
        BUILDER.pop();
        BUILDER.push("Rituals");
        
        RITUAL_END_BIOMETAG = BUILDER.comment("Biome tag that allows the 'End' ritual to be performed").define("ritualEndBiome", "forge:is_plains");
        RITUAL_ADEPT_NETHER_BIOMETAG = BUILDER.comment("Biome tag that allows the 'Adept Nether' ritual to be performed").define("ritualAdeptNetherBiome", "minecraft:is_forest");
        RITUAL_EXPERT_NETHER_BIOMETAG = BUILDER.comment("Biome tag that allows the 'Expert Nether' ritual to be performed").define("ritualExpertNetherBiome", "minecraft:is_taiga");
        
        BUILDER.pop();
        BUILDER.push("canSustainPlant Rules");
        BUILDER.comment("The following can be used to add additional blocks for plants to be placed on");
        
        EXTRA_PLANT_SURFACES = BUILDER
                .comment(
                        "Format: plant - block1, block2, block3...",
                        "Example: betterend:dragon_tree_sapling - minecraft:stone, minecraft:grass_block, minecraft:end_stone"
                )
                .defineListAllowEmpty(
                        List.of("extraPlantSurfaces"),
                        List.of("betterend:dragon_tree_sapling - minecraft:stone, minecraft:grass_block, minecraft:end_stone",
                                "betterend:tenanea_sapling - minecraft:obsidian, minecraft:prismarine"
                        ),
                        o -> o instanceof String
                );
        
        EXTRA_PLANT_SURFACES_WATER = BUILDER
                .comment(
                        "Use this list for plants meant to only be placed underwater"
                )
                .defineListAllowEmpty(
                        List.of("extraPlantSurfacesWater"),
                        List.of("betterend:hydralux_sapling - minecraft:stone, minecraft:grass_block"
                        ),
                        o -> o instanceof String
                );
        
        BUILDER.pop();
        
        BUILDER.push("Bosses");
        
        BUILDER.push("Apostle");
        APOSTLE_SUPPERBOSS_BIOME = BUILDER.comment("Biome in which a special variant of the Apostle boss can spawn").define("apostleSuperbossBiome", "biomesoplenty:volcano");
        BUILDER.pop();
        
        BUILDER.push("Obliterator");
        OBLITERATOR_DAMAGE_CAP = BUILDER.comment("Maximum damage the Obliterator boss can receive per hit").defineInRange("obliteratorDamageCap", 200.0D, 0.0D, 1000000.0D);
        OBLITERATOR_GROUND_NUKE_DAMAGE_FLAT = BUILDER.comment("Flat damage bonus for the ground nuke attack. Also affects the Tesseract").defineInRange("obliteratorGroundNukeDamageFlat", 100.0D, -1000000.0D, 1000000.0D);
        OBLITERATOR_GROUND_NUKE_DAMAGE_PERCENT = BUILDER.comment("Bonus damage based on target's maximum health for the ground nuke attack. 0.5 = 50% of target's max health dealt as extra damage. Also affects the Tesseract").defineInRange("obliteratorGroundNukeDamagePercent", 0.1D, -1000.0D, 1000.0D);
        BUILDER.pop();
        
        BUILDER.pop();
        
        BUILDER.push("Food Bonuses");
        
        FOOD_TEMPERATURE_IMMUNITY = BUILDER
                .comment("Food items that apply temperature immunity")
                .defineListAllowEmpty(
                        List.of("foodTemperatureImmunity"),
                        List.of(
                                "minecraft:bread",
                                "minecraft:apple"
                        ),
                        o -> o instanceof String
                );
        
        FOOD_THIRST_IMMUNITY = BUILDER
                .comment("Food items that apply thirst immunity")
                .defineListAllowEmpty(
                        List.of("foodThirstImmunity"),
                        List.of(
                                "minecraft:carrot",
                                "minecraft:potato"
                        ),
                        o -> o instanceof String
                );
        
        FOOD_EXHAUSTION_IMMUNITY = BUILDER
                .comment("Food items that apply exhaustion immunity")
                .defineListAllowEmpty(
                        List.of("foodExhaustionImmunity"),
                        List.of(
                                "cavedelight:musubi"
                        ),
                        o -> o instanceof String
                );
        
        BUILDER.pop();
        
        BUILDER.push("Reskillable attributes and bonuses");
        BUILDER.comment("Attribute bonus that should be applied for each skill. NONE for no bonus, DEFAULT to use default (reskillable) ones");
        
        RESKILLABLE_ATTACK_BONUS = BUILDER.define("attack", "minecraft:generic.luck");
        RESKILLABLE_GATHERING_BONUS = BUILDER.define("gathering", "DEFAULT");
        RESKILLABLE_MINING_BONUS = BUILDER.define("mining", "DEFAULT");
        RESKILLABLE_FARMING_BONUS = BUILDER.define("farming", "DEFAULT");
        RESKILLABLE_BUILDING_BONUS = BUILDER.define("building", "DEFAULT");
        RESKILLABLE_DEFENSE_BONUS = BUILDER.define("defense", "DEFAULT");
        RESKILLABLE_AGILITY_BONUS = BUILDER.define("agility", "DEFAULT");
        RESKILLABLE_MAGIC_BONUS = BUILDER.define("magic", "DEFAULT");
        
        GAMESTAGE_SKILL_CAP_NORMAL = BUILDER.comment("Maximum skill level in Normal Mode").defineInRange("gamestageSkillCapNormal", 16, 1, 50);
        GAMESTAGE_SKILL_CAP_EXPERT = BUILDER.comment("Maximum skill level in Expert Mode").defineInRange("gamestageSkillCapExpert", 32, 1, 50);
        GAMESTAGE_SKILL_CAP_MASTER = BUILDER.comment("Maximum skill level in Master Mode").defineInRange("gamestageSkillCapMaster", 50, 1, 50);
        
        BUILDER.pop();
        
        BUILDER.push("EMI");
        
        EMI_RECIPE_CATEGORY_BLACKLIST = BUILDER
                .comment("Blacklisted EMI recipe categories. Categories on this list will not show up in-game")
                .defineListAllowEmpty(
                        List.of("emiRecipeCategoryBlacklist"),
                        List.of(
                                "emi:anvil_repairing",
                                "irons_spellbooks:arcane_anvil",
                                "emi:grinding"
                        ),
                        o -> o instanceof String
                );
        
        EMI_RECIPE_WHITELIST = BUILDER
                .comment("EMI recipes that should be added even if their category is blacklisted",
                        "NOTE: Substring based, not id based (or it would've been extremely painful)",
                        "'iron_sword' will whitelist all iron sword recipes. repairing/minecraft/ will whitelist vanilla anvil repairing etc. etc.")
                .defineListAllowEmpty(
                        List.of("emiRecipeWhitelist"),
                        List.of(
                                "iron_sword",
                                "grind",
                                "minecraft:golden_apple"
                        ),
                        o -> o instanceof String
                );
        
        BUILDER.pop();
        
        BUILDER.push("Hardcore");
        
        HARDCORE_LIVES_COUNT = BUILDER.comment("Amount of lives the player has in hardcore mode").defineInRange("hardcoreLivesCount", 5, 1, 2137);
        HARDCORE_ICON_VISIBLE = BUILDER.comment("Should the hardcore heart icon be displayed on the screen").define("hardcoreIconVisible", true);
        HARDCORE_ICON_SIZE = BUILDER.comment("Hardcore icon size").defineInRange("hardcoreIconSize", 16, 4, 1024);
        HARDCORE_ICON_POSX = BUILDER.comment("Hardcore icon position X (0-100%)").defineInRange("hardcoreIconPosX", 2.0D, 0.1D, 99.9D);
        HARDCORE_ICON_POSY = BUILDER.comment("Hardcore icon position Y (0-100%)").defineInRange("hardcoreIconPosY", 2.0D, 0.1D, 99.9D);
        
        BUILDER.pop();
        
        BUILDER.push("Enchantments and villagers");
        
        VILLAGER_BOOK_BLACKLIST = BUILDER
                .comment("List of enchanted books that villagers cannot trade")
                .defineListAllowEmpty(
                        List.of("villagerBookBlacklist"),
                        List.of(
                                "minecraft:sharpness",
                                "minecraft:unbreaking"
                        ),
                        o -> o instanceof String
                );
        
        ENCHANTMENT_TIERS = BUILDER
                .comment("Tiers for enchantments. Currently used in villager trading to determine which books can be traded at which level",
                        "Tiers go from 1 to 5 (novice to master villager)")
                .defineListAllowEmpty(
                        List.of("enchantmentTiers"),
                        List.of(
                                "minecraft:mending;4",
                                "minecraft:flame;2"
                        ),
                        o -> o instanceof String
                );
        
        ENCHANTMENT_SECONDARY_COST_RANGE = BUILDER.comment("Price range multiplier for the secondary item in enchanted book trades. Depends on the emerald cost",
                "Example: range=0.5-1.5 means the cost can be anything from 50% to 150% of the emerald cost").define("enchantmentSecondaryCostRange", "0.5-1.5");
        
        ENCHANTMENT_SECONDARY_COST_ITEM = BUILDER.comment("Secondary item required for trading enchanted books (replaces the single book requirement)").define("enchantmentSecondaryCostItem", "kubejs:diamond_ingot");
        TRADING_COST_ITEM = BUILDER.comment("Secondary item required for non enchanted book trades. Applies to villagers selling diamond gear").define("tradingSecondaryCostItem", "kubejs:diamond_ingot");
        
        BUILDER.pop();
        
        BUILDER.push("Parrying and blocking");
        BUILDER.comment("Affects Spartan Weaponry melee block trait");
        
        BLOCK_STAMINA_CONSUME_BASE = BUILDER.comment("Base stamina cost for blocking").defineInRange("blockStaminaConsumeBase", 200, 0, 1000000);
        BLOCK_STAMINA_CONSUME_EXTRA = BUILDER.comment("Additional stamina cost for blocking per incoming damage point").defineInRange("blockStaminaConsumeExtra", 50, 0, 1000000);
        BLOCK_STAMINA_CONSUME_MAX = BUILDER.comment("Maximum stamina cost for blocking").defineInRange("blockStaminaConsumeMax", 1000, 0, 1000000);
        BLOCK_BREAK_COOLDOWN_BASE = BUILDER.comment("Base block cooldown (in ticks) from block disabling attackers").defineInRange("blockBreakCooldownBase", 60, 0, 1000000);
        BLOCK_BREAK_COOLDOWN_EXTRA = BUILDER.comment("Additional block cooldown (in ticks) from block disabling attackers per incoming damage point").defineInRange("blockBreakCooldownExtra", 10, 0, 1000000);
        BLOCK_BREAK_COOLDOWN_MAX = BUILDER.comment("Maximum block cooldown (in ticks) from block disabling attackers").defineInRange("blockBreakCooldownMax", 200, 0, 1000000);
        PARRY_WIND_UP = BUILDER.comment("Wind up (in ticks) after blocking before parrying can be performed").defineInRange("parryWindUp", 8, 0, 1000000);
        PARRY_WINDOW = BUILDER.comment("Window (in ticks) in which enemies can be parried").defineInRange("parryWindow", 20, 0, 1000000);
        PARRY_COOLDOWN_FAIL = BUILDER.comment("Cooldown (in ticks) after an unsuccessful parry before another one can be performed. Used to prevent spamming").defineInRange("parryCooldownFail", 30, 0, 1000000);
        PARRY_COOLDOWN_SUCCESS = BUILDER.comment("Cooldown (in ticks) after a successful parry before another one can be performed").defineInRange("parryCooldownSuccess", 200, 0, 1000000);
        
        BUILDER.pop();
        
        BUILDER.push("Resistances");
        
        RESISTANCE_AMOUNT = BUILDER.comment("Damage reduced by the resistance effect per level (0 - 1)").defineInRange("resistanceAmount", 0.1D, 0.0D, 1.0D);
        FIRE_RESISTANCE_AMOUNT = BUILDER.comment("Fire damage reduced by the fire resistance effect per level (0 - 1)").defineInRange("fireResistanceAmount", 0.1D, 0.0D, 1.0D);
        ICE_RESISTANCE_AMOUNT = BUILDER.comment("Ice damage reduced by the ice resistance effect per level (0 - 1)").defineInRange("iceResistanceAmount", 0.1D, 0.0D, 1.0D);
        LIGHTNING_RESISTANCE_AMOUNT = BUILDER.comment("Lightning damage reduced by the lightning resistance effect per level (0 - 1)").defineInRange("lightningResistanceAmount", 0.1D, 0.0D, 1.0D);
        
        BUILDER.pop();
        
        BUILDER.push("Block of Silly");
        
        BLOCK_SILLY_ENABLED = BUILDER.comment("Whether the Block of Silly can generate in the world").define("blockSillyEnabled", true);
        BLOCK_SILLY_CHANCE = BUILDER.comment("Chance for the Block of Silly to replace a random ore block during generation (in %)").defineInRange("blockSillyChance", 0.1D, 0.0D, 100.0D);
        BLOCK_SILLY_EFFECT_ROLLS = BUILDER.comment("How many effects are rolled per one use of the block").defineInRange("blockSillyEffectRolls", 1, 1, 50);
        BLOCK_SILLY_VISUALS_RADIUS = BUILDER.comment("Radius (in blocks) in which the Block of Silly can apply certain visual effects").defineInRange("blockSillyVisualsRadius", 15, 0, 100);
        
        BLOCK_SILLY_EFFECT_WEIGHT = BUILDER
                .comment("Weight for each Block of Silly effect. Bigger = more common. 0 = off",
                        "This option automatically lists all available effects on first load",
                        "You can delete this option and restart the game to generate them again (if more appear with updates or just to restore them)")
                .defineListAllowEmpty(
                        List.of("blockSillyEffectWeight"),
                        Arrays.stream(BlockItemOfSilly.SillyEffect.values())
                                .map(e -> e.getId() + "=" + e.getDefaultWeight())
                                .collect(Collectors.toList()),
                        o -> o instanceof String s && s.matches("[a-z_]+=\\d+")
                );
        
        BLOCK_SILLY_POSITIVE_EFFECTS = BUILDER
                .comment("Potion effects applied when the 'positive potion effects' effect is rolled",
                        "Format: effect;amplifier;duration ticks",
                        "Example: minecraft:strength;1;300")
                .defineListAllowEmpty(
                        List.of("blockSillyPositiveEffects"),
                        List.of("minecraft:strength;1;300",
                                "minecraft:haste;2;300",
                                "minecraft:regeneration;5;300"),
                        o -> o instanceof String
                );
        
        BLOCK_SILLY_NEGATIVE_EFFECTS = BUILDER
                .comment("Potion effects applied when the 'negative potion effects' effect is rolled",
                        "Format: effect;amplifier;duration ticks",
                        "Example: minecraft:poison;1;300")
                .defineListAllowEmpty(
                        List.of("blockSillyNegativeEffects"),
                        List.of("minecraft:poison;1;300",
                                "minecraft:wither;2;300",
                                "minecraft:slowness;5;300"),
                        o -> o instanceof String
                );
        
        BLOCK_SILLY_ITEMS = BUILDER
                .comment("List from which items will be rolled when the 'give items' effect is rolled",
                        "Format: item;count",
                        "Example: minecraft:diamond;7")
                .defineListAllowEmpty(
                        List.of("blockSillyItems"),
                        List.of("minecraft:diamond;7",
                                "minecraft:gold_ingot;15",
                                "minecraft:ender_pearl;5"),
                        o -> o instanceof String
                );
        
        BLOCK_SILLY_GIVE_ITEMS_ROLLS = BUILDER.comment("Number of rolls for the 'give items' effect").defineInRange("blockSillyGiveItemRolls", 3, 1, 100);
        BLOCK_SILLY_STEAL_ITEMS_ROLLS = BUILDER.comment("Number of rolls for the 'steal items' effect").defineInRange("blockSillyStealItemRolls", 3, 1, 100);
        
        BUILDER.pop();
        
        BUILDER.push("Misc");
        
        WORLDGEN_TYPE = BUILDER
                .comment("World generation type",
                        "0: Default (vanilla), 1: Arcane, 2: Biome Blend")
                .defineInRange("worldgenType", 1, 0, 2);
        
        DEATH_MESSAGES = BUILDER
                .comment("Extra messages that appear on the death screen")
                .defineListAllowEmpty(
                        List.of("deathMessages"),
                        List.of(
                                "Rip bozo",
                                "Most unfortunate",
                                "When monument?"
                        ),
                        o -> o instanceof String
                );
        
        LOST_CITIES_DOORS = BUILDER
                .comment("List of doors to choose from when generating Lost Cities buildings",
                        "The same ID can be included multiple times to make it more common e.g. 'oak', 'oak', 'spruce' = 66% for oak, 33% for spruce door")
                .defineListAllowEmpty(
                        List.of("lostCitiesDoors"),
                        List.of(
                                "minecraft:oak_door",
                                "minecraft:birch_door",
                                "minecraft:iron_door"
                        ),
                        o -> o instanceof String
                );
        
        NARRATOR_KEYBIND = BUILDER.comment("If for some reason you want to bring the narrator keybind back, set this to true").define("narratorKeybind", false);
        
        SOUL_FRACTURE_STRENGTH = BUILDER.comment("Maximum health reduction per level of the Soul Fracture effect (1.0 = 100%)").defineInRange("soulFractureStrength", 0.02D, 0.0D, 1.0D);
        
        STARLIGHT_PORTAL_ITEM = BUILDER.comment("Item used to activate the portal leading to the Starlight dimensionn").define("starlightPortalItem", "minecraft:enchanted_golden_apple");
      
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
    
    // =========================================================
    // Runtime values
    // =========================================================
    
    public static Set<String> extraAlliesSet;
    public static Set<String> foodTemperatureImmunitySet;
    public static Set<String> foodThirstImmunitySet;
    public static Set<String> foodExhaustionImmunitySet;
    public static Set<String> mobFreezeImmunitySet;
    public static Set<String> emiRecipeCategoryBlacklistSet;
    public static Set<String> emiRecipeWhitelistSet;
    public static Set<String> dragonNukeImmuneSet;
    public static Set<String> villagerBookBlacklistSet;
 
    public static Map<ResourceLocation, Set<ResourceLocation>> extraPlantSurfaces = new HashMap<>();
    public static Map<ResourceLocation, Set<ResourceLocation>> extraPlantSurfacesWater = new HashMap<>();
    public static Map<SkillAttributeBonus, Supplier<Attribute>> reskillableAttributeBonuses = new HashMap<>();
    public static Map<EntityType<?>, MobStats> mobAttributeModifiers = new HashMap<>();
    public static Map<ResourceLocation, Integer> enchantmentTiers = new HashMap<>();
    public static Map<BlockItemOfSilly.SillyEffect, Integer> blockSillyEffectWeight;
 
    public static List<String> deathMessages;
    public static List<String> lostCitiesDoors;
    public static List<String> blockSillyPositiveEffects;
    public static List<String> blockSillyNegativeEffects;
    public static List<String> blockSillyItems;
    public static List<MobReplacement> mobReplacements;
    
    public static ResourceKey<Biome> apostleSuperbossBiome;
    public static ResourceLocation enchantmentSecondaryCostItem;
    public static ResourceLocation tradingCostItem;
    public static ResourceLocation starlightPortalItem;
    public static Range enchantmentSecondaryCost;
    public static TagKey<Biome> ritualEndBiome;
    public static TagKey<Biome> ritualAdeptNetherBiome;
    public static TagKey<Biome> ritualExpertNetherBiome;
    
    public static boolean hardcoreIconVisible;
    public static boolean narratorKeybind;
    public static boolean blockSillyEnabled;
    
    public static double obliteratorDamageCap;
    public static double obliteratorGroundNukeDamageFlat;
    public static double obliteratorGroundNukeDamagePercent;
    public static double resistanceAmount;
    public static double fireResistanceAmount;
    public static double iceResistanceAmount;
    public static double lightningResistanceAmount;
    public static double hardcoreIconPosX;
    public static double hardcoreIconPosY;
    public static double seaSerpentReach;
    public static double soulFractureStrength;
    public static double blockSillyChance;
    
    public static int worldgenType;
    public static int hardcoreLivesCount;
    public static int hardcoreIconSize;
    public static int dragonNukeColorFire;
    public static int dragonNukeColorIce;
    public static int dragonNukeColorLightning;
    public static int blockStaminaConsumeBase;
    public static int blockStaminaConsumeExtra;
    public static int blockStaminaConsumeMax;
    public static int blockBreakCooldownBase;
    public static int blockBreakCooldownExtra;
    public static int blockBreakCooldownMax;
    public static int parryWindUp;
    public static int parryWindow;
    public static int parryCooldownFail;
    public static int parryCooldownSuccess;
    public static int gamestageSkillCapNormal;
    public static int gamestageSkillCapExpert;
    public static int gamestageSkillCapMaster;
    public static int blockSillyGiveItemsRolls;
    public static int blockSillyStealItemsRolls;
    public static int blockSillyVisualsRadius;
    public static int blockSillyEffectRolls;
    
    // =========================================================
    // Sync
    // =========================================================
    
    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) return;
        
        extraAlliesSet = new HashSet<>(EXTRA_ALLIES.get());
        foodTemperatureImmunitySet = new HashSet<>(FOOD_TEMPERATURE_IMMUNITY.get());
        foodThirstImmunitySet = new HashSet<>(FOOD_THIRST_IMMUNITY.get());
        foodExhaustionImmunitySet = new HashSet<>(FOOD_EXHAUSTION_IMMUNITY.get());
        mobFreezeImmunitySet = new HashSet<>(MOB_FREEZE_IMMUNITY.get());
        emiRecipeCategoryBlacklistSet = new HashSet<>(EMI_RECIPE_CATEGORY_BLACKLIST.get());
        emiRecipeWhitelistSet = new HashSet<>(EMI_RECIPE_WHITELIST.get());
        dragonNukeImmuneSet = new HashSet<>(DRAGON_NUKE_IMMUNE.get());
        villagerBookBlacklistSet = new HashSet<>(VILLAGER_BOOK_BLACKLIST.get());
        extraPlantSurfaces = parsePlantSurfaces(false);
        extraPlantSurfacesWater = parsePlantSurfaces(true);
        reskillableAttributeBonuses = parseReskillableBonuses();
        mobAttributeModifiers = parseMobAttributeModifiers();
        enchantmentTiers = parseEnchantmentTiers();
        deathMessages = new ArrayList<>(DEATH_MESSAGES.get());
        lostCitiesDoors = new ArrayList<>(LOST_CITIES_DOORS.get());
        mobReplacements = parseMobReplacements();
        ritualEndBiome = TagKey.create(Registries.BIOME, new ResourceLocation(RITUAL_END_BIOMETAG.get()));
        ritualAdeptNetherBiome = TagKey.create(Registries.BIOME, new ResourceLocation(RITUAL_ADEPT_NETHER_BIOMETAG.get()));
        ritualExpertNetherBiome = TagKey.create(Registries.BIOME, new ResourceLocation(RITUAL_EXPERT_NETHER_BIOMETAG.get()));
        apostleSuperbossBiome = ResourceKey.create(Registries.BIOME, new ResourceLocation(APOSTLE_SUPPERBOSS_BIOME.get()));
        worldgenType = Mth.clamp(WORLDGEN_TYPE.get(), 0, 2);
        obliteratorDamageCap = OBLITERATOR_DAMAGE_CAP.get();
        obliteratorGroundNukeDamageFlat = OBLITERATOR_GROUND_NUKE_DAMAGE_FLAT.get();
        obliteratorGroundNukeDamagePercent = OBLITERATOR_GROUND_NUKE_DAMAGE_PERCENT.get();
        resistanceAmount = RESISTANCE_AMOUNT.get();
        fireResistanceAmount = FIRE_RESISTANCE_AMOUNT.get();
        iceResistanceAmount = ICE_RESISTANCE_AMOUNT.get();
        lightningResistanceAmount = LIGHTNING_RESISTANCE_AMOUNT.get();
        hardcoreLivesCount = Math.max(HARDCORE_LIVES_COUNT.get(), 1);
        hardcoreIconVisible = HARDCORE_ICON_VISIBLE.get();
        hardcoreIconSize = HARDCORE_ICON_SIZE.get();
        hardcoreIconPosX = MathHelper.clamp(HARDCORE_ICON_POSX.get(), 0.0D, 99.0D);
        hardcoreIconPosY  = MathHelper.clamp(HARDCORE_ICON_POSY.get(), 0.0D, 99.0D);
        seaSerpentReach = SEA_SERPENT_REACH.get();
        dragonNukeColorFire = DRAGON_NUKE_COLOR_FIRE.get();
        dragonNukeColorIce = DRAGON_NUKE_COLOR_ICE.get();
        dragonNukeColorLightning = DRAGON_NUKE_COLOR_LIGHTNING.get();
        enchantmentSecondaryCost = parseEnchantmentSecondaryCost(ENCHANTMENT_SECONDARY_COST_RANGE.get(), 0.5F, 2.0F);
        enchantmentSecondaryCostItem = new ResourceLocation(ENCHANTMENT_SECONDARY_COST_ITEM.get());
        tradingCostItem = new ResourceLocation(TRADING_COST_ITEM.get());
        blockStaminaConsumeBase = BLOCK_STAMINA_CONSUME_BASE.get();
        blockStaminaConsumeExtra = BLOCK_STAMINA_CONSUME_EXTRA.get();
        blockStaminaConsumeMax = BLOCK_STAMINA_CONSUME_MAX.get();
        blockBreakCooldownBase = BLOCK_BREAK_COOLDOWN_BASE.get();
        blockBreakCooldownExtra = BLOCK_BREAK_COOLDOWN_EXTRA.get();
        blockBreakCooldownMax = BLOCK_BREAK_COOLDOWN_MAX.get();
        parryWindUp = PARRY_WIND_UP.get();
        parryWindow = PARRY_WINDOW.get();
        parryCooldownFail = PARRY_COOLDOWN_FAIL.get();
        parryCooldownSuccess = PARRY_COOLDOWN_SUCCESS.get();
        gamestageSkillCapNormal = GAMESTAGE_SKILL_CAP_NORMAL.get();
        gamestageSkillCapExpert = Math.max(gamestageSkillCapNormal, GAMESTAGE_SKILL_CAP_EXPERT.get());
        gamestageSkillCapMaster = Math.max(gamestageSkillCapExpert, GAMESTAGE_SKILL_CAP_MASTER.get());
        narratorKeybind = NARRATOR_KEYBIND.get();
        soulFractureStrength = SOUL_FRACTURE_STRENGTH.get();
        starlightPortalItem = new ResourceLocation(STARLIGHT_PORTAL_ITEM.get());
        blockSillyEffectWeight = parseBlockSillyEffectWeight();
        blockSillyPositiveEffects = new ArrayList<>(BLOCK_SILLY_POSITIVE_EFFECTS.get());
        blockSillyNegativeEffects = new ArrayList<>(BLOCK_SILLY_NEGATIVE_EFFECTS.get());
        blockSillyEnabled = BLOCK_SILLY_ENABLED.get();
        blockSillyChance = BLOCK_SILLY_CHANCE.get();
        blockSillyItems = new ArrayList<>(BLOCK_SILLY_ITEMS.get());
        blockSillyGiveItemsRolls = BLOCK_SILLY_GIVE_ITEMS_ROLLS.get();
        blockSillyStealItemsRolls = BLOCK_SILLY_STEAL_ITEMS_ROLLS.get();
        blockSillyVisualsRadius =  BLOCK_SILLY_VISUALS_RADIUS.get();
        blockSillyEffectRolls = BLOCK_SILLY_EFFECT_ROLLS.get();
    }
    
    // =========================================================
    // Helpers
    // =========================================================
    
    private static Map<ResourceLocation, Set<ResourceLocation>> parsePlantSurfaces(boolean water) {
        Map<ResourceLocation, Set<ResourceLocation>> map = new HashMap<>();
        
        List<? extends String> surfaces = water ? EXTRA_PLANT_SURFACES_WATER.get() : EXTRA_PLANT_SURFACES.get();
        for (String line : surfaces) {
            String[] split = line.split("-");
            if (split.length != 2) continue;
            
            ResourceLocation plantId = new ResourceLocation(split[0].trim());
            
            Set<ResourceLocation> blocks = Arrays.stream(split[1].split(","))
                    .map(String::trim)
                    .map(ResourceLocation::new)
                    .collect(Collectors.toSet());
            
            map.put(plantId, blocks);
        }
        return map;
    }
    
    private static Map<SkillAttributeBonus, Supplier<Attribute>> parseReskillableBonuses() {
        Map<SkillAttributeBonus, Supplier<Attribute>> map = new HashMap<>();
        
        parseSkill(map, SkillAttributeBonus.ATTACK, RESKILLABLE_ATTACK_BONUS.get());
        parseSkill(map, SkillAttributeBonus.GATHERING, RESKILLABLE_GATHERING_BONUS.get());
        parseSkill(map, SkillAttributeBonus.MINING, RESKILLABLE_MINING_BONUS.get());
        parseSkill(map, SkillAttributeBonus.FARMING, RESKILLABLE_FARMING_BONUS.get());
        parseSkill(map, SkillAttributeBonus.BUILDING, RESKILLABLE_BUILDING_BONUS.get());
        parseSkill(map, SkillAttributeBonus.DEFENSE, RESKILLABLE_DEFENSE_BONUS.get());
        parseSkill(map, SkillAttributeBonus.AGILITY, RESKILLABLE_AGILITY_BONUS.get());
        parseSkill(map, SkillAttributeBonus.MAGIC, RESKILLABLE_MAGIC_BONUS.get());
        
        return map;
    }
    
    private static void parseSkill(Map<SkillAttributeBonus, Supplier<Attribute>> map, SkillAttributeBonus bonus, String id) {
        if (id.equalsIgnoreCase("DEFAULT")) return;
        
        if (id.equalsIgnoreCase("NONE")) {
            map.put(bonus, () -> null);
            return;
        }
        
        ResourceLocation rl = new ResourceLocation(id);
        Attribute attr = ForgeRegistries.ATTRIBUTES.getValue(rl);
        
        if (attr == null) {
            ArcaneTweaks.LOGGER.warn("[ArcaneTweaks] Invalid attribute ID in config: " + id + " for skill " + bonus.name());
            map.put(bonus, () -> null);
            return;
        }
        
        map.put(bonus, () -> attr);
    }
    
    private static Map<EntityType<?>, MobStats> parseMobAttributeModifiers() {
        Map<EntityType<?>, MobStats> map = new HashMap<>();
        
        for (String line : MOB_ATTRIBUTE_MODIFIERS.get()) {
            if (!line.contains("-")) continue;
            
            String[] split = line.split("-", 2);
            String idPart = split[0].trim();
            String statsPart = split[1].trim();
            
            ResourceLocation id = new ResourceLocation(idPart);
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(id);
            if (type == null) {
                ArcaneTweaks.LOGGER.warn("[ArcaneTweaks] Unknown entity type in config: " + id);
                continue;
            }
            
            double attack = 1, armor = 1, health = 1, speed = 1, follow = 1, tick = 1;
            
            for (String token : statsPart.split(" ")) {
                if (!token.contains("=")) continue;
                
                String[] kv = token.split("=", 2);
                String key = kv[0];
                double val = Double.parseDouble(kv[1]);
                
                switch (key) {
                    case "attack" -> attack = val;
                    case "armor" -> armor = val;
                    case "health" -> health = val;
                    case "speed" -> speed = val;
                    case "follow" -> follow = val;
                    case "tick" -> tick = val;
                }
            }
            
            map.put(type, new MobStats(attack, armor, health, speed, follow, tick));
        }
        return map;
    }
    
    public static Map<ResourceLocation, Integer> parseEnchantmentTiers() {
        Map<ResourceLocation, Integer> map = new HashMap<>();
        
        for (String entry : ENCHANTMENT_TIERS.get()) {
            String[] parts = entry.split(";");
            if (parts.length != 2) continue;
            
            ResourceLocation id = new ResourceLocation(parts[0]);
            int tier = Integer.parseInt(parts[1]);
            
            map.put(id, tier);
        }
        return map;
    }
    
    public static Range parseEnchantmentSecondaryCost(String s, float defaultMin, float defaultMax) {
        if (s == null) return new Range(defaultMin, defaultMax);
        
        String[] parts = s.trim().split("-");
        if (parts.length != 2) return new Range(defaultMin, defaultMax);
        
        try {
            float min = Float.parseFloat(parts[0].trim());
            float max = Float.parseFloat(parts[1].trim());
            
            if (min > max) {
                float tmp = min;
                min = max;
                max = tmp;
            }
            
            min = Math.max(0f, min);
            max = Math.max(min, max);
            
            return new Range(min, max);
            
        } catch (NumberFormatException e) {
            return new Range(defaultMin, defaultMax);
        }
    }

    public static List<MobReplacement> parseMobReplacements() {
        List<MobReplacement> list = new ArrayList<>();

        for (String line : MOB_REPLACEMENTS.get()) {
            String[] parts = line.split(";");

            try {
                ResourceLocation oldId = new ResourceLocation(parts[0]);
                ResourceLocation newId = new ResourceLocation(parts[1]);
                double chance = Double.parseDouble(parts[2]);

                if (chance < 0 || chance > 1) {
                    throw new IllegalArgumentException("Chance must be between 0 and 1: " + line);
                }

                list.add(new MobReplacement(oldId, newId, chance));
            } catch (Exception e) {
                ArcaneTweaks.LOGGER.warn("Invalid mob replacement entry '{}': {}", line, e.getMessage());
            }
        }

        return list;
    }
    
    public static Map<BlockItemOfSilly.SillyEffect, Integer> parseBlockSillyEffectWeight() {
        return BLOCK_SILLY_EFFECT_WEIGHT.get().stream()
                .map(s -> s.split("="))
                .filter(parts -> parts.length == 2)
                .flatMap(parts -> {
                    try {
                        int weight = Integer.parseInt(parts[1].trim());
                        return Arrays.stream(BlockItemOfSilly.SillyEffect.values())
                                .filter(e -> e.getId().equals(parts[0].trim()))
                                .map(e -> Map.entry(e, weight));
                    } catch (NumberFormatException e) {
                        ArcaneTweaks.LOGGER.warn("Invalid weight in blockSillyEffectWeight: {}", parts[1]);
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    public record Range(float min, float max) {}
    public record MobReplacement(ResourceLocation oldId, ResourceLocation newId, double chance) {}
   
    public record SillyEffects(ResourceLocation effect, int amplifier, int duration) {
        public static SillyEffects parse(String s) {
            String[] parts = s.split(";");
            return new SillyEffects(
                    new ResourceLocation(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            );
        }
        
        public static List<SillyEffects> getPositiveEffects() {
            return blockSillyPositiveEffects.stream()
                    .map(s -> parse((String) s))
                    .toList();
        }
        
        public static List<SillyEffects> getNegativeEffects() {
            return blockSillyNegativeEffects.stream()
                    .map(s -> parse((String) s))
                    .toList();
        }
    }
    public record SillyItems(ResourceLocation item, int count) {
        public static SillyItems parse(String s) {
            String[] parts = s.split(";");
            return new SillyItems(
                    new ResourceLocation(parts[0]),
                    Integer.parseInt(parts[1])
            );
        }
        
        public static List<SillyItems> getItems() {
            return Config.blockSillyItems.stream()
                    .map(s -> SillyItems.parse((String) s))
                    .toList();
        }
    }
}