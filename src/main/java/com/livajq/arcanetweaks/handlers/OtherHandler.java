package com.livajq.arcanetweaks.handlers;

import com.gametechbc.traveloptics.entity.mobs.nightwarden_boss.NightwardenBossEntity;
import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.Config;
import com.livajq.arcanetweaks.init.ArcaneSounds;
import com.livajq.arcanetweaks.mixin.vanilla.ChunkGeneratorAccessor;
import com.livajq.arcanetweaks.world.district.DistrictBiomeSource;
import com.mojang.blaze3d.systems.RenderSystem;
import com.ordana.spelunkery.reg.ModFluids;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID)
public class OtherHandler {
    
    //private static final ResourceKey<Level> ABYSS = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("lostworlds", "abyss"));
    private static final UUID HER_ID = UUID.fromString("7905095f-4e96-43d1-83a0-870265821205");
    private static final UUID WOMPWOMP_ID = UUID.fromString("9b65f606-23d8-428e-a769-5817ca979faf");
    private static final String WOMPWOMP_NAME = "Therealcaprisun";
    private static final ResourceLocation MEME = new ResourceLocation(ArcaneTweaks.MODID, "textures/misc/lol.png");
    private static final Map<Player, Integer> ENTERED_DISMOUNT_BIOME_TICK = new HashMap<>();
    
    //certain eyes used as dimension teleporters instead. 1 eye now. and nothing now lol
    /*
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Item item = event.getItemStack().getItem();
        if (item != ItemInit.BLOODY_ALTAR_EYE.get()) return;
        event.setCanceled(true);
        Player player = event.getEntity();
        if (player.level().isClientSide() || !player.isCrouching()) return;
        
        ServerPlayer serverPlayer = (ServerPlayer) player;
        MinecraftServer server = serverPlayer.getServer();
        if (server == null) return;
        
        ResourceKey<Level> targetDim = ABYSS;
        if (serverPlayer.level().dimension() == targetDim) return;
        
        ServerLevel targetLevel = server.getLevel(targetDim);
        if (targetLevel == null) return;
        
        serverPlayer.changeDimension(targetLevel, new ITeleporter() {
            @Override
            public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
                Entity e = repositionEntity.apply(false);

                BlockPos spawn = destWorld.getSharedSpawnPos();
                Vec3 pos = Vec3.atBottomCenterOf(spawn);
                
                e.teleportTo(pos.x, pos.y, pos.z);
                return e;
            }
        });
    }
     */
    
    //replace vanilla BiomeSource
    //I probably just don't know how to do it like a normal person but json overrides caused modded biomes to stop generating
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLevelLoad(LevelEvent.Load event) {
        //if (Config.worldgenType == 0) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        
        replaceOverworldBiomeSource(level);
    }
    
    private static void replaceOverworldBiomeSource(ServerLevel level) {
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        
        BiomeSource vanilla = generator.getBiomeSource();
        if (vanilla instanceof DistrictBiomeSource) return;
        BiomeSource wrapped = new DistrictBiomeSource(vanilla, level.getSeed());

        ((ChunkGeneratorAccessor) generator).setBiomeSource(wrapped);
    }

    //constantly spawn particles on the player model
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Player player = event.player;
        if (player == null) return;
        
        if (player.tickCount % 2 != 0) return;
        if (!player.getUUID().equals(HER_ID)) return;
        
        if (!player.level().isClientSide) {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            ServerLevel level = serverPlayer.serverLevel();
            
            AABB box = player.getBoundingBox();
            
            for (ServerPlayer other : level.players()) {
                if (other == serverPlayer) continue;
                
                double x = randomInside(box.minX, box.maxX);
                double y = randomInside(box.minY, box.maxY);
                double z = randomInside(box.minZ, box.maxZ);

                level.sendParticles(
                        other,
                        ParticleTypes.GLOW,
                        false,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0
                );
            }
            spawnWings(player, 1.0F);
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (player == mc.player && !mc.options.getCameraType().isFirstPerson()) {
            
            AABB box = player.getBoundingBox();
            
            double x = randomInside(box.minX, box.maxX);
            double y = randomInside(box.minY, box.maxY);
            double z = randomInside(box.minZ, box.maxZ);

            player.level().addParticle(
                    ParticleTypes.GLOW,
                    x, y, z,
                    0, 0, 0
            );
            
            spawnWings(player, Minecraft.getInstance().getFrameTime());
        }
    }
    
    private static double randomInside(double min, double max) {
        return min + Math.random() * (max - min);
    }
    
    private static void spawnWings(Player player, float partialTick) {
        
        float yaw = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot);
        double rad = Math.toRadians(yaw);
        
        Vec3 pos = player.position();
        
        double baseX = pos.x + Math.sin(rad) * 0.25;
        double baseY = pos.y + 1.2;
        double baseZ = pos.z - Math.cos(rad) * 0.25;
        
        Level level = player.level();
        
