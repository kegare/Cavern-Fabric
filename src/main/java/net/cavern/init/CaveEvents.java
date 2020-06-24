package net.cavern.init;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class CaveEvents
{
	public static void registerAll()
	{
		UseBlockCallback.EVENT.register(CaveBlocks.CAVERN_PORTAL);
	}
}