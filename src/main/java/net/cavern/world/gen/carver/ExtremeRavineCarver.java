package net.cavern.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;

public class ExtremeRavineCarver extends CavernRavineCarver
{
	public ExtremeRavineCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer)
	{
		super(configDeserializer);
	}

	@Override
	public boolean carve(Chunk chunk, Function<BlockPos, Biome> posToBiome, Random random, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config)
	{
		int i = (getBranchFactor() * 2 - 1) * 16;
		double x = chunkXOffset * 16 + random.nextInt(16);
		double y = random.nextInt(random.nextInt(10) + 8) + 70;
		double z = chunkZOffset * 16 + random.nextInt(16);
		float yaw = random.nextFloat() * ((float)Math.PI * 7.0F);
		float pitch = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
		float width = (random.nextFloat() * 2.0F + random.nextFloat()) * 8.0F;
		int branchCount = i - random.nextInt(i / 4);

		carveRavine(chunk, posToBiome, random.nextLong(), seaLevel, chunkX, chunkZ, x, y, z, width, yaw, pitch, 0, branchCount, 9.0D, carvingMask);

		return true;
	}
}