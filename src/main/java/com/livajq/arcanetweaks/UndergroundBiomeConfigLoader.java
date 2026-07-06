package com.livajq.arcanetweaks;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class UndergroundBiomeConfigLoader {
    
    private static final String FILE_NAME = "underground_biomes.toml";
    private static final Map<ResourceLocation, Boolean> exclusiveBiomes = new HashMap<>();
    private static final Map<ResourceKey<Biome>, List<UndergroundCandidate>> idMappings = new HashMap<>();
    private static final Map<TagKey<Biome>, List<UndergroundCandidate>> tagMappings = new HashMap<>();
    public static final Map<ResourceKey<Biome>, List<UndergroundCandidate>> lookupMappings = new HashMap<>();
    
    public static void init() {
        File file = FMLPaths.CONFIGDIR.get().resolve(ArcaneTweaks.MODID).resolve(FILE_NAME).toFile();
        
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                writeDefaults(file);
            } catch (IOException e) {
                return;
            }
        }
        loadConfig(file);
    }
    
    private static void writeDefaults(File file) {
        String defaultToml = """
        # Modify which biomes can spawn underground depending on the surface biome
        # Useful tags:
        # weight - how likely each underground biome is to be chosen if multiple are allowed for the same surface biome
        # max_y - absolute Y level below which the replacement can happen
        # exclusive - will prevent that underground biome(s) from generating in other places
        
        [[underground_biomes]]
        surface = "minecraft:badlands"
        
            [[underground_biomes.candidates]]
            biome = "minecraft:deep_dark"
            weight = 1
            max_y = 0
        
            [[underground_biomes.candidates]]
            biome = "minecraft:dripstone_caves"
            weight = 2
        
        [[underground_biomes]]
        surface = "#minecraft:is_forest"
        
            [[underground_biomes.candidates]]
            biome = "minecraft:lush_caves"
            exclusive = true
        """;
        
        try {
            Files.writeString(file.toPath(), defaultToml, StandardOpenOption.APPEND);
        } catch (IOException e) {
            ArcaneTweaks.LOGGER.error("Could not write default underground biome config!", e);
        }
    }
    
    private static void loadConfig(File file) {
        try (FileConfig cfg = FileConfig.of(file, TomlFormat.instance())) {
            cfg.load();
            idMappings.clear();
            tagMappings.clear();
            
            List<UnmodifiableConfig> entries = cfg.get("underground_biomes");
            if (entries == null) return;
            
            for (UnmodifiableConfig entry : entries) {
                String surface = entry.get("surface");
                boolean surfaceisTag = surface.startsWith("#");
                
                List<UnmodifiableConfig> candidateConfigs = entry.get("candidates");
                if (candidateConfigs == null) continue;
                
                List<UndergroundCandidate> candidates = new ArrayList<>();
                for (UnmodifiableConfig c : candidateConfigs) {
                    String undergroundBiome = c.get("biome");
                    boolean undergroundBiomeIsTag = undergroundBiome.startsWith("#");
                    boolean exclusive = c.getOrElse("exclusive", false);
                    int weight = c.getOrElse("weight", 1);
                    Optional<Integer> maxY = Optional.ofNullable(c.get("max_y"));
                    ResourceLocation biomeRL = new ResourceLocation(undergroundBiomeIsTag
                            ? undergroundBiome.substring(1)
                            : undergroundBiome);
                    
                    candidates.add(new UndergroundCandidate(
                            biomeRL,
                            undergroundBiomeIsTag, weight, maxY)
                    );
                    
                    if (exclusive) exclusiveBiomes.put(biomeRL, undergroundBiomeIsTag);
                }
                
                ResourceLocation key = new ResourceLocation(surfaceisTag ? surface.substring(1) : surface);
                if (surfaceisTag) tagMappings.put(TagKey.create(Registries.BIOME, key), candidates);
                else idMappings.put(ResourceKey.create(Registries.BIOME, key), candidates);
            }
        }

        lookupMappings.clear();
        lookupMappings.putAll(idMappings);
        for (var entry : tagMappings.entrySet()) {
            for (ResourceLocation rl : ForgeRegistries.BIOMES.getKeys()) {
                var holder = ForgeRegistries.BIOMES.getHolder(rl).orElse(null);
                if (holder == null) continue;
                if (holder.is(entry.getKey())) {
                    holder.unwrapKey().ifPresent(k ->
                            lookupMappings.merge(k, entry.getValue(), (a, b) -> {
                                List<UndergroundCandidate> merged = new ArrayList<>(a);
                                merged.addAll(b);
                                return merged;
                            })
                    );
                }
            }
        }
    }
    
    public static boolean isExclusive(Holder<Biome> biome) {
        ResourceLocation rl = biome.unwrapKey().isPresent() ? biome.unwrapKey().get().location() : null;
        if (rl == null) return false;
        if (exclusiveBiomes.containsKey(rl)) {
            if (!exclusiveBiomes.get(rl)) {
                return true;
            }
            else {
                TagKey<Biome> tag = TagKey.create(Registries.BIOME, rl);
                return biome.is(tag);
            }
        }
        return false;
    }
    
    public record UndergroundCandidate(ResourceLocation biome, boolean biomeIsTag, int weight, Optional<Integer> maxY) {
      
        public List<Holder<Biome>> resolveHolders(Map<ResourceKey<Biome>, Holder<Biome>> biomeHolders) {
            if (!biomeIsTag) {
                ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, biome);
                Holder<Biome> holder = biomeHolders.get(key);
                return holder != null ? List.of(holder) : List.of();
            } else {
                TagKey<Biome> tag = TagKey.create(Registries.BIOME, biome);
                return biomeHolders.values().stream()
                        .filter(h -> h.is(tag))
                        .toList();
            }
        }
        
        public boolean meetsConditions(int quartY) {
            int blockY = quartY << 2;
            return maxY.map(max -> blockY <= max).orElse(true);
        }
    }
}