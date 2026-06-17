package com.livajq.arcanetweaks.common.item;

import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.Config;
import com.livajq.arcanetweaks.handlers.PacketHandler;
import com.livajq.arcanetweaks.packet.StartSillyRainbowEffectPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class BlockItemOfSilly extends BlockItem {
    
    public BlockItemOfSilly(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }
    
    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("block.arcanetweaks.block_of_silly",
                Component.literal("Silly").withStyle(ChatFormatting.YELLOW));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(
                Component.translatable("block.arcanetweaks.block_of_silly.desc.part1")
                        .append(Component.translatable("block.arcanetweaks.block_of_silly.desc.silly")
                                .withStyle(ChatFormatting.YELLOW))
                        .append(Component.translatable("block.arcanetweaks.block_of_silly.desc.part2"))
                        .append(Component.translatable("block.arcanetweaks.block_of_silly.desc.positive")
                                .withStyle(ChatFormatting.GREEN))
                        .append(Component.translatable("block.arcanetweaks.block_of_silly.desc.part3"))
                        .append(Component.translatable("block.arcanetweaks.block_of_silly.desc.negative")
                                .withStyle(ChatFormatting.RED))
        );
    }
    
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown()) return super.place(context);
        if (context.getLevel().isClientSide()) return InteractionResult.SUCCESS;
        ServerLevel level = (ServerLevel) context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!player.getAbilities().instabuild) context.getItemInHand().shrink(1);
        for (int x = 0; x < Config.blockSillyEffectRolls; x++) doRandomSilly(level, pos, player);
        return InteractionResult.CONSUME;
    }
    
    private void doRandomSilly(ServerLevel level, BlockPos pos, Player player) {
        Map<SillyEffect, Integer> weights = Config.blockSillyEffectWeight;
        
        int totalWeight = Arrays.stream(SillyEffect.values())
                .mapToInt(e -> weights.getOrDefault(e, e.getDefaultWeight()))
                .filter(w -> w > 0)
                .sum();
        
        if (totalWeight == 0) return;
        
        int roll = level.getRandom().nextInt(totalWeight);
        
        for (SillyEffect effect : SillyEffect.values()) {
            int weight = weights.getOrDefault(effect, effect.getDefaultWeight());
            if (weight <= 0) continue;
            roll -= weight;
            if (roll < 0) {
                applyEffect(effect, level, pos, player);
                return;
            }
        }
    }
    
    private void applyEffect(SillyEffect effect, ServerLevel level, BlockPos pos, Player player) {
        player.sendSystemMessage(Component.translatable("block.arcanetweaks.block_of_silly.effect." + effect.getId()));
        
        switch (effect) {
            case NUKE -> {
                NuclearBombEntity nuke = new NuclearBombEntity(ACEntityRegistry.NUCLEAR_BOMB.get(), level);
                nuke.setPos(player.getPosition(1));
                level.addFreshEntity(nuke);
            }
            
            case GIVE_ITEM -> {
                List<Config.SillyItems> items = Config.SillyItems.getItems();
                if (items.isEmpty()) break;
                for (int i = 0; i < Config.blockSillyGiveItemsRolls; i++) {
                    Config.SillyItems rolled = items.get(level.getRandom().nextInt(items.size()));
                    Item item = ForgeRegistries.ITEMS.getValue(rolled.item());
                    if (item != null && item != Items.AIR) {
                        player.addItem(new ItemStack(item, rolled.count()));
                    }
                }
            }
            
            case STEAL_ITEM -> {
                List<ItemStack> stealable = new ArrayList<>();
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (!stack.isEmpty() && !stack.is(this)) stealable.add(stack);
                }
                player.getHandSlots().forEach(s -> { if (!s.isEmpty() && !s.is(this)) stealable.add(s); });
                player.getArmorSlots().forEach(s -> { if (!s.isEmpty()) stealable.add(s); });
                
                if (stealable.size() < Config.blockSillyStealItemsRolls) {
                    player.sendSystemMessage(Component.translatable("block.arcanetweaks.block_of_silly.effect.steal_item.fail"));
                    Handler.lungTimers.put(player.getUUID(), 12000);
                }
                for (int i = 0; i < Config.blockSillyStealItemsRolls && !stealable.isEmpty(); i++) {
                    ItemStack target = stealable.remove(level.getRandom().nextInt(stealable.size()));
                    target.setCount(0);
                }
            }
            
            case POSITIVE_POTION_EFFECTS ->
                Config.SillyEffects.getPositiveEffects()
                        .forEach(e -> {
                            MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(e.effect());
                            if (mobEffect != null) {
                                player.addEffect(new MobEffectInstance(mobEffect, e.duration(), e.amplifier() - 1));
                            }
                        });
            
            case NEGATIVE_POTION_EFFECTS ->
                    Config.SillyEffects.getNegativeEffects()
                            .forEach(e -> {
                                MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(e.effect());
                                if (mobEffect != null) {
                                    player.addEffect(new MobEffectInstance(mobEffect, e.duration(), e.amplifier() - 1));
                                }
                            });
            
            case ZEUS -> Handler.zeusTimers.put(player.getUUID(), 2400);
            
            case RAINBOW -> PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new StartSillyRainbowEffectPacket(3600));
        }
    }
    
    public enum SillyEffect {
        NUKE("nuke", 1),
        GIVE_ITEM("give_item", 2),
        STEAL_ITEM("steal_item", 2),
        POSITIVE_POTION_EFFECTS("positive_potion_effects", 2),
        NEGATIVE_POTION_EFFECTS("negative_potion_effects", 2),
        ZEUS("zeus", 1),
        RAINBOW("rainbow", 2);
        
        private final String id;
        private final int defaultWeight;
        
        SillyEffect(String id, int defaultWeight) {
            this.id = id;
            this.defaultWeight = defaultWeight;
        }
        
        public String getId() {
            return id;
        }
        
        public int getDefaultWeight() {
            return defaultWeight;
        }
    }
    
    @Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID)
    public static class Handler {
        private static final Map<UUID, Integer> zeusTimers = new HashMap<>();
        private static final Map<UUID, Integer> lungTimers = new HashMap<>();
        
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            if (event.side == LogicalSide.CLIENT) return;
            
            Player player = event.player;
            if (zeusTimers.containsKey(player.getUUID())) {
                int remaining = zeusTimers.get(player.getUUID());
                if (remaining <= 0) {
                    zeusTimers.remove(player.getUUID());
                    return;
                }
                
                zeusTimers.put(player.getUUID(), remaining - 1);
                
                ServerLevel level = (ServerLevel) player.level();
                if (level.getRandom().nextFloat() < 0.1f) {
                    int dx = level.getRandom().nextIntBetweenInclusive(-30, 30);
                    int dz = level.getRandom().nextIntBetweenInclusive(-30, 30);
                    BlockPos strikePos = player.blockPosition().offset(dx, 0, dz);
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                    if (bolt != null) {
                        bolt.moveTo(Vec3.atBottomCenterOf(strikePos));
                        level.addFreshEntity(bolt);
                    }
                }
            }
            
            if (lungTimers.containsKey(player.getUUID())) {
                if (player.isDeadOrDying()) {
                    lungTimers.remove(player.getUUID());
                    return;
                }
                
                int remaining = lungTimers.get(player.getUUID());
                if (remaining <= 0) {
                    lungTimers.remove(player.getUUID());
                    return;
                }
                player.setAirSupply(-20);
                player.hurt(player.level().damageSources().drown(), 2.0F);
                lungTimers.put(player.getUUID(), remaining - 1);
            }
        }
        
    }
    
    @Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID, value = Dist.CLIENT)
    public static class ClientHandler {
        private static int rainbowTimer = 0;
        private static final ResourceLocation RAINBOW = new ResourceLocation(ArcaneTweaks.MODID, "shaders/post/rainbow.json");
        
        private static void start() {
            Minecraft mc = Minecraft.getInstance();
            mc.execute(() -> {
                try {
                    mc.gameRenderer.loadEffect(RAINBOW);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        
        public static void stop() {
            Minecraft mc = Minecraft.getInstance();
            mc.execute(mc.gameRenderer::shutdownEffect);
        }
        
        public static void setRainbowTimer(int duration) {
            rainbowTimer = duration;
            start();
        }
        
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            
            if (rainbowTimer > 0) {
                rainbowTimer--;
                if (rainbowTimer == 0) stop();
            }
        }
    }
}
