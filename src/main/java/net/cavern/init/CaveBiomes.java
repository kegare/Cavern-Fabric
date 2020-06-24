package net.cavern.init;

import net.cavern.world.CavernBiome;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CaveBiomes
{
	public static final CavernBiome CAVERN = new CavernBiome();

	public static void registerAll()
	{
		Registry.register(Registry.BIOME, new Identifier("cavern", "cavern"), CAVERN);
	}
}