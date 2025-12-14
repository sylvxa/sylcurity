package lol.sylvie.sylcurity.block.impl;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.SecurityBlock;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import lol.sylvie.sylcurity.gui.DialogBuilder;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.input.SingleOptionInput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class EventReceiverBlock extends SecurityBlock implements PolymerTexturedBlock {
	protected final HashMap<BlockState, BlockState> BLOCKSTATES = new HashMap<>();
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public EventReceiverBlock(Properties settings) {
		super(settings);
		this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
		generateBlockStates();
	}

	protected void generateBlockStates() {
		for (BlockState state : this.getStateDefinition().getPossibleStates()) {
			PolymerBlockModel model = PolymerBlockModel.of(state.getValue(POWERED) ? Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "block/event_receiver_on") : Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "block/event_receiver"));
			BlockState display = PolymerBlockResourceUtils.requestBlock(BlockModelType.FULL_BLOCK, model);
			BLOCKSTATES.put(state, display);
		}
	}

	@Override
	public BlockState getPolymerBlockState(BlockState blockState, PacketContext packetContext) {
		if (!PolymerResourcePackUtils.hasMainPack(packetContext)) return Blocks.LODESTONE.defaultBlockState();
		return BLOCKSTATES.get(blockState);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
		EventReceiverBlockEntity entity = (EventReceiverBlockEntity) world.getBlockEntity(pos);
		if (entity != null && entity.getActivated()) {
			return 15;
		}
		return super.getSignal(state, world, pos, direction);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		EventReceiverBlockEntity entity = (EventReceiverBlockEntity) world.getBlockEntity(pos);
		if (entity != null && entity.getActivated()) {
			entity.reset();
			world.setBlockAndUpdate(pos, state.setValue(EventReceiverBlock.POWERED, false));
			world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock());
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof EventReceiverBlockEntity entity) || !(user instanceof ServerPlayer player) || !entity.checkAccessVisibly(player)) {
			return super.useWithoutItem(state, world, pos, user, hit);
		}

		openMenu(player, entity);
		return InteractionResult.SUCCESS;
	}

	private void openMenu(ServerPlayer player, EventReceiverBlockEntity entity) {
		List<SingleOptionInput.Entry> options = Arrays.stream(SecurityMessage.Type.values())
				.map(e -> new SingleOptionInput.Entry(e.name(), Optional.of(e.getText()), entity.getEventType().equals(e)))
				.toList();
		DialogBuilder builder = CommonDialogs.createSecurityBlockSettings(player, entity, Component.translatable(this.getDescriptionId()), data -> {
			String value = data.getStringOr("type", SecurityMessage.Type.PLAYER_DETECTION.name());
			try {
				entity.setEventType(SecurityMessage.Type.valueOf(value));
			} catch (IllegalArgumentException ignored) {};
		}, () -> openMenu(player, entity));

		builder.addSingleOptionInput("type", 200, Component.translatable("message.sylcurity.type"), options);
		builder.openTo(player);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(EventReceiverBlock::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new EventReceiverBlockEntity(pos, state);
	}
}
