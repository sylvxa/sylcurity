package lol.sylvie.sylcurity.block.impl;

import com.mojang.serialization.MapCodec;
import eu.pb4.polymer.core.api.block.PolymerHeadBlock;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.HeadSecurityBlock;
import lol.sylvie.sylcurity.block.HorizontalSecurityBlock;
import lol.sylvie.sylcurity.block.SecurityBlock;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.block.impl.camera.CameraBlockEntity;
import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import lol.sylvie.sylcurity.gui.CommonDialogs;
import lol.sylvie.sylcurity.gui.DialogBuilder;
import lol.sylvie.sylcurity.messaging.SecurityMessage;
import lol.sylvie.sylcurity.messaging.SecurityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TerminalBlock extends HeadSecurityBlock {
	public TerminalBlock(Properties settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(TerminalBlock::new);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TerminalBlockEntity(pos, state);
	}

	@Override
	public String getPolymerSkinValue(BlockState blockState, BlockPos blockPos, PacketContext packetContext) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk3OGMzMWM2M2Q3ZGE4MGI5NDQ4MjIwYjhhOWI4OTYwMDNmMWIzMjgxYzM0MWFiMTAxOTRjNmM0ZGZiZTIzZCJ9fX0=";
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof TerminalBlockEntity terminal) || !(user instanceof ServerPlayer player) || !terminal.checkAccessVisibly(player)) {
			return super.useWithoutItem(state, world, pos, user, hit);
		}

		openTerminalDialog(player, terminal);

		return InteractionResult.SUCCESS;
	}

	public void openTerminalDialog(ServerPlayer player, TerminalBlockEntity entity) {
		DialogBuilder builder = CommonDialogs.createSecurityBlockSettings(player, entity, Component.translatable(this.getDescriptionId()), nbtCompound -> {}, () -> openTerminalDialog(player, entity));
		ArrayList<SecurityBlockEntity> blocks = SecurityRegistry.REGISTRY.getOrDefault(entity.getChannel(), new ArrayList<>());
		ArrayList<CameraBlockEntity> cameras = new ArrayList<>();

		// I'm doing this so we only iterate once
		for (SecurityBlockEntity block : blocks) {
			if (!block.checkAccess(player)) continue;
			if (block instanceof CameraBlockEntity camera) {
				cameras.add(camera);
				continue;
			}
		}

		if (!cameras.isEmpty()) {
			builder.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "camera_select"), Component.translatable("menu.sylcurity.camera"), nbtCompound -> {
				openCameraSelector(player, entity, cameras);
			});
		}

		entity.getGroups().forEach(g -> SecurityRegistry.post(new SecurityMessage.SecurityAccess(entity.getChannel(), g, entity.getName(), player), entity.getOwner()));

		builder.openTo(player);
	}

	public void openCameraSelector(ServerPlayer player, TerminalBlockEntity parent, List<CameraBlockEntity> blockEntities) {
		DialogBuilder dialog = new DialogBuilder(player, Component.translatable("menu.sylcurity.camera"));
		for (CameraBlockEntity blockEntity : blockEntities) {
			CompoundTag cameraData = new CompoundTag();
			cameraData.putInt("index", blockEntities.indexOf(blockEntity));
			dialog.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "view_camera"), Component.literal(blockEntity.getName()), data -> {
				if (blockEntities.isEmpty()) return;
				int camera = data.getIntOr("index", 0);
				CameraBlockEntity thisEntity = blockEntities.get(camera);
				ServerLevel world = (ServerLevel) thisEntity.getLevel();
				if (world == null || world.getBlockEntity(thisEntity.getBlockPos()) != thisEntity) return;
				CameraViewer.open(world, thisEntity.getBlockPos(), player);
			}, cameraData, null);
		}

		dialog.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "back"), Component.translatable("menu.sylcurity.back"), nbtCompound -> {
			openTerminalDialog(player, parent);
		});

		dialog.openTo(player);
	}
}