        for (int i = 0; i < 10; i++) {
            
            double t = i / 10.0;
            
            double x = 0.2 + t * 1.2;
            double y = Math.sin(t * Math.PI) * 0.8;
            
            spawnWingParticle(player, level, baseX, baseY, baseZ, x, y, rad);
            spawnWingParticle(player, level, baseX, baseY, baseZ, -x, y, rad);
        }
    }
    
    private static void spawnWingParticle(Player player, Level level, double baseX, double baseY, double baseZ, double x, double y, double rad) {
        double rx = x * Math.cos(rad);
        double rz = x * Math.sin(rad);
        
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer other : serverLevel.players()) {
                if (other == player) continue;
                
                serverLevel.sendParticles(
                        
                        other,
                        new DustParticleOptions(new Vector3f(1.0f, 0.85f, 0.2f), 1.2f),
                        false,
                        baseX + rx,
                        baseY + y,
                        baseZ + rz,
                        1, 0, 0, 0, 0
                );
            }
        }
        
        else level.addParticle(
                new DustParticleOptions(new Vector3f(1.0f, 0.85f, 0.2f), 1.2f),
                baseX + rx,
                baseY + y,
                baseZ + rz,
                0, 0, 0
        );
    }
    
    //periodically check the biome the player is in and dismount in blacklisted ones
    @SubscribeEvent
    public static void onPlayerTick2(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Player player = event.player;
        if (player == null) return;
        if (player.tickCount % 20 != 0) return;
        if (player.level().isClientSide) return;
        if (!player.isPassenger()) return;
    
        boolean blacklisted = isDismountBiome(player);
        
        if (blacklisted) {
            if (ENTERED_DISMOUNT_BIOME_TICK.containsKey(player)) {
                if (player.tickCount - ENTERED_DISMOUNT_BIOME_TICK.get(player) > 120) {
                    LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, player.level());
                    lightning.setPos(player.position());
                    player.level().addFreshEntity(lightning);
                    player.displayClientMessage(Component.translatable(ArcaneTweaks.MODID + ".dismount_strike_" + (player.getRandom().nextInt(5) + 1))
                            .withStyle(style -> style.withColor(ChatFormatting.GOLD)), true);
                    player.stopRiding();
                }
            }
            else {
                ENTERED_DISMOUNT_BIOME_TICK.put(player, player.tickCount);
                player.displayClientMessage(Component.translatable(ArcaneTweaks.MODID + ".dismount_warn_" + (player.getRandom().nextInt(5) + 1))
                        .withStyle(style -> style.withColor(ChatFormatting.GOLD)), true);
            }
        }
    }
    
    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (!(event.getEntityMounting() instanceof Player player)) return;
        if (!(event.getEntityBeingMounted() instanceof LivingEntity)) return;
        if (event.isDismounting()) return;
        
        if (isDismountBiome(player)) {
            event.setCanceled(true);
            if (!event.getLevel().isClientSide) player.displayClientMessage(Component.translatable(ArcaneTweaks.MODID + ".cannot_mount_here")
                    .withStyle(style -> style.withColor(ChatFormatting.GOLD)), true);
        }
        else ENTERED_DISMOUNT_BIOME_TICK.remove(player);
    }
    
    private static boolean isDismountBiome(Player player) {
        Set<String> ids = Config.dismountBiomesSet.stream().filter(s -> !s.startsWith("#")).collect(Collectors.toSet());
        Set<String> tags = Config.dismountBiomesSet.stream().filter(s -> s.startsWith("#")).map(s -> s.substring(1)).collect(Collectors.toSet());
        
        Holder<Biome> biome = player.level().getBiome(player.blockPosition());
        ResourceLocation id = biome.unwrapKey().orElseThrow().location();
        
        return ids.contains(id.toString())
                || biome.tags().anyMatch(tag -> tags.contains(tag.location().toString()));
    }
    
    //apply effects when player is in portal liquid from Spelunkery
    @SubscribeEvent
    public static void applyFluidEffects(LivingEvent.LivingTickEvent event) {
        if (!ModList.get().isLoaded("spelunkery")) return;
        
        LivingEntity entity = event.getEntity();
        if (entity.tickCount % 10 != 0) return;
        
        FluidType portal = ModFluids.PORTAL_FLUID.get().getFluidType();
        FluidType portalFlowing = ModFluids.FLOWING_PORTAL_FLUID.get().getFluidType();
 
        if (entity.isInFluidType(portal) || entity.isInFluidType(portalFlowing)) {
            MobEffect abyssalBurn = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("cataclysm:abyssal_burn"));
            MobEffect abyssalCurse = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("cataclysm:abyssal_curse"));
            MobEffect abyssalFear = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("cataclysm:abyssal_fear"));
            MobEffect sapped = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("goety:sapped"));
            if (abyssalBurn != null) entity.addEffect(new MobEffectInstance(abyssalBurn, 110, 0, false, true), entity);
            if (abyssalCurse != null) entity.addEffect(new MobEffectInstance(abyssalCurse, 110, 0, false, true), entity);
            if (abyssalFear != null) entity.addEffect(new MobEffectInstance(abyssalFear, 110, 0, false, true), entity);
            if (sapped != null) entity.addEffect(new MobEffectInstance(sapped, 110, 0, false, true), entity);
        }
    }
    
    //invalidate a certain run :3
    @SubscribeEvent
    public static void onLivingDeathFun(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof NightwardenBossEntity)) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!player.getUUID().equals(WOMPWOMP_ID) && !player.getGameProfile().getName().equals(WOMPWOMP_NAME)) return;
        if (player.isCreative()) return;
        CompoundTag tag = player.getPersistentData();
        
        tag.putInt("ArcaneTweaks_RunInvalid_1", 100);
    }
    
    //shit code but whatever. temporary
    @SubscribeEvent
    public static void onPlayerTickFun(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (!player.getUUID().equals(WOMPWOMP_ID) && !player.getGameProfile().getName().equals(WOMPWOMP_NAME)) return;
        
        CompoundTag tag = player.getPersistentData();
        
        if (!tag.contains("ArcaneTweaks_GoofyScreen")) {
            ItemStack main = player.getMainHandItem();
            if (main.getItem() == Items.DEAD_BUSH) {
                if (main.getHoverName().getString().equals("Cringe, brother")) tag.putInt("ArcaneTweaks_GoofyScreen", 100);
            }
            else if (player.getMaxHealth() >= 1000) tag.putInt("ArcaneTweaks_GoofyScreen", 100);
        }
        
        if (event.side == LogicalSide.CLIENT) return;
        
        if (!tag.contains("ArcaneTweaks_RunInvalid_1")) return;
        
        if (tag.getInt("ArcaneTweaks_RunInvalid_1") == 0) {
            tag.putInt("ArcaneTweaks_RunInvalid_2", 100);
            tag.putInt("ArcaneTweaks_RunInvalid_1", Integer.MAX_VALUE);
            invalid1(player);
        }
        else tag.putInt("ArcaneTweaks_RunInvalid_1",  tag.getInt("ArcaneTweaks_RunInvalid_1") - 1);
        
        if (!tag.contains("ArcaneTweaks_RunInvalid_2")) return;
        
        if (tag.getInt("ArcaneTweaks_RunInvalid_2") == 0) {
            tag.putInt("ArcaneTweaks_RunInvalid_3", 60);
            tag.putInt("ArcaneTweaks_RunInvalid_2", Integer.MAX_VALUE);
            invalid2(player);
        }
        else tag.putInt("ArcaneTweaks_RunInvalid_2",  tag.getInt("ArcaneTweaks_RunInvalid_2") - 1);
        
        if (!tag.contains("ArcaneTweaks_RunInvalid_3")) return;
        
        if (tag.getInt("ArcaneTweaks_RunInvalid_3") == 0) {
            tag.putInt("ArcaneTweaks_RunInvalid_3", Integer.MAX_VALUE);
            invalid3(player);
        }
        else tag.putInt("ArcaneTweaks_RunInvalid_3",  tag.getInt("ArcaneTweaks_RunInvalid_3") - 1);
    }
    
    private static void invalid1(Player player) {
        player.sendSystemMessage(Component.literal("Oh wow, you are the first person to beat the pack, congrats! You can now ask Velkhana for a reward..."));
    }
    
    private static void invalid2(Player player) {
        player.sendSystemMessage(Component.literal("Oh wait! You cheated, nevermind RUN INVALID HAHAHHAhHSFHEUOGIHtgh231trwtazg"));
    }
    
    private static void invalid3(Player player) {
        Inventory inv = player.getInventory();
        ItemStack deadbush = new ItemStack(Items.DEAD_BUSH);
        deadbush.setHoverName(Component.literal("Cringe, brother"));
        
        for (int i = 0; i < inv.getContainerSize(); i++) {
            inv.setItem(i, deadbush.copy());
        }
        
        inv.setChanged();
        
        MobEffect madness = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("spore", "madness"));
        MobEffect marker = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("spore", "marker"));
        MobEffect hex = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("goety", "burn_hex"));
        MobEffect irradiated  = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("alexscaves", "irradiated"));
        
        if (madness != null) player.addEffect(new MobEffectInstance(madness, Integer.MAX_VALUE, 0));
        if (marker != null) player.addEffect(new MobEffectInstance(marker, Integer.MAX_VALUE, 0));
        if (hex != null) player.addEffect(new MobEffectInstance(hex, Integer.MAX_VALUE, 0));
        if (irradiated != null) player.addEffect(new MobEffectInstance(irradiated, Integer.MAX_VALUE, 0));
        
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                ArcaneSounds.FUNNEHSOUND.get(), SoundSource.PLAYERS, 1f, 1f);
        
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(696969);
        player.setHealth(696969);
    }
    
    //it's bugged but became even funnier so refactored to intentional
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        
        CompoundTag tag = player.getPersistentData();
        if (!tag.contains("ArcaneTweaks_GoofyScreen")) return;
       
        float alpha;
        if (player.tickCount % 2 == 0) alpha = 1.0f;
        else alpha = 0.0f;
        
        GuiGraphics gui = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        
        gui.blit(MEME, 0, 0, 0, 0, w, h, w, h);
        
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }
}