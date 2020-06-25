package net.cavern.world.gen.carver;

import java.util.Random;
import java.util.function.Function;

import com.mojang.datafixers.Dynamic;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.ProbabilityConfig;

public class ExtremeCaveCarver extends CavernCaveCarver
{
	public ExtremeCaveCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> configDeserializer)
	{
		super(configDeserializer);
	}

	@Override
	protected int getMaxCaveCount()
	{
		return 20;
	}

	@Override
	protected float getTunnelSystemWidth(Random random)
	{
		float width = random.nextFloat() * 20.0F + random.nextFloat();

		if (random.nextInt(10) == 0)
		{
			width *= random.nextFloat() * random.nextFloat() * 1.5F + 1.0F;
		}

		return width;
	}

	@Override
	protected double getTunnelSystemHeightWidthRatio()
	{
		return 0.75D;
	}

	@Override
	protected int getCaveY(Random random)
	{
		return MathHelper.floor(heightLimit * 0.75D) + random.nextInt(random.nextInt(5) + 5) + 5;
	}
}