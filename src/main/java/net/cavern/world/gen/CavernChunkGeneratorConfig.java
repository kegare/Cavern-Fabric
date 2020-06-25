package net.cavern.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class CavernChunkGeneratorConfig extends ChunkGeneratorConfig
{
	@Override
	public int getMinY()
	{
		return 0;
	}

	@Override
	public int getMaxY()
	{
		return 256;
	}

	public int getGroundY()
	{
		return 150;
	}

	public BlockState getGroundTopBlock()
	{
		return Blocks.GRASS_BLOCK.getDefaultState();
	}

	public BlockState getGroundUnderBlock()
	{
		return Blocks.DIRT.getDefaultState();
	}
}