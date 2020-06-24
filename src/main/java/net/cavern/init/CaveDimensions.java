package net.cavern.init;

import net.cavern.world.CavernDimension;
import net.cavern.world.CavernEntityPlacer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.minecraft.util.Identifier;

public class CaveDimensions
{
	public static final FabricDimensionType CAVERN = FabricDimensionType.builder()
		.factory(CavernDimension::new).defaultPlacer(new CavernEntityPlacer()).skyLight(false).buildAndRegister(new Identifier("cavern", "cavern"));

	public static void registerAll() {}
}