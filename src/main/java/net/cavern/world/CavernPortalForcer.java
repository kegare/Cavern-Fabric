package net.cavern.world;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.cavern.block.CavernPortalBlock;
import net.cavern.init.CaveBlocks;
import net.cavern.init.CavePointTypes;
import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

public class CavernPortalForcer implements EntityPlacer
{
	@Override
	public BlockPattern.TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset)
	{
		BlockPos originPos = teleported.getBlockPos();
		Vec3d velocity = teleported.getVelocity();
		BlockPattern.TeleportTarget ret = getPortal(destination, originPos, velocity, portalDir, horizontalOffset, verticalOffset);

		if (ret == null)
		{
			BlockPos pos = createPortal(destination, originPos);

			if (pos != null)
			{
				ret = CavernPortalBlock.findPortal(destination, pos).getTeleportTarget(portalDir, pos, verticalOffset, velocity, horizontalOffset);
			}
		}

		if (ret == null)
		{
			BlockPos pos = getForcedTeleportPoint(destination, originPos);

			if (pos != null)
			{
				ret = new BlockPattern.TeleportTarget(new Vec3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D), velocity, 0);
			}
		}

		return ret;
	}

	public BlockPattern.TeleportTarget getPortal(ServerWorld world, BlockPos originPos, Vec3d velocity, Direction direction, double horizontalOffset, double verticalOffset)
	{
		PointOfInterestStorage storage = world.getPointOfInterestStorage();

		storage.method_22439(world, originPos, 128);

		List<PointOfInterest> list = storage.method_22383(o -> o == CavePointTypes.CAVERN_PORTAL, originPos, 128, PointOfInterestStorage.OccupationStatus.ANY).collect(Collectors.toList());
		Optional<PointOfInterest> ret = list.stream().min(Comparator.<PointOfInterest>comparingDouble(o -> o.getPos().getSquaredDistance(originPos)).thenComparingInt(o -> o.getPos().getY()));

		return ret.map(o ->
		{
			BlockPos pos = o.getPos();

			world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(pos), 3, pos);

			return CavernPortalBlock.findPortal(world, pos).getTeleportTarget(direction, pos, verticalOffset, velocity, horizontalOffset);
		}).orElse((BlockPattern.TeleportTarget)null);
	}

	public BlockPos createPortal(ServerWorld world, BlockPos originPos)
	{
		int min = 10;
		int max = world.getEffectiveHeight() - 10;
		int x = originPos.getX();
		int y = originPos.getY();
		int z = originPos.getZ();
		int i = 0;
		int j = world.random.nextInt(4);
		BlockPos.Mutable pos = new BlockPos.Mutable();
		double portalDist = -1.0D;

		for (int r = 1; r <= 128; ++r)
		{
			for (int rx = -r; rx <= r; ++rx)
			{
				for (int rz = -r; rz <= r; ++rz)
				{
					if (Math.abs(rx) < r && Math.abs(rz) < r) continue;

					int px = originPos.getX() + rx;
					int py = min;
					int pz = originPos.getZ() + rz;

					finder: while (true)
					{
						for (py = originPos.getY(); py <= max; ++py)
						{
							if (world.isAir(pos.set(px, py, pz)) && world.getBlockState(pos.setOffset(Direction.DOWN)).getMaterial().isSolid())
							{
								break finder;
							}
						}

						for (py = originPos.getY(); py >= min; --py)
						{
							if (world.isAir(pos.set(px, py, pz)) && world.getBlockState(pos.setOffset(Direction.DOWN)).getMaterial().isSolid())
							{
								break finder;
							}
						}

						py = 0;

						break;
					}

					if (py < min || py > max)
					{
						continue;
					}

					double dist = originPos.getSquaredDistance(px, py, pz, true);

					outside: for (int k = j; k < j + 4; ++k)
					{
						int xDist = k % 2;
						int zDist = 1 - xDist;

						if (k % 4 >= 2)
						{
							xDist = -xDist;
							zDist = -zDist;
						}

						for (int size1 = 0; size1 < 3; ++size1)
						{
							for (int size2 = 0; size2 < 4; ++size2)
							{
								for (int height = -1; height < 4; ++height)
								{
									pos.set(px + (size2 - 1) * xDist + size1 * zDist, py + height, pz + (size2 - 1) * zDist - size1 * xDist);

									if (height < 0 && !world.getBlockState(pos).getMaterial().isSolid() || height >= 0 && !world.isAir(pos))
									{
										break outside;
									}
								}
							}
						}

						if (portalDist < 0.0D || dist < portalDist)
						{
							portalDist = dist;
							x = px;
							y = py;
							z = pz;
							i = k % 4;
						}
					}
				}
			}

			if (portalDist >= 0.0D)
			{
				break;
			}
		}

		if (portalDist < 0.0D)
		{
			for (int r = 1; r <= 128; ++r)
			{
				for (int rx = -r; rx <= r; ++rx)
				{
					for (int rz = -r; rz <= r; ++rz)
					{
						if (Math.abs(rx) < r && Math.abs(rz) < r) continue;

						int px = originPos.getX() + rx;
						int py = min;
						int pz = originPos.getZ() + rz;

						finder: while (true)
						{
							for (py = originPos.getY(); py <= max; ++py)
							{
								if (world.isAir(pos.set(px, py, pz)) && world.getBlockState(pos.setOffset(Direction.DOWN)).getMaterial().isSolid())
								{
									break finder;
								}
							}

							for (py = originPos.getY(); py >= min; --py)
							{
								if (world.isAir(pos.set(px, py, pz)) && world.getBlockState(pos.setOffset(Direction.DOWN)).getMaterial().isSolid())
								{
									break finder;
								}
							}

							py = 0;

							break;
						}

						if (py < min || py > max)
						{
							continue;
						}

						double dist = originPos.getSquaredDistance(px, py, pz, true);

						outside: for (int k = j; k < j + 2; ++k)
						{
							int xDist = k % 2;
							int zDist = 1 - xDist;

							for (int width = 0; width < 4; ++width)
							{
								for (int height = -1; height < 4; ++height)
								{
									pos.set(px + (width - 1) * xDist, py + height, pz + (width - 1) * zDist);

									if (height < 0 && !world.getBlockState(pos).getMaterial().isSolid() || height >= 0 && !world.isAir(pos))
									{
										break outside;
									}
								}
							}

							if (portalDist < 0.0D || dist < portalDist)
							{
								portalDist = dist;
								x = px;
								y = py;
								z = pz;
								i = k % 2;
							}
						}
					}
				}

				if (portalDist >= 0.0D)
				{
					break;
				}
			}
		}

		int xDist = i % 2;
		int zDist = 1 - xDist;

		if (i % 4 >= 2)
		{
			xDist = -xDist;
			zDist = -zDist;
		}

		if (portalDist < 0.0D)
		{
			y = MathHelper.clamp(y, min, max);

			for (int size1 = -1; size1 <= 1; ++size1)
			{
				for (int size2 = 1; size2 < 3; ++size2)
				{
					for (int height = -1; height < 3; ++height)
					{
						int blockX = x + (size2 - 1) * xDist + size1 * zDist;
						int blockY = y + height;
						int blockZ = z + (size2 - 1) * zDist - size1 * xDist;
						boolean flag = height < 0;

						world.setBlockState(pos.set(blockX, blockY, blockZ), flag ? Blocks.MOSSY_COBBLESTONE.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}
		}

		for (int width = -1; width < 3; ++width)
		{
			for (int height = -1; height < 4; ++height)
			{
				if (width == -1 || width == 2 || height == -1 || height == 3)
				{
					pos.set(x + width * xDist, y + height, z + width * zDist);

					world.setBlockState(pos, Blocks.MOSSY_COBBLESTONE.getDefaultState());
				}
			}
		}

		BlockState portalState = CaveBlocks.CAVERN_PORTAL.getDefaultState().with(CavernPortalBlock.AXIS, xDist != 0 ? Direction.Axis.X : Direction.Axis.Z);
		BlockPos portalPos = null;

		for (int width = 0; width < 2; ++width)
		{
			for (int height = 0; height < 3; ++height)
			{
				world.setBlockState(pos.set(x + width * xDist, y + height, z + width * zDist), portalState, 18);

				if (width == 1 && height == 0)
				{
					portalPos = pos.toImmutable();
				}
			}
		}

		return portalPos;
	}

	public BlockPos getForcedTeleportPoint(ServerWorld world, BlockPos originPos)
	{
		BlockPos.Mutable pos = new BlockPos.Mutable(originPos);
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

					for (int y = originY; y < world.getEffectiveHeight() - 1; ++y)
					{
						if (world.isAir(pos.set(x, y, z)))
						{
							if (world.isAir(pos.setOffset(Direction.DOWN)))
							{
								continue;
							}
							else if (world.isAir(pos.setOffset(Direction.UP, 2)))
							{
								resultPos = pos.setOffset(Direction.DOWN).toImmutable();

								break outside;
							}
						}
					}

					for (int y = originY; y > 1; --y)
					{
						if (world.isAir(pos.set(x, y, z)))
						{
							if (world.isAir(pos.setOffset(Direction.DOWN)))
							{
								continue;
							}
							else if (world.isAir(pos.setOffset(Direction.UP, 2)))
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
			resultPos = new BlockPos(originX, originY, originZ);
		}

		return resultPos;
	}
}