package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import static lol.sylvie.sylcurity.block.BlockStateUtil.DIRECTION_ROTATIONS;

public abstract class HorizontalSecurityBlock extends SecurityBlock implements PolymerTexturedBlock {
	protected final HashMap<BlockState, BlockState> BLOCKSTATES = new HashMap<>();
	public static final EnumProperty<Direction> DIRECTION = BlockStateProperties.HORIZONTAL_FACING;

	public HorizontalSecurityBlock(Properties settings) {
		super(settings);
	}

	@Override
	public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
		return BLOCKSTATES.get(blockState);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(DIRECTION);
	}

	protected void generateBlockStates(Identifier modelName) {
		for (BlockState state : this.getStateDefinition().getPossibleStates()) {
			Tuple<Integer, Integer> rotation = DIRECTION_ROTATIONS.get(state.getValue(DIRECTION));
			PolymerBlockModel model = PolymerBlockModel.of(modelName, rotation.getA(), rotation.getB());
			BlockState display = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, model);
			BLOCKSTATES.put(state, display);
		}
	}
}
