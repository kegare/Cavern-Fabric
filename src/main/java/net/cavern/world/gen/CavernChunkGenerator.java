package net.cavern.world.gen;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.GenerationStep.Carver;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CavernChunkGenerator extends ChunkGenerator<CavernChunkGeneratorConfig>
{
	private final int floorY;
	private final int roofY;
	private final int groundY;

	public CavernChunkGenerator(IWorld world, BiomeSource biomeSource, CavernChunkGeneratorConfig config)
	{
		super(world, biomeSource, config);

		int i = getMaxY();
		int min = config.getMinY() >= i ? 0 : config.getMinY();
		int max = config.getMaxY() <= 0 ? i : config.getMaxY();

		if (min >= max)
		{
			min = 0;
			max = i;
		}

		this.floorY = min;
		this.roofY = max;
		this.groundY = config.getGroundY();
	}

	@Override
	public void populateNoise(IWorld world, Chunk chunk)
	{
		buildBase(chunk, config.getDefaultBlock(), config.getGroundUnderBlock());
	}

	protected void buildBase(Chunk chunk, BlockState defaultBlock, BlockState groundBlock)
	{
		ChunkPos chunkPos = chunk.getPos();

		BlockPos.Mutable.stream(chunkPos.getStartX(), floorY, chunkPos.getStartZ(), chunkPos.getEndX(), roofY, chunkPos.getEndZ()).forEach(pos ->
		{
			chunk.setBlockState(pos, pos.getY() < groundY ? defaultBlock : groundBlock, false);
		});
	}

	@Override
	public void buildSurface(ChunkRegion chunkRegion, Chunk chunk)
	{
		ChunkPos chunkPos = chunk.getPos();
		ChunkRandom chunkRandom = new ChunkRandom();

		chunkRandom.setSeed(chunkPos.x, chunkPos.z);

		buildBorders(chunk, chunkRandom, Blocks.BEDROCK.getDefaultState(), config.getGroundUnderBlock());
	}

	protected void buildBorders(Chunk chunk, Random random, BlockState bedrockBlock, BlockState groundBlock)
	{
		ChunkPos chunkPos = chunk.getPos();
		BlockPos.Mutable borderPos = new BlockPos.Mutable();

		BlockPos.Mutable.stream(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 0, chunkPos.getEndZ()).forEach(pos ->
		{
			for (int y = floorY + 4; y >= floorY; --y)
			{
				if (y <= floorY + random.nextInt(5))
				{
					chunk.setBlockState(borderPos.set(pos.getX(), y, pos.getZ()), bedrockBlock, false);
				}
			}

			for (int y = roofY; y >= roofY - 4; --y)
			{
				if (y >= roofY - random.nextInt(5))
				{
					chunk.setBlockState(borderPos.set(pos.getX(), y, pos.getZ()), bedrockBlock, false);
				}
			}

			for (int y = groundY; y >= groundY - 4; --y)
			{
				if (y >= groundY - random.nextInt(5))
				{
					chunk.setBlockState(borderPos.set(pos.getX(), y, pos.getZ()), groundBlock, false);
				}
			}
		});
	}

	@Override
	public void carve(BiomeAccess biomeAccess, Chunk chunk, Carver carver)
	{
		super.carve(biomeAccess, chunk, carver);

		ChunkPos chunkPos = chunk.getPos();
		BlockPos.Mutable posHere = new BlockPos.Mutable();
		BlockPos.Mutable posAbove = new BlockPos.Mutable();
		BlockState topBlock = config.getGroundTopBlock();
		BlockState underBlock = config.getGroundUnderBlock();

		BlockPos.Mutable.stream(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 0, chunkPos.getEndZ()).forEach(pos ->
		{
			for (int y = roofY; y > groundY; --y)
			{
				BlockState stateHere = chunk.getBlockState(posHere.set(pos.getX(), y, pos.getZ()));
				BlockState stateAbove = chunk.getBlockState(posAbove.set(posHere).setOffset(Direction.UP));

				if (stateHere.getBlock() == underBlock.getBlock() && stateAbove.isAir())
				{
					chunk.setBlockState(posHere, topBlock, false);
				}
			}
		});
	}

	@Override
	public int getSpawnHeight()
	{
		return world.getSeaLevel() + 1;
	}

	@Override
	public int getHeightOnGround(int x, int z, Type heightmapType)
	{
		return 0;
	}
}