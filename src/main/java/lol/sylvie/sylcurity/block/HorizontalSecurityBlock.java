package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;

import static lol.sylvie.sylcurity.block.BlockStateUtil.DIRECTION_ROTATIONS;

public abstract class HorizontalSecurityBlock extends SecurityBlock implements PolymerTexturedBlock {
	protected final HashMap<BlockState, BlockState> BLOCKSTATES = new HashMap<>();
	public static final EnumProperty<Direction> DIRECTION = Properties.HORIZONTAL_FACING;

	public HorizontalSecurityBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
		return BLOCKSTATES.get(blockState);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(DIRECTION);
	}

	protected void generateBlockStates(Identifier modelName) {
		for (BlockState state : this.getStateManager().getStates()) {
			Pair<Integer, Integer> rotation = DIRECTION_ROTATIONS.get(state.get(DIRECTION));
			PolymerBlockModel model = PolymerBlockModel.of(modelName, rotation.getLeft(), rotation.getRight());
			BlockState display = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, model);
			BLOCKSTATES.put(state, display);
		}
	}
}
