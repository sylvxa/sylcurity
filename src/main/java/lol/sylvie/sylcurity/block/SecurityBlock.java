package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.HashMap;

public abstract class SecurityBlock extends BlockWithEntity {
	public SecurityBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
		SecurityBlockEntity entity = (SecurityBlockEntity) world.getBlockEntity(pos);
		if (entity != null && !entity.checkAccess(player)) return 0;

		return super.calcBlockBreakingDelta(state, player, world, pos);
	}
}
