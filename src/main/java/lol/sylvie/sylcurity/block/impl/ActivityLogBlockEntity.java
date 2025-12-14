package lol.sylvie.sylcurity.block.impl;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import java.util.ArrayList;

public class ActivityLogBlockEntity extends SecurityBlockEntity {
	public static final int MAX_LINES = 128;
	public ArrayList<String> lines;

	public ActivityLogBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ACTIVITY_LOG_BLOCK_ENTITY, pos, state);
		clear();
	}

	public void clear() {
		this.lines = new ArrayList<>();
		this.lines.addFirst("[INFO] Start of log");
	}

	@Override
	protected void loadAdditional(ValueInput view) {
		super.loadAdditional(view);

		view.read("lines", STRING_LIST_CODEC).ifPresent(l -> lines = new ArrayList<>(l));
	}

	@Override
	protected void saveAdditional(ValueOutput view) {
		super.saveAdditional(view);

		view.store("lines", STRING_LIST_CODEC, lines);
	}

	@Override
	public void accept(SecurityMessage message) {
		lines.add(message.asString());
		if (lines.size() > MAX_LINES)
			lines.removeFirst();
	}
}
