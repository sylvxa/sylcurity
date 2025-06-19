package lol.sylvie.sylcurity.block.impl;

import com.mojang.serialization.MapCodec;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.HeadSecurityBlock;
import lol.sylvie.sylcurity.block.HorizontalSecurityBlock;
import lol.sylvie.sylcurity.gui.DialogBuilder;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Optional;

public class ActivityLogBlock extends HeadSecurityBlock {
	public ActivityLogBlock(Settings settings) {
		super(settings);
	}

	@Override
	public String getPolymerSkinValue(BlockState state, BlockPos pos, PacketContext context) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y5YjY3YmI5Y2MxYzg4NDg2NzYwYjE3MjY1MDU0MzEyZDY1OWRmMmNjNjc1NTc1MDA0NWJkNzFjZmZiNGU2MCJ9fX0=";
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(ActivityLogBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ActivityLogBlockEntity(pos, state);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof ActivityLogBlockEntity entity) || !(user instanceof ServerPlayerEntity player) || !entity.checkAccessVisibly(player)) {
			return super.onUse(state, world, pos, user, hit);
		}

		openActivityLog(player, entity);
		return ActionResult.SUCCESS;
	}

	public void openActivityLog(ServerPlayerEntity player, ActivityLogBlockEntity entity) {
		DialogBuilder dialog = CommonDialogs.createSecurityBlockSettings(player, entity, Text.translatable(this.getTranslationKey()), nbtCompound -> {}, () -> openActivityLog(player, entity));

		TextInputControl.Multiline multiline = new TextInputControl.Multiline(Optional.of(ActivityLogBlockEntity.MAX_LINES), Optional.of(200));
		dialog.addTextInput("log", 400, Text.translatable(this.getTranslationKey()), String.join("\n", entity.lines.toArray(new String[]{})), entity.lines.size() + entity.lines.stream().mapToInt(String::length).sum(), multiline);

		dialog.addActionButton(Identifier.of(Sylcurity.MOD_ID, "clear_log"), Text.translatable("menu.sylcurity.clear"), nbtCompound -> {
			entity.clear();
			openActivityLog(player, entity);
		});

		dialog.openTo(player);
	}
}
