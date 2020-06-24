package net.cavern.world;

import net.cavern.init.CaveCarvers;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class CavernBiome extends Biome
{
	public CavernBiome()
	{
		super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.NOPE, SurfaceBuilder.STONE_CONFIG).category(Biome.Category.NONE).parent(null)
			.precipitation(Biome.Precipitation.NONE).depth(-1.0F).scale(0.0F).temperature(0.5F).downfall(0.0F).waterColor(4159204).waterFogColor(329011));
		this.addCarvers();
		this.addFeatures();
	}

	protected void addCarvers()
	{
		addCarver(GenerationStep.Carver.AIR, configureCarver(CaveCarvers.CAVE, new ProbabilityConfig(0.2F)));
	}

	protected void addFeatures()
	{
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
	}
}