package lol.sylvie.sylcurity.block.impl;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import net.minecraft.block.BlockState;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class ActivityLogBlockEntity extends SecurityBlockEntity {
	public static final int MAX_LINES = 32;
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
	protected void readData(ReadView view) {
		super.readData(view);

		view.read("lines", STRING_LIST_CODEC).ifPresent(l -> lines = new ArrayList<>(l));
	}

	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);

		view.put("lines", STRING_LIST_CODEC, lines);
	}

	@Override
	public void accept(SecurityMessage message) {
		lines.add(message.asString());
		if (lines.size() > MAX_LINES)
			lines.removeFirst();
	}
}
