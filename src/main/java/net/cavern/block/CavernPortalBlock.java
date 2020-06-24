package net.cavern.block;

import com.google.common.cache.LoadingCache;

import net.cavern.init.CaveBlocks;
import net.cavern.init.CaveDimensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.mixin.dimension.EntityHooks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class CavernPortalBlock extends Block implements UseBlockCallback
{
	public static final EnumProperty<Direction.Axis> AXIS = Properties.HORIZONTAL_AXIS;

	protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

	public CavernPortalBlock(Settings settings)
	{
		super(settings);
		this.setDefaultState(stateManager.getDefaultState().with(AXIS, Direction.Axis.X));
	}

	public FabricDimensionType getDimension()
	{
		return CaveDimensions.CAVERN;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context)
	{
		switch (state.get(AXIS))
		{
			case Z:
				return Z_SHAPE;
			case X:
			default:
				return X_SHAPE;
		}
	}

	public boolean createPortalAt(IWorld world, BlockPos pos)
	{
		CavernPortalBlock.AreaHelper areaHelper = createAreaHelper(world, pos);

		if (areaHelper != null)
		{
			areaHelper.createPortal();

			return true;
		}

		return false;
	}

	public CavernPortalBlock.AreaHelper createAreaHelper(IWorld world, BlockPos pos)
	{
		CavernPortalBlock.AreaHelper areaHelper = new CavernPortalBlock.AreaHelper(world, pos, Direction.Axis.X);

		if (areaHelper.isValid() && areaHelper.foundPortalBlocks == 0)
		{
			return areaHelper;
		}

		areaHelper = new CavernPortalBlock.AreaHelper(world, pos, Direction.Axis.Z);

		return areaHelper.isValid() && areaHelper.foundPortalBlocks == 0 ? areaHelper : null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos)
	{
		Direction.Axis axis = facing.getAxis();
		Direction.Axis stateAxis = state.get(AXIS);
		boolean bl = stateAxis != axis && axis.isHorizontal();

		return !bl && neighborState.getBlock() != this && !(new CavernPortalBlock.AreaHelper(world, pos, stateAxis)).wasAlreadyValid() ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult)
	{
		ItemStack stack = player.getStackInHand(hand);

		if (stack.getItem() != Items.EMERALD)
		{
			return ActionResult.PASS;
		}

		BlockPos pos = hitResult.getBlockPos();

		if (world.getBlockState(pos).getBlock() != Blocks.MOSSY_COBBLESTONE)
		{
			return ActionResult.PASS;
		}

		if (!createPortalAt(world, pos.offset(hitResult.getSide())))
		{
			return ActionResult.PASS;
		}

		if (!player.isCreative())
		{
			stack.decrement(1);
		}

		return ActionResult.SUCCESS;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
	{
		if (!world.isClient && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals())
		{
			int cooldown = Math.max(entity.getDefaultNetherPortalCooldown(), 60);

			if (entity.netherPortalCooldown > 0)
			{
				entity.netherPortalCooldown = cooldown;

				return;
			}

			entity.netherPortalCooldown = cooldown;

			DimensionType destination = DimensionType.OVERWORLD;

			if (world.getDimension().getType() == destination)
			{
				destination = getDimension();
			}

			BlockPattern.Result result = findPortal(world, pos);
			double d = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
			double horizontalOffset = Math.abs(MathHelper.minusDiv((result.getForwards().getAxis() == Direction.Axis.X ? entity.getZ() : entity.getX()) - (result.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d, d - result.getWidth()));
			double verticalOffset = MathHelper.minusDiv(entity.getY() - 1.0D, result.getFrontTopLeft().getY(), result.getFrontTopLeft().getY() - result.getHeight());

			EntityHooks access = (EntityHooks)entity;

			access.setLastNetherPortalDirectionVector(new Vec3d(horizontalOffset, verticalOffset, 0.0D));
			access.setLastNetherPortalDirection(result.getForwards());

			FabricDimensions.teleport(entity, destination, getDimension().getDefaultPlacement());
		}
	}

	@Environment(EnvType.CLIENT) @Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
	{
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation)
	{
		switch (rotation)
		{
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch (state.get(AXIS))
				{
					case Z:
						return state.with(AXIS, Direction.Axis.X);
					case X:
						return state.with(AXIS, Direction.Axis.Z);
					default:
						return state;
				}
			default:
				return state;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(AXIS);
	}

	public static BlockPattern.Result findPortal(IWorld world, BlockPos pos)
	{
		Direction.Axis axis = Direction.Axis.Z;
		CavernPortalBlock.AreaHelper areaHelper = new CavernPortalBlock.AreaHelper(world, pos, Direction.Axis.X);
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(world, true);

		if (!areaHelper.isValid())
		{
			axis = Direction.Axis.X;
			areaHelper = new CavernPortalBlock.AreaHelper(world, pos, Direction.Axis.Z);
		}

		if (!areaHelper.isValid())
		{
			return new BlockPattern.Result(pos, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
		}
		else
		{
			int[] is = new int[Direction.AxisDirection.values().length];
			Direction direction = areaHelper.negativeDir.rotateYCounterclockwise();
			BlockPos blockPos = areaHelper.lowerCorner.up(areaHelper.getHeight() - 1);
			Direction.AxisDirection[] directions = Direction.AxisDirection.values();

			for (int i = 0; i < directions.length; ++i)
			{
				Direction.AxisDirection axisDirection = directions[i];
				BlockPattern.Result result = new BlockPattern.Result(direction.getDirection() == axisDirection ? blockPos : blockPos.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1), Direction.get(axisDirection, axis), Direction.UP, loadingCache, areaHelper.getWidth(), areaHelper.getHeight(), 1);

				for (int j = 0; j < areaHelper.getWidth(); ++j)
				{
					for (int k = 0; k < areaHelper.getHeight(); ++k)
					{
						CachedBlockPosition cachedBlockPosition = result.translate(j, k, 1);

						if (!cachedBlockPosition.getBlockState().isAir())
						{
							++is[axisDirection.ordinal()];
						}
					}
				}
			}

			Direction.AxisDirection positiveDirections = Direction.AxisDirection.POSITIVE;

			for (int i = 0; i < directions.length; ++i)
			{
				Direction.AxisDirection axisDirection = directions[i];

				if (is[axisDirection.ordinal()] < is[positiveDirections.ordinal()])
				{
					positiveDirections = axisDirection;
				}
			}

			return new BlockPattern.Result(direction.getDirection() == positiveDirections ? blockPos : blockPos.offset(areaHelper.negativeDir, areaHelper.getWidth() - 1), Direction.get(positiveDirections, axis), Direction.UP, loadingCache, areaHelper.getWidth(), areaHelper.getHeight(), 1);
		}
	}

	public static class AreaHelper
	{
		private final IWorld world;
		private final Direction.Axis axis;
		private final Direction negativeDir;
		private final Direction positiveDir;
		private int foundPortalBlocks;
		private BlockPos lowerCorner;
		private int height;
		private int width;

		public AreaHelper(IWorld world, BlockPos pos, Direction.Axis axis)
		{
			this.world = world;
			this.axis = axis;

			if (axis == Direction.Axis.X)
			{
				this.positiveDir = Direction.EAST;
				this.negativeDir = Direction.WEST;
			}
			else
			{
				this.positiveDir = Direction.NORTH;
				this.negativeDir = Direction.SOUTH;
			}

			for (BlockPos blockPos = pos; pos.getY() > blockPos.getY() - 21 && pos.getY() > 0 && validStateInsidePortal(world.getBlockState(pos.down())); pos = pos.down())
			{
				;
			}

			int i = distanceToPortalEdge(pos, positiveDir) - 1;

			if (i >= 0)
			{
				this.lowerCorner = pos.offset(positiveDir, i);
				this.width = distanceToPortalEdge(lowerCorner, negativeDir);

				if (width < 2 || width > 21)
				{
					this.lowerCorner = null;
					this.width = 0;
				}
			}

			if (lowerCorner != null)
			{
				this.height = findHeight();
			}
		}

		protected int distanceToPortalEdge(BlockPos pos, Direction dir)
		{
			int i;

			for (i = 0; i < 22; ++i)
			{
				BlockPos blockPos = pos.offset(dir, i);

				if (!validStateInsidePortal(world.getBlockState(blockPos)) || world.getBlockState(blockPos.down()).getBlock() != Blocks.MOSSY_COBBLESTONE)
				{
					break;
				}
			}

			Block block = world.getBlockState(pos.offset(dir, i)).getBlock();

			return block == Blocks.MOSSY_COBBLESTONE ? i : 0;
		}

		public int getHeight()
		{
			return height;
		}

		public int getWidth()
		{
			return width;
		}

		protected int findHeight()
		{
			int i;

			outside: for (height = 0; height < 21; ++height)
			{
				for (i = 0; i < width; ++i)
				{
					BlockPos pos = lowerCorner.offset(negativeDir, i).up(height);
					BlockState state = world.getBlockState(pos);

					if (!validStateInsidePortal(state))
					{
						break outside;
					}

					Block block = state.getBlock();

					if (block == CaveBlocks.CAVERN_PORTAL)
					{
						++foundPortalBlocks;
					}

					if (i == 0)
					{
						block = world.getBlockState(pos.offset(positiveDir)).getBlock();

						if (block != Blocks.MOSSY_COBBLESTONE)
						{
							break outside;
						}
					}
					else if (i == width - 1)
					{
						block = world.getBlockState(pos.offset(negativeDir)).getBlock();

						if (block != Blocks.MOSSY_COBBLESTONE)
						{
							break outside;
						}
					}
				}
			}

			for (i = 0; i < width; ++i)
			{
				if (world.getBlockState(lowerCorner.offset(negativeDir, i).up(height)).getBlock() != Blocks.MOSSY_COBBLESTONE)
				{
					height = 0;

					break;
				}
			}

			if (height <= 21 && height >= 3)
			{
				return height;
			}
			else
			{
				lowerCorner = null;
				width = 0;
				height = 0;

				return 0;
			}
		}

		protected boolean validStateInsidePortal(BlockState state)
		{
			Block block = state.getBlock();

			return state.isAir() || block == CaveBlocks.CAVERN_PORTAL;
		}

		public boolean isValid()
		{
			return lowerCorner != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
		}

		public void createPortal()
		{
			for (int i = 0; i < width; ++i)
			{
				BlockPos blockPos = lowerCorner.offset(negativeDir, i);

				for (int j = 0; j < height; ++j)
				{
					world.setBlockState(blockPos.up(j), CaveBlocks.CAVERN_PORTAL.getDefaultState().with(CavernPortalBlock.AXIS, axis), 18);
				}
			}
		}

		private boolean portalAlreadyExisted()
		{
			return foundPortalBlocks >= width * height;
		}

		public boolean wasAlreadyValid()
		{
			return isValid() && portalAlreadyExisted();
		}
	}
}