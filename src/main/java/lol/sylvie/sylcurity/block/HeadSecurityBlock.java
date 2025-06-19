package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.RotationPropertyHelper;
import xyz.nucleoid.packettweaker.PacketContext;

public abstract class HeadSecurityBlock extends SecurityBlock implements PolymerHeadBlock {
	public static final IntProperty ROTATION = Properties.ROTATION;

	public HeadSecurityBlock(Settings settings) {
		super(settings);
		this.setDefaultState(getDefaultState().with(ROTATION, 0));
	}

	@Override
	public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
		return PolymerHeadBlock.super.getPolymerBlockState(state, context)
				.with(Properties.ROTATION, state.get(ROTATION));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, rotation.rotate(state.get(ROTATION), RotationPropertyHelper.getMax() + 1));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.with(ROTATION, mirror.mirror(state.get(ROTATION), RotationPropertyHelper.getMax() + 1));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(ROTATION);
	}
}
