package lol.sylvie.sylcurity.block.impl;

import com.mojang.serialization.MapCodec;
import lol.sylvie.sylcurity.block.HeadSecurityBlock;
import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class PlayerDetectorBlock extends HeadSecurityBlock {
	public PlayerDetectorBlock(Settings settings) {
		super(settings);
	}

	@Override
	public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZhN2QyMTk1ZmY3Njc0YmJiMTJlMmY3NTc4YTJhNjNjNTRhOTgwZTY0NzQ0NDUwYWM2NjU2ZTA1YTc5MDQ5OSJ9fX0=";
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(PlayerDetectorBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new PlayerDetectorBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
			return validateTicker(type, ModBlockEntities.PLAYER_DETECTOR_BLOCK_ENTITY, PlayerDetectorBlockEntity::tick);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof PlayerDetectorBlockEntity entity) || !(user instanceof ServerPlayerEntity player)) {
			return super.onUse(state, world, pos, user, hit);
		}

		openMenu(player, entity);
		return ActionResult.SUCCESS;
	}

	private void openMenu(ServerPlayerEntity player, PlayerDetectorBlockEntity entity) {
		CommonDialogs.openSecurityBlockSettings(player, entity, Text.translatable(this.translationKey), nbtCompound -> {}, () -> openMenu(player, entity));
	}
}
