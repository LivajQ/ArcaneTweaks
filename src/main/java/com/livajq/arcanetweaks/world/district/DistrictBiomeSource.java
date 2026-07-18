package com.livajq.arcanetweaks.world.district;

import com.livajq.arcanetweaks.Config;
import com.livajq.arcanetweaks.UndergroundBiomeConfigLoader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//I still have no idea what I'm doing
public class DistrictBiomeSource extends BiomeSource {
    
    public static final Codec<DistrictBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("delegate").forGetter(src -> src.delegate),
                    Codec.LONG.fieldOf("seed").forGetter(src -> src.seed)
            ).apply(instance, DistrictBiomeSource::new)
    );
    
    private final BiomeSource delegate;
    private final long seed;
    
    private final Map<TagKey<Biome>, List<Holder<Biome>>> tagCache;
    private final Map<Long, Holder<Biome>> remapCache;
    private final Map<ResourceKey<Biome>, Holder<Biome>> biomeHolders;
    
    private static final int SURFACE_QUART_Y = 75;
    
    public DistrictBiomeSource(BiomeSource delegate, long seed) {
        this.delegate = delegate;
        this.seed = seed;
        
        this.biomeHolders = delegate.possibleBiomes().stream()
                .filter(h -> h.unwrapKey().isPresent())
                .collect(Collectors.toMap(h -> h.unwrapKey().get(), h -> h));
        
        Map<TagKey<Biome>, List<Holder<Biome>>> tagTmp = new HashMap<>();
        List<Holder<Biome>> possible = delegate.possibleBiomes().stream().toList();
        
        for (DistrictBand band : DistrictBand.values()) {
            TagKey<Biome> tag = band.tag();
            List<Holder<Biome>> list = possible.stream()
                    .filter(holder -> holder.is(tag))
                    .toList();
            tagTmp.put(tag, list);
        }
        this.tagCache = Map.copyOf(tagTmp);
        
        Map<Long, Holder<Biome>> remapTmp = new HashMap<>();
        for (DistrictBand band : DistrictBand.values()) {
            List<Holder<Biome>> allowed = tagCache.get(band.tag());
            if (allowed == null || allowed.isEmpty()) continue;
            
            for (Holder<Biome> original : possible) {
                long key = makeKey(band, original);
                long mix = seed ^ key;
                RandomSource rand = RandomSource.create(mix);
                Holder<Biome> result = allowed.get(rand.nextInt(allowed.size()));
                remapTmp.put(key, result);
            }
        }
        this.remapCache = Map.copyOf(remapTmp);
    }
    
    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }
    
    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return delegate.possibleBiomes().stream();
    }
    
    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        Holder<Biome> original = delegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
        if (original == null) return fallbackBiome();
        
        Holder <Biome> surfaceBiome = delegate.getNoiseBiome(quartX, SURFACE_QUART_Y, quartZ, sampler);
        Holder <Biome> currentBiome = delegate.getNoiseBiome(quartX, quartY, quartZ, sampler);
        
        if (!Objects.equals(
                surfaceBiome.unwrapKey().orElse(null),
                currentBiome.unwrapKey().orElse(null)
        )) {
            if (surfaceBiome != null) {
                List<UndergroundBiomeConfigLoader.UndergroundCandidate> candidates = surfaceBiome.unwrapKey()
                        .map(UndergroundBiomeConfigLoader.lookupMappings::get)
                        .orElse(null);
                if (candidates != null) {
                    Holder<Biome> underground = selectCandidate(candidates, original, quartY);
                    if (underground != null) return underground;
                }
                else if (UndergroundBiomeConfigLoader.isExclusive(currentBiome)) return surfaceBiome;
            }
            return original;
        }
        
        if (Config.worldgenType == 0) return original;
        
        int blockZ = quartZ << 2;
        DistrictBand band = DistrictBand.fromZ(blockZ);
        
        if (Config.worldgenType == 2) {
            return pickRandomBiomeForBand(band, quartX << 2, quartZ << 2, original);
        }
        
        return remapBiome(band, original);
    }
    
    private Holder<Biome> selectCandidate(List<UndergroundBiomeConfigLoader.UndergroundCandidate> candidates, Holder<Biome> originalUnderground, int quartY) {
        List<Holder<Biome>> pool = new ArrayList<>();
        for (UndergroundBiomeConfigLoader.UndergroundCandidate candidate : candidates) {
            if (!candidate.meetsConditions(quartY)) continue;
            List<Holder<Biome>> resolved = candidate.resolveHolders(biomeHolders);
            for (Holder<Biome> h : resolved) {
                for (int i = 0; i < candidate.weight(); i++) {
                    pool.add(h);
                }
            }
        }
        if (pool.isEmpty()) return null;
        
        int biomeHash = originalUnderground.unwrapKey()
                .map(k -> k.location().toString().hashCode())
                .orElse(0);
        long mix = seed ^ (biomeHash & 0xFFFFFFFFL);
        RandomSource rand = RandomSource.create(mix);
        
        return pool.get(rand.nextInt(pool.size()));
    }
    
    private Holder<Biome> fallbackBiome() {
        return delegate.possibleBiomes().stream().findFirst().orElseThrow();
    }
    
    private long makeKey(DistrictBand band, Holder<Biome> original) {
        int biomeHash = original.unwrapKey()
                .map(k -> k.location().hashCode())
                .orElse(0);
        return (((long) band.ordinal()) << 32) ^ (biomeHash & 0xFFFFFFFFL);
    }
    
    /**
     * IMPORTANT:
     * Same vanilla biome ALWAYS maps to same replacement biome
     * inside the same district.
     */
    private Holder<Biome> remapBiome(DistrictBand band, Holder<Biome> original) {
        long key = makeKey(band, original);
        return remapCache.getOrDefault(key, original);
    }
    
    private Holder<Biome> pickRandomBiomeForBand(DistrictBand band, int x, int z, Holder<Biome> original) {
        List<Holder<Biome>> allowed = tagCache.get(band.tag());
        if (allowed == null || allowed.isEmpty()) return original;
        
        long biomeSeed = Mth.getSeed(x, 0, z);
        RandomSource rand = RandomSource.create(seed ^ biomeSeed);
        
        return allowed.get(rand.nextInt(allowed.size()));
    }
}