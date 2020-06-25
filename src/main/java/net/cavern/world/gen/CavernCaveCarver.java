package net.cavern.world.gen;

import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CaveCarver;

public class CavernCaveCarver extends CaveCarver
{
	public CavernCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer)
	{
		super(configDeserializer, 256);
	}

	@Override
	protected int getMaxCaveCount()
	{
		return 35;
	}

	@Override
	protected double getTunnelSystemHeightWidthRatio()
	{
		return 1.1D;
	}

	@Override
	protected int getCaveY(Random random)
	{
		return random.nextInt(heightLimit - 50) + 5;
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