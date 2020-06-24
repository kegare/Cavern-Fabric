package net.cavern.init;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class CavePointTypes
{
	public static final PointOfInterestType CAVERN_PORTAL = PointOfInterestHelper.register(new Identifier("cavern", "cavern_portal"), 0, 1, CaveBlocks.CAVERN_PORTAL);

	public static void registerAll() {}
}