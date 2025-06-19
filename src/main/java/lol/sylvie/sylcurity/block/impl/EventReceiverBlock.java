package lol.sylvie.sylcurity.block.impl;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.SecurityBlock;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import lol.sylvie.sylcurity.gui.DialogBuilder;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.dialog.input.SingleOptionInputControl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class EventReceiverBlock extends SecurityBlock implements PolymerTexturedBlock {
	protected final HashMap<BlockState, BlockState> BLOCKSTATES = new HashMap<>();
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");

	public EventReceiverBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(POWERED, false));
		generateBlockStates();
	}

	protected void generateBlockStates() {
		for (BlockState state : this.getStateManager().getStates()) {
			PolymerBlockModel model = PolymerBlockModel.of(state.get(POWERED) ? Identifier.of(Sylcurity.MOD_ID, "block/event_receiver_on") : Identifier.of(Sylcurity.MOD_ID, "block/event_receiver"));
			BlockState display = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, model);
			BLOCKSTATES.put(state, display);
		}
	}

	@Override
	public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
		return BLOCKSTATES.get(blockState);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(POWERED);
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		EventReceiverBlockEntity entity = (EventReceiverBlockEntity) world.getBlockEntity(pos);
		if (entity != null && entity.getActivated()) {
			return 15;
		}
		return super.getWeakRedstonePower(state, world, pos, direction);
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		EventReceiverBlockEntity entity = (EventReceiverBlockEntity) world.getBlockEntity(pos);
		if (entity != null && entity.getActivated()) {
			entity.reset();
			world.setBlockState(pos, state.with(EventReceiverBlock.POWERED, false));
			world.updateNeighbors(pos, world.getBlockState(pos).getBlock());
		}
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof EventReceiverBlockEntity entity) || !(user instanceof ServerPlayerEntity player) || !entity.checkAccessVisibly(player)) {
			return super.onUse(state, world, pos, user, hit);
		}

		openMenu(player, entity);
		return ActionResult.SUCCESS;
	}

	private void openMenu(ServerPlayerEntity player, EventReceiverBlockEntity entity) {
		List<SingleOptionInputControl.Entry> options = Arrays.stream(SecurityMessage.Type.values())
				.map(e -> new SingleOptionInputControl.Entry(e.name(), Optional.of(e.getText()), entity.getEventType().equals(e)))
				.toList();
		DialogBuilder builder = CommonDialogs.createSecurityBlockSettings(player, entity, Text.translatable(this.getTranslationKey()), data -> {
			String value = data.getString("type", SecurityMessage.Type.PLAYER_DETECTION.name());
			try {
				entity.setEventType(SecurityMessage.Type.valueOf(value));
			} catch (IllegalArgumentException ignored) {};
		}, () -> openMenu(player, entity));

		builder.addSingleOptionInput("type", 200, Text.translatable("message.sylcurity.type"), options);
		builder.openTo(player);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(EventReceiverBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new EventReceiverBlockEntity(pos, state);
	}
}
