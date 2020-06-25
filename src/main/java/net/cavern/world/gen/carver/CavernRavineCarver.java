package net.cavern.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.RavineCarver;

public class CavernRavineCarver extends RavineCarver
{
	private final float[] heightToHorizontalStretchFactor = new float[1024];

	public CavernRavineCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer)
	{
		super(configDeserializer);
	}

	@Override
	public boolean carve(Chunk chunk, Function<BlockPos, Biome> posToBiome, Random random, int seaLevel, int chunkXOffset, int chunkZOffset, int chunkX, int chunkZ, BitSet carvingMask, ProbabilityConfig config)
	{
		int i = (getBranchFactor() * 2 - 1) * 16;
		double x = chunkXOffset * 16 + random.nextInt(16);
		double y = random.nextInt(random.nextInt(random.nextInt(80) + 8) + 70);
		double z = chunkZOffset * 16 + random.nextInt(16);
		float yaw = random.nextFloat() * ((float)Math.PI * 2.0F);
		float pitch = (random.nextFloat() - 0.5F) * 2.0F / 8.0F;
		float width = (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
		int branchCount = i - random.nextInt(i / 4);

		carveRavine(chunk, posToBiome, random.nextLong(), seaLevel, chunkX, chunkZ, x, y, z, width, yaw, pitch, 0, branchCount, 9.0D, carvingMask);

		return true;
	}

	protected void carveRavine(Chunk chunk, Function<BlockPos, Biome> posToBiome, long seed, int seaLevel, int chunkX, int chunkZ, double x, double y, double z, float width, float yaw, float pitch, int branchStartIndex, int branchCount, double yawPitchRatio, BitSet carvingMask)
	{
		Random random = new Random(seed);
		float f = 1.0F;

		for (int i = 0; i < 256; ++i)
		{
			if (i == 0 || random.nextInt(3) == 0)
			{
				f = 1.0F + random.nextFloat() * random.nextFloat();
			}

			heightToHorizontalStretchFactor[i] = f * f;
		}

		float leftRightChange = 0.0F;
		float upDownChange = 0.0F;

		for (int branch = branchStartIndex; branch < branchCount; ++branch)
		{
			double roomWidth = 1.5D + MathHelper.sin(branch * (float)Math.PI / branchCount) * width;
			double roomHeight = roomWidth * yawPitchRatio;
			roomWidth = roomWidth * (random.nextFloat() * 0.25D + 0.75D);
			roomHeight = roomHeight * (random.nextFloat() * 0.25D + 0.75D);
			float moveHorizontal = MathHelper.cos(pitch);
			float moveVertical = MathHelper.sin(pitch);
			x += MathHelper.cos(yaw) * moveHorizontal;
			y += moveVertical;
			z += MathHelper.sin(yaw) * moveHorizontal;
			pitch = pitch * 0.7F;
			pitch = pitch + upDownChange * 0.05F;
			yaw += leftRightChange * 0.05F;
			upDownChange = upDownChange * 0.8F;
			leftRightChange = leftRightChange * 0.5F;
			upDownChange = upDownChange + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
			leftRightChange = leftRightChange + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;

			if (random.nextInt(4) != 0)
			{
				if (!canCarveBranch(chunkX, chunkZ, x, z, branch, branchCount, width))
				{
					return;
				}

				carveRegion(chunk, posToBiome, seed, seaLevel, chunkX, chunkZ, x, y, z, roomWidth, roomHeight, carvingMask);
			}
		}
	}

	@Override
	protected boolean carveAtPoint(Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random rand, BlockPos.Mutable posHere, BlockPos.Mutable posAbove, BlockPos.Mutable posBelow, int seaLevel, int chunkX, int chunkZ, int x, int z, int relativeX, int y, int relativeZ, AtomicBoolean foundSurface)
	{
		int i = relativeX | relativeZ << 4 | y << 8;

		if (carvingMask.get(i))
		{
			return false;
		}
		else
		{
			carvingMask.set(i);
			posHere.set(x, y, z);

			BlockState stateHere = chunk.getBlockState(posHere);
			BlockState stateAbove = chunk.getBlockState(posAbove.set(posHere).setOffset(Direction.UP));

			if (!canCarveBlock(stateHere, stateAbove))
			{
				return false;
			}
			else
			{
				if (y < 11)
				{
					chunk.setBlockState(posHere, LAVA.getBlockState(), false);
				}
				else
				{
					chunk.setBlockState(posHere, CAVE_AIR, false);
				}

				return true;
			}
		}
	}
}