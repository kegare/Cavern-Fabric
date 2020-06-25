package net.cavern.world;

import net.cavern.init.CaveCarvers;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
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
		addCarver(GenerationStep.Carver.AIR, configureCarver(CaveCarvers.EXTREME_CAVE, new ProbabilityConfig(0.15F)));
		addCarver(GenerationStep.Carver.AIR, configureCarver(CaveCarvers.CANYON, new ProbabilityConfig(0.02F)));
		addCarver(GenerationStep.Carver.AIR, configureCarver(CaveCarvers.EXTREME_CANYON, new ProbabilityConfig(0.001F)));
	}

	protected void addFeatures()
	{
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);

		addMineables();
		addOres();
	}

	public void addMineables()
	{
		GenerationStep.Feature step = GenerationStep.Feature.UNDERGROUND_ORES;
		OreFeatureConfig.Target target = OreFeatureConfig.Target.NATURAL_STONE;

		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.DIRT.getDefaultState(), 33))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 256))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.GRAVEL.getDefaultState(), 33))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(15, 0, 0, 256))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.GRANITE.getDefaultState(), 33))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 256))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.DIORITE.getDefaultState(), 33))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 256))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.ANDESITE.getDefaultState(), 33))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(20, 0, 0, 256))));
	}

	public void addOres()
	{
		GenerationStep.Feature step = GenerationStep.Feature.UNDERGROUND_ORES;
		OreFeatureConfig.Target target = OreFeatureConfig.Target.NATURAL_STONE;

		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.COAL_ORE.getDefaultState(), 17))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(40, 0, 0, 256))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.IRON_ORE.getDefaultState(), 9))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(40, 0, 0, 256))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.GOLD_ORE.getDefaultState(), 9))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(5, 0, 0, 128))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.REDSTONE_ORE.getDefaultState(), 8))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(15, 0, 0, 64))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.DIAMOND_ORE.getDefaultState(), 8))
			.createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(1, 0, 0, 16))));
		addFeature(step, Feature.ORE.configure(new OreFeatureConfig(target, Blocks.LAPIS_ORE.getDefaultState(), 7))
			.createDecoratedFeature(Decorator.COUNT_DEPTH_AVERAGE.configure(new CountDepthDecoratorConfig(1, 16, 16))));
		addFeature(step, Feature.EMERALD_ORE.configure(new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(), Blocks.EMERALD_ORE.getDefaultState()))
			.createDecoratedFeature(Decorator.EMERALD_ORE.configure(DecoratorConfig.DEFAULT)));
	}
}