package com.livajq.arcanetweaks.compat.darkerdepths;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.livajq.arcanetweaks.ArcaneTweaks;
import com.naterbobber.darkerdepths.client.fog.BiomeFog;
import com.naterbobber.darkerdepths.client.fog.DDBiomeFogs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.core.object.Color;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Supplier;

public class BiomeFogConfigLoader {
    
    private static final String FILE_NAME = "biome_fog.toml";
    
    public static void init() {
        if (!ModList.get().isLoaded("darkerdepths")) return;
        File modConfigDir = FMLPaths.CONFIGDIR.get().resolve(ArcaneTweaks.MODID).toFile();
        File file = new File(modConfigDir, FILE_NAME);
        
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                writeDefaults(file);
            } catch (IOException e) {
                ArcaneTweaks.LOGGER.error("Could not create config file!", e);
                return;
            }
        }
        
        loadConfig(file);
    }
    
    private static void writeDefaults(File file) {
        String defaultToml = """
        # Adds fog from Darker Depths to more biomes
        # Supports both biomes and biome tags (use isTag)
        
        [[biomes]]
        id = "minecraft:plains"
        isTag = false
        color = "0xFFFFFF"
        min_distance = 1
        max_distance = 32
        """;
        
        try {
            Files.writeString(file.toPath(), defaultToml, StandardOpenOption.APPEND);
        } catch (IOException e) {
            ArcaneTweaks.LOGGER.error("Could not write default biome fog config!", e);
        }
    }
    
    private static void loadConfig(File file) {
        try (FileConfig cfg = FileConfig.of(file, TomlFormat.instance())) {
            cfg.load();
            
            List<UnmodifiableConfig> biomes = cfg.get("biomes");
            if (biomes == null) return;
            
            for (UnmodifiableConfig section : biomes) {
                String id = section.get("id");
                boolean isTag = section.getOrElse("isTag", false);
                
                String hex = section.getOrElse("color", "0xFFFFFF");
                Color fogColor = new Color(Integer.decode(hex));
                
                int minDistance = section.getOrElse("min_distance", 1);
                int maxDistance = section.getOrElse("max_distance", 32);
                
                addFogConfig(id, fogColor, () -> minDistance, () -> maxDistance, isTag);
            }
        }
    }
    
    private static void addFogConfig(String id, Color color, Supplier<Integer> minDist, Supplier<Integer> maxDist, boolean isTag) {
        if (!isTag) {
            ResourceKey<Biome> key = ResourceKey.create(Registries.BIOME, new ResourceLocation(id));
            BiomeFog biomeFog = new BiomeFog(key, color, minDist, maxDist);
            DDBiomeFogs.BIOME_FOGS.add(biomeFog);
        }
        
        else {
            TagKey<Biome> tag = TagKey.create(Registries.BIOME, new ResourceLocation(id));
            var registry = ForgeRegistries.BIOMES;
            
            for (ResourceLocation rl : registry.getKeys()) {
                Holder<Biome> holder = registry.getHolder(rl).orElse(null);
                if (holder == null) continue;
         
                if (holder.is(tag)) {
                    ResourceKey<Biome> key = holder.unwrapKey().orElse(null);
                    if (key != null) {
                        BiomeFog biomeFog = new BiomeFog(key, color, minDist, maxDist);
                        DDBiomeFogs.BIOME_FOGS.add(biomeFog);
                    }
                }
            }
        }
    }
}