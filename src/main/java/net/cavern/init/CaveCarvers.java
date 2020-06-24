package net.cavern.init;

import net.cavern.world.gen.CavernCaveCarver;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;

public class CaveCarvers
{
	public static final Carver<ProbabilityConfig> CAVE = new CavernCaveCarver(ProbabilityConfig::deserialize);

	public static void registerAll()
	{
		Registry.register(Registry.CARVER, new Identifier("cavern", "cave"), CAVE);
	}
}