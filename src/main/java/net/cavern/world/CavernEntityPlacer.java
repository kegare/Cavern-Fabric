package net.cavern.world;

import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.minecraft.block.pattern.BlockPattern.TeleportTarget;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CavernEntityPlacer implements EntityPlacer
{
	@Override
	public TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset)
	{
		BlockPos.Mutable pos = new BlockPos.Mutable(teleported.getBlockPos());
		int originX = pos.getX();
		int originY = pos.getY();
		int originZ = pos.getZ();
		int radius = 128;
		BlockPos resultPos = null;

		outside: for (int i = 1; i <= radius; ++i)
		{
			for (int j = -i; j <= i; ++j)
			{
				for (int k = -i; k <= i; ++k)
				{
					if (Math.abs(j) < i && Math.abs(k) < i) continue;

					int x = originX + j;
					int z = originZ + k;

					for (int y = originY; y < destination.getEffectiveHeight() - 1; ++y)
					{
						if (destination.isAir(pos.set(x, y, z)))
						{
							if (destination.isAir(pos.setOffset(Direction.DOWN)))
							{
								continue;
							}
							else if (destination.isAir(pos.setOffset(Direction.UP, 2)))
							{
								resultPos = pos.setOffset(Direction.DOWN).toImmutable();

								break outside;
							}
						}
					}

					for (int y = originY; y > 1; --y)
					{
						if (destination.isAir(pos.set(x, y, z)))
						{
							if (destination.isAir(pos.setOffset(Direction.DOWN)))
							{
								continue;
							}
							else if (destination.isAir(pos.setOffset(Direction.UP, 2)))
							{
								resultPos = pos.setOffset(Direction.DOWN).toImmutable();

								break outside;
							}
						}
					}
				}
			}
		}

		if (resultPos == null)
		{
			pos.set(originX, originY, originZ);
		}

		return new TeleportTarget(new Vec3d(pos), Vec3d.ZERO, 0);
	}
}