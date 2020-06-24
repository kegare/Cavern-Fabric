package net.cavern.world.gen;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CavernChunkGenerator extends ChunkGenerator<CavernChunkGeneratorConfig>
{
	public CavernChunkGenerator(IWorld world, BiomeSource biomeSource, CavernChunkGeneratorConfig config)
	{
		super(world, biomeSource, config);
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk)
	{
		ChunkPos chunkPos = chunk.getPos();
		ChunkRandom chunkRandom = new ChunkRandom();

		chunkRandom.setSeed(chunkPos.x, chunkPos.z);

		CavernChunkGeneratorConfig config = getConfig();
		int maxY = getMaxY();
		int min = config.getMinY() >= maxY ? 0 : config.getMinY();
		int max = config.getMaxY() <= 0 ? maxY : config.getMaxY();

		if (min >= max)
		{
			min = 0;
			max = maxY;
		}

		buildBase(chunk, min, max, config.getDefaultBlock());
		buildBedrock(chunk, min, max, chunkRandom);
	}

	protected void buildBase(Chunk chunk, int min, int max, BlockState defaultBlock)
	{
		ChunkPos chunkPos = chunk.getPos();

		BlockPos.Mutable.stream(chunkPos.getStartX(), min, chunkPos.getStartZ(), chunkPos.getEndX(), max, chunkPos.getEndZ()).forEach(pos ->
		{
			chunk.setBlockState(pos, defaultBlock, false);
		});
	}

	protected void buildBedrock(Chunk chunk, int min, int max, Random random)
	{
		ChunkPos chunkPos = chunk.getPos();
		BlockPos.Mutable bedrockPos = new BlockPos.Mutable();
		BlockState bedrock = Blocks.BEDROCK.getDefaultState();

		BlockPos.Mutable.stream(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 0, chunkPos.getEndZ()).forEach(pos ->
		{
			for (int y = min + 4; y >= min; --y)
			{
				if (y <= min + random.nextInt(5))
				{
					chunk.setBlockState(bedrockPos.set(pos.getX(), y, pos.getZ()), bedrock, false);
				}
			}

			for (int y = max; y >= max - 4; --y)
			{
				if (y >= max - random.nextInt(5))
				{
					chunk.setBlockState(bedrockPos.set(pos.getX(), y, pos.getZ()), bedrock, false);
				}
			}
		});
	}

	@Override
	public int getSpawnHeight()
	{
		return 0;
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk)
	{

	}

	@Override
	public int getHeightOnGround(int x, int z, Type heightmapType)
	{
		return 0;
	}
}