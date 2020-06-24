package net.cavern;

import net.cavern.init.CaveBiomes;
import net.cavern.init.CaveBlocks;
import net.cavern.init.CaveCarvers;
import net.cavern.init.CaveDimensions;
import net.cavern.init.CaveEvents;
import net.fabricmc.api.ModInitializer;

public class CavernMod implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		CaveBlocks.registerAll();
		CaveCarvers.registerAll();
		CaveBiomes.registerAll();
		CaveDimensions.registerAll();
		CaveEvents.registerAll();
	}
}