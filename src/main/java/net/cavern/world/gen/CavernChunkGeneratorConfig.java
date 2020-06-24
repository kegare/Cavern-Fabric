package net.cavern.world.gen;

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
}