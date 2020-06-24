package net.cavern.init;

import net.cavern.block.CavernPortalBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CaveBlocks
{
	public static final CavernPortalBlock CAVERN_PORTAL = new CavernPortalBlock(FabricBlockSettings.of(Material.PORTAL)
		.noCollision().strength(-1.0F).sounds(BlockSoundGroup.GLASS).lightLevel(5).dropsNothing());

	public static void registerAll()
	{
		Registry.register(Registry.BLOCK, new Identifier("cavern", "cavern_portal"), CAVERN_PORTAL);

		BlockRenderLayerMap.INSTANCE.putBlock(CAVERN_PORTAL, RenderLayer.getTranslucent());
	}
}