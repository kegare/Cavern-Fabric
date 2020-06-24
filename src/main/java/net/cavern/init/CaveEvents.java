package net.cavern.init;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.world.dimension.DimensionType;

public class CaveEvents
{
	public static void registerAll()
	{
		UseBlockCallback.EVENT.register(CaveBlocks.CAVERN_PORTAL);

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
		{
			dispatcher.register(CommandManager.literal("cavern").executes(context ->
			{
				PlayerEntity player = context.getSource().getPlayer();
				DimensionType destination = DimensionType.OVERWORLD;

				if (player.dimension == destination)
				{
					destination = CaveDimensions.CAVERN;
				}

				FabricDimensions.teleport(player, destination);

				return 1;
			}));
		});
	}
}