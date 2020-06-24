package net.cavern.world;

import net.cavern.init.CaveBiomes;
import net.cavern.init.CaveDimensions;
import net.cavern.world.gen.CavernChunkGenerator;
import net.cavern.world.gen.CavernChunkGeneratorConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CavernDimension extends Dimension
{
	private static final Vec3d FOG_COLOR = new Vec3d(0.01D, 0.01D, 0.01D);

	public CavernDimension(World world, DimensionType type)
	{
		super(world, type, 0.1F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator()
	{
		BiomeSource biomeSource = BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig(world.getLevelProperties()).setBiome(CaveBiomes.CAVERN));

		return new CavernChunkGenerator(world, biomeSource, new CavernChunkGeneratorConfig());
	}

	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos chunkPos, boolean checkMobSpawnValidity)
	{
		return null;
	}

	@Override
	public BlockPos getTopSpawningBlockPosition(int x, int z, boolean checkMobSpawnValidity)
	{
		return null;
	}

	@Override
	public float getSkyAngle(long timeOfDay, float tickDelta)
	{
		return 0.5F;
	}

	@Override
	public boolean hasVisibleSky()
	{
		return false;
	}

	@Environment(EnvType.CLIENT) @Override
	public Vec3d getFogColor(float skyAngle, float tickDelta)
	{
		return FOG_COLOR;
	}

	@Override
	public boolean canPlayersSleep()
	{
		return false;
	}

	@Environment(EnvType.CLIENT) @Override
	public boolean isFogThick(int x, int z)
	{
		return false;
	}

	@Override
	public DimensionType getType()
	{
		return CaveDimensions.CAVERN;
	}
}