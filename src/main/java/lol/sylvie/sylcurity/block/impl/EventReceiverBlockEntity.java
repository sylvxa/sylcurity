package lol.sylvie.sylcurity.block.impl;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.ModBlocks;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import net.minecraft.block.BlockState;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EventReceiverBlockEntity extends SecurityBlockEntity {
	private boolean activated;
	private SecurityMessage.Type type = SecurityMessage.Type.PLAYER_DETECTION;

	public EventReceiverBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.EVENT_RECEIVER_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void readData(ReadView view) {
		super.readData(view);
		int typeOrdinal = view.getInt("type", 1);
		SecurityMessage.Type[] values = SecurityMessage.Type.values();
		if (typeOrdinal < values.length) type = values[typeOrdinal];
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		view.putInt("type", type.ordinal());
	}

	@Override
	public void accept(SecurityMessage message) {
		if (!message.getType().equals(type)) return;
		activated = true;

		if (world == null) return;

		BlockState state = world.getBlockState(pos);
		world.updateNeighbors(pos, state.getBlock());
		world.scheduleBlockTick(pos, ModBlocks.EVENT_RECEIVER, 4);
		world.setBlockState(pos, state.with(EventReceiverBlock.POWERED, true));
		Vec3d pos = this.getPos().toCenterPos().add(0, 0.6, 0);
		if (world instanceof ServerWorld serverWorld)
			serverWorld.spawnParticles(new DustParticleEffect(0xFF0000, 1f), pos.x, pos.y, pos.z, 3, 0.05d, 0.05d, 0.05d, 1d);
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
