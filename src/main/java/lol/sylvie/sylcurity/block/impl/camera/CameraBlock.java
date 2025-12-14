package lol.sylvie.sylcurity.block.impl.camera;

import com.mojang.serialization.MapCodec;
import lol.sylvie.sylcurity.block.HeadSecurityBlock;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class CameraBlock extends HeadSecurityBlock {
	public CameraBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(CameraBlock::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CameraBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof CameraBlockEntity entity) || !(user instanceof ServerPlayer player)) {
			return super.useWithoutItem(state, world, pos, user, hit);
		}

		openMenu(player, entity);
		return InteractionResult.SUCCESS;
	}

	private void openMenu(ServerPlayer player, CameraBlockEntity entity) {
		CommonDialogs.openSecurityBlockSettings(player, entity, Component.translatable(this.getDescriptionId()),data -> {}, () -> openMenu(player, entity));
	}

	@Override
	public String getPolymerSkinValue(BlockState blockState, BlockPos blockPos, PacketContext packetContext) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNjNjlmNzllZmY4NTQyZGIwMzA2N2Y5NjQ1OWQxY2RhMzQ4YWM1MTYwMzBkNDJjMDU0MmIxMjlkZDY4YzMwNCJ9fX0=";
	}
}
