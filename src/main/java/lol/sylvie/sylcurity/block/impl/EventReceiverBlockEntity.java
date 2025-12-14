package lol.sylvie.sylcurity.block.impl;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.ModBlocks;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class EventReceiverBlockEntity extends SecurityBlockEntity {
	private boolean activated;
	private SecurityMessage.Type type = SecurityMessage.Type.PLAYER_DETECTION;

	public EventReceiverBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.EVENT_RECEIVER_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void loadAdditional(ValueInput view) {
		super.loadAdditional(view);
		int typeOrdinal = view.getIntOr("type", 1);
		SecurityMessage.Type[] values = SecurityMessage.Type.values();
		if (typeOrdinal < values.length) type = values[typeOrdinal];
	}

	@Override
	protected void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);
		view.putInt("type", type.ordinal());
	}

	@Override
	public void accept(SecurityMessage message) {
		if (!message.getType().equals(type)) return;
		activated = true;

		if (level == null) return;

		BlockState state = level.getBlockState(worldPosition);
		level.updateNeighborsAt(worldPosition, state.getBlock());
		level.scheduleTick(worldPosition, ModBlocks.EVENT_RECEIVER, 4);
		level.setBlockAndUpdate(worldPosition, state.setValue(EventReceiverBlock.POWERED, true));
		Vec3 pos = this.getBlockPos().getCenter().add(0, 0.6, 0);
		if (level instanceof ServerLevel serverWorld)
			serverWorld.sendParticles(new DustParticleOptions(0xFF0000, 1f), pos.x, pos.y, pos.z, 3, 0.05d, 0.05d, 0.05d, 1d);
	}

	public SecurityMessage.Type getEventType() {
		return type;
	}

	public void setEventType(SecurityMessage.Type type) {
		this.type = type;
	}

	public boolean getActivated() {
		return activated;
	}

	public void reset() {
		activated = false;
	}
}
