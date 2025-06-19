package lol.sylvie.sylcurity.block.impl.camera;

import com.mojang.serialization.MapCodec;
import lol.sylvie.sylcurity.block.HeadSecurityBlock;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class CameraBlock extends HeadSecurityBlock {
	public CameraBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(CameraBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new CameraBlockEntity(pos, state);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof CameraBlockEntity entity) || !(user instanceof ServerPlayerEntity player)) {
			return super.onUse(state, world, pos, user, hit);
		}

		openMenu(player, entity);
		return ActionResult.SUCCESS;
	}

	private void openMenu(ServerPlayerEntity player, CameraBlockEntity entity) {
		CommonDialogs.openSecurityBlockSettings(player, entity, Text.translatable(this.getTranslationKey()),data -> {}, () -> openMenu(player, entity));
	}

	@Override
	public String getPolymerSkinValue(BlockState blockState, BlockPos blockPos, PacketContext packetContext) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNjNjlmNzllZmY4NTQyZGIwMzA2N2Y5NjQ1OWQxY2RhMzQ4YWM1MTYwMzBkNDJjMDU0MmIxMjlkZDY4YzMwNCJ9fX0=";
	}
}
