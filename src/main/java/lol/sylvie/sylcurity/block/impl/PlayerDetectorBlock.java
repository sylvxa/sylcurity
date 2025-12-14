package lol.sylvie.sylcurity.block.impl;

import com.mojang.serialization.MapCodec;
import lol.sylvie.sylcurity.block.HeadSecurityBlock;
import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class PlayerDetectorBlock extends HeadSecurityBlock {
	public PlayerDetectorBlock(Properties settings) {
		super(settings);
	}

	@Override
	public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZhN2QyMTk1ZmY3Njc0YmJiMTJlMmY3NTc4YTJhNjNjNTRhOTgwZTY0NzQ0NDUwYWM2NjU2ZTA1YTc5MDQ5OSJ9fX0=";
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(PlayerDetectorBlock::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PlayerDetectorBlockEntity(pos, state);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
			return createTickerHelper(type, ModBlockEntities.PLAYER_DETECTOR_BLOCK_ENTITY, PlayerDetectorBlockEntity::tick);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof PlayerDetectorBlockEntity entity) || !(user instanceof ServerPlayer player)) {
			return super.useWithoutItem(state, world, pos, user, hit);
		}

		openMenu(player, entity);
		return InteractionResult.SUCCESS;
	}

	private void openMenu(ServerPlayer player, PlayerDetectorBlockEntity entity) {
		CommonDialogs.openSecurityBlockSettings(player, entity, Component.translatable(this.descriptionId), nbtCompound -> {}, () -> openMenu(player, entity));
	}
}
