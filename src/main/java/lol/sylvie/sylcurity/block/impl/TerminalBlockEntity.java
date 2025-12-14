package lol.sylvie.sylcurity.block.impl;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;

public class TerminalBlockEntity extends SecurityBlockEntity {
	public TerminalBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.TERMINAL_BLOCK_ENTITY, pos, state);
	}
}
