package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SecurityBlock extends BaseEntityBlock {
	public SecurityBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
		SecurityBlockEntity entity = (SecurityBlockEntity) world.getBlockEntity(pos);
		if (entity != null && !entity.checkAccess(player)) return 0;

		return super.getDestroyProgress(state, player, world, pos);
	}
}
