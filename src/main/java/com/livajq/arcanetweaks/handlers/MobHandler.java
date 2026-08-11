package com.livajq.arcanetweaks.handlers;

import cn.leolezury.eternalstarlight.common.EternalStarlight;
import com.github.L_Ender.cataclysm.world.data.CMWorldData;
import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.Config;
import insane96mcp.enhancedai.modules.mobs.Leaders;
import io.redspace.ironsspellbooks.api.entity.IOminousEntity;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.saksolm.monsterexpansion.entity.custom.AbstractLargeMonster;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID)
public class MobHandler {
    private static final ResourceKey<Level> STARLIGHT = ResourceKey.create(Registries.DIMENSION, EternalStarlight.id("starlight"));
    private static final TagKey<EntityType<?>> FORGE_BOSS = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("forge", "boss"));
    private static final UUID BOSS_HEALTH_BONUS_UUID = UUID.fromString("a3f1c9e2-7b4d-4e8a-9c1f-5d6b8e2a4f7c");
    private static final UUID BOSS_ARMOR_BONUS_UUID = UUID.fromString("b4e2d0f3-8c5e-4f9b-ad2f-6e7c9f3b5a8d");
    private static final UUID BOSS_TOUGHNESS_BONUS_UUID = UUID.fromString("c5f3e1a4-9d6f-4a8c-be3a-7f8d0a4c6b9e");
    
    //spawn ominous Irons bosses based on dimension
    @SubscribeEvent
    public static void handleOminousEntities(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel) || event.loadedFromDisk()) return;
        
        var entity = event.getEntity();
        if (entity instanceof IOminousEntity ominousSettings && !ominousSettings.isOminous() && ominousSettings.canTriggerOminous()) {
            if (serverLevel.dimension() == STARLIGHT) {
                Vec3 center = entity.position();
                ominousSettings.onOminousTrigger();
                serverLevel.playSound(null, BlockPos.containing(center), SoundRegistry.TRIAL_SPAWNER_OMINOUS_ACTIVATE.get(), SoundSource.BLOCKS, 4, 1.0F);
            }
        }
    }
    
    //set IgnisBossDefeatedOnce flag by killing drag instead
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (id != null && id.toString().equals("block_factorys_bosses:infernal_dragon")) {
            CMWorldData worldData = CMWorldData.get(entity.level(), Level.NETHER);
            if (worldData != null) {
                boolean prev = worldData.isIgnisDefeatedOnce();
                if (!prev) worldData.setIgnisDefeatedOnce(true);
            }
        }
    }
    
    //heal mobs by a certain amount whenever they kill another mob, full heal everything in the vicinity after 3 player deaths on a boss
    @SubscribeEvent
    public static void onLivingDeath2(LivingDeathEvent event) {
       /*
        if ((event.getEntity() instanceof ServerPlayer player)) {
            int deaths = player.getStats().getValue(Stats.CUSTOM, Stats.DEATHS);
            if (deaths > 0 && deaths % 3 == 0) massHeal(player);
            return;
        }
        */
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        if (attacker instanceof Player) return;
        attacker.heal((float) (attacker.getMaxHealth() * Config.mobKillHealAmount));
    }
    
    private static void massHeal(Player player) {
        double radius = 128;
        AABB area = player.getBoundingBox().inflate(radius);
        
        List<LivingEntity> nearby = player.level().getEntitiesOfClass(
                LivingEntity.class,
                area,
                entity -> !(entity instanceof Player)
                        && !(entity instanceof OwnableEntity ownable && ownable.getOwner() != null)
        );
        
        for (LivingEntity entity : nearby) {
            entity.heal(entity.getMaxHealth());
        }
    }
    
    //add more drops to leader mobs. Eclipse wanted it hardcoded idk
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!ModList.get().isLoaded("enhancedai")) return;
        if (!Leaders.isLeader(entity)) return;
        
        Level level = entity.level();
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        
        Map<Item, Integer> itemCounts = new HashMap<>();
        
        Item arcaneEssence = ForgeRegistries.ITEMS.getValue(new ResourceLocation("irons_spellbooks", "arcane_essence"));
        Item glowingPowder = ForgeRegistries.ITEMS.getValue(new ResourceLocation("trinketsandbaubles", "glowing_powder"));
        Item ectoplasm = ForgeRegistries.ITEMS.getValue(new ResourceLocation("goety", "ectoplasm"));
        if (arcaneEssence != null) itemCounts.put(arcaneEssence, entity.getRandom().nextInt(4) + 1);
        if (glowingPowder != null) itemCounts.put(glowingPowder, 1);
        if (ectoplasm != null) itemCounts.put(ectoplasm, entity.getRandom().nextInt(2) + 1);
        
        itemCounts.forEach((item, count) -> {
            ItemStack stack = new ItemStack(item, count);
            event.getDrops().add(new ItemEntity(level, x, y, z, stack));
        });
    }
    
    //Randomly replace a mob with a different one on the very first spawn
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (event.getLevel().isClientSide()) return;
        
        CompoundTag tag = entity.getPersistentData();
        if (tag.getBoolean("ArcaneTweaks_SpawnFlag")) return;
        tag.putBoolean("ArcaneTweaks_SpawnFlag", true);
        
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (id == null) return;
        
        for (Config.MobReplacement rule : Config.mobReplacements) {
            if (!rule.oldId().equals(id)) continue;
            
            if (entity.getRandom().nextDouble() > rule.chance()) continue;
            
            EntityType<?> newType = ForgeRegistries.ENTITY_TYPES.getValue(rule.newId());
            if (newType == null) {
                ArcaneTweaks.LOGGER.warn("Unknown replacement entity type: {}", rule.newId());
                continue;
            }
            
            Entity replacement = newType.create(entity.level());
            if (replacement == null) continue;
            
            replacement.setPos(entity.position());
            entity.level().addFreshEntity(replacement);
            
            event.setCanceled(true);
            return;
        }
    }
    
    //add scaling for bosses depending on the amount of players during first spawn
    @SubscribeEvent
    public static void onEntityJoin2(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (event.getLevel().isClientSide()) return;
        if (!entity.getType().is(FORGE_BOSS)) return;
        
        CompoundTag tag = entity.getPersistentData();
        if (tag.getBoolean("ArcaneTweaks_SpawnFlag")) return;
        tag.putBoolean("ArcaneTweaks_SpawnFlag", true);
        
        int nearbyPlayers = countNearbyPlayers(entity, 128);
        int extraPlayers = Math.max(0, nearbyPlayers - 1);
        if (extraPlayers == 0) return;
        
        entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(healthBonus(extraPlayers));
        if (entity.getAttributes().hasAttribute(Attributes.ARMOR))
            entity.getAttribute(Attributes.ARMOR).addPermanentModifier(armorBonus(extraPlayers));
        if (entity.getAttributes().hasAttribute(Attributes.ARMOR_TOUGHNESS))
            entity.getAttribute(Attributes.ARMOR_TOUGHNESS).addPermanentModifier(toughnessBonus(extraPlayers));
        
        entity.setHealth(entity.getMaxHealth());
    }
    
    //turn Monster Expansion mobs into tempered versions in specific biomes
    @SubscribeEvent
    public static void onEntityJoin3(EntityJoinLevelEvent event) {
        if (!ModList.get().isLoaded("monsterexpansion")) return;
        if (!(event.getEntity() instanceof AbstractLargeMonster<?, ?> monster)) return;
        if (event.getLevel().isClientSide()) return;
        
        Config.TemperedMonsterBiomes data = Config.temperedMonsterBiomes.get(ForgeRegistries.ENTITY_TYPES.getKey(monster.getType()));
        Holder<Biome> currentBiome = monster.level().getBiome(monster.blockPosition());
        ResourceLocation currentBiomeId = ForgeRegistries.BIOMES.getKey(currentBiome.get());
        
        if (data != null) {
            if (data.biomeIds().contains(currentBiomeId)
                    || currentBiome.tags().anyMatch(tag -> data.biomeTags().contains(tag.location().toString()))) {
                
                monster.setTempered(true);
            }
        }
    }
    
    private static int countNearbyPlayers(LivingEntity boss, double radius) {
        AABB area = boss.getBoundingBox().inflate(radius);
        return boss.level().getEntitiesOfClass(Player.class, area).size();
    }
    
    private static AttributeModifier healthBonus(int extraPlayers) {
        double amount = Config.bossMultiplayerHealthScaling * extraPlayers;
        return new AttributeModifier(BOSS_HEALTH_BONUS_UUID, "boss_health_bonus", amount, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
    
    private static AttributeModifier armorBonus(int extraPlayers) {
        double amount = Config.bossMultiplayerArmorScaling * extraPlayers;
        return new AttributeModifier(BOSS_ARMOR_BONUS_UUID, "boss_armor_bonus", amount, AttributeModifier.Operation.ADDITION);
    }
    
    private static AttributeModifier toughnessBonus(int extraPlayers) {
        double amount = Config.bossMultiplayerToughnessScaling * extraPlayers;
        return new AttributeModifier(BOSS_TOUGHNESS_BONUS_UUID, "boss_toughness_bonus", amount, AttributeModifier.Operation.ADDITION);
    }
    
    //fully heal the boss after 3 player deaths
    @SubscribeEvent
    public static void onLivingDeath3(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (Config.deathsForBossFullHeal <= 0) return;
        
        player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(128),
                entity -> entity.getType().is(FORGE_BOSS))
                .forEach(
                        entity -> {
                            int playerDeaths = entity.getPersistentData().getInt("ArcaneTweaks_PlayerDeathTracker") + 1;
                            if (playerDeaths % Config.deathsForBossFullHeal == 0) entity.setHealth(entity.getMaxHealth());
                            entity.getPersistentData().putInt("ArcaneTweaks_PlayerDeathTracker", playerDeaths);
                        }
                );
    }
}