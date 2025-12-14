package lol.sylvie.sylcurity.block.impl;

import com.mojang.serialization.MapCodec;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.HeadSecurityBlock;
import lol.sylvie.sylcurity.block.HorizontalSecurityBlock;
import lol.sylvie.sylcurity.gui.DialogBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.input.TextInput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Optional;

public class ActivityLogBlock extends HeadSecurityBlock {
	public ActivityLogBlock(Properties settings) {
		super(settings);
	}

	@Override
	public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y5YjY3YmI5Y2MxYzg4NDg2NzYwYjE3MjY1MDU0MzEyZDY1OWRmMmNjNjc1NTc1MDA0NWJkNzFjZmZiNGU2MCJ9fX0=";
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(ActivityLogBlock::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ActivityLogBlockEntity(pos, state);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof ActivityLogBlockEntity entity) || !(user instanceof ServerPlayer player) || !entity.checkAccessVisibly(player)) {
			return super.useWithoutItem(state, world, pos, user, hit);
		}

		openActivityLog(player, entity);
		return InteractionResult.SUCCESS;
	}

	public void openActivityLog(ServerPlayer player, ActivityLogBlockEntity entity) {
		DialogBuilder dialog = CommonDialogs.createSecurityBlockSettings(player, entity, Component.translatable(this.getDescriptionId()), nbtCompound -> {}, () -> openActivityLog(player, entity));

		TextInput.MultilineOptions multiline = new TextInput.MultilineOptions(Optional.of(ActivityLogBlockEntity.MAX_LINES), Optional.of(200));
		dialog.addTextInput("log", 400, Component.translatable(this.getDescriptionId()), String.join("\n", entity.lines.toArray(new String[]{})), entity.lines.size() + entity.lines.stream().mapToInt(String::length).sum(), multiline);

		dialog.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "clear_log"), Component.translatable("menu.sylcurity.clear"), nbtCompound -> {
			entity.clear();
			openActivityLog(player, entity);
		});

		dialog.openTo(player);
	}
}
