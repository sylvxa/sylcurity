package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import xyz.nucleoid.packettweaker.PacketContext;

public abstract class HeadSecurityBlock extends SecurityBlock implements PolymerHeadBlock {
	public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

	public HeadSecurityBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(defaultBlockState().setValue(ROTATION, 0));
	}

	@Override
	public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
		return PolymerHeadBlock.super.getPolymerBlockState(state, context)
				.setValue(BlockStateProperties.ROTATION_16, state.getValue(ROTATION));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(ROTATION, RotationSegment.convertToSegment(ctx.getRotation()));
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), RotationSegment.getMaxSegmentIndex() + 1));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), RotationSegment.getMaxSegmentIndex() + 1));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ROTATION);
	}
}
