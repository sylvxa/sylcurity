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
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TerminalBlock extends HeadSecurityBlock {
	public TerminalBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return createCodec(TerminalBlock::new);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TerminalBlockEntity(pos, state);
	}

	@Override
	public String getPolymerSkinValue(BlockState blockState, BlockPos blockPos, PacketContext packetContext) {
		return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk3OGMzMWM2M2Q3ZGE4MGI5NDQ4MjIwYjhhOWI4OTYwMDNmMWIzMjgxYzM0MWFiMTAxOTRjNmM0ZGZiZTIzZCJ9fX0=";
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity user, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof TerminalBlockEntity terminal) || !(user instanceof ServerPlayerEntity player) || !terminal.checkAccessVisibly(player)) {
			return super.onUse(state, world, pos, user, hit);
		}

		openTerminalDialog(player, terminal);

		return ActionResult.SUCCESS;
	}

	public void openTerminalDialog(ServerPlayerEntity player, TerminalBlockEntity entity) {
		DialogBuilder builder = CommonDialogs.createSecurityBlockSettings(player, entity, Text.translatable(this.getTranslationKey()), nbtCompound -> {}, () -> openTerminalDialog(player, entity));
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
			builder.addActionButton(Identifier.of(Sylcurity.MOD_ID, "camera_select"), Text.translatable("menu.sylcurity.camera"), nbtCompound -> {
				openCameraSelector(player, entity, cameras);
			});
		}

		entity.getGroups().forEach(g -> SecurityRegistry.post(new SecurityMessage.SecurityAccess(entity.getChannel(), g, entity.getName(), player), entity.getOwner()));

		builder.openTo(player);
	}

	public void openCameraSelector(ServerPlayerEntity player, TerminalBlockEntity parent, List<CameraBlockEntity> blockEntities) {
		DialogBuilder dialog = new DialogBuilder(player, Text.translatable("menu.sylcurity.camera"));
		for (CameraBlockEntity blockEntity : blockEntities) {
			NbtCompound cameraData = new NbtCompound();
			cameraData.putInt("index", blockEntities.indexOf(blockEntity));
			dialog.addActionButton(Identifier.of(Sylcurity.MOD_ID, "view_camera"), Text.literal(blockEntity.getName()), data -> {
				if (blockEntities.isEmpty()) return;
				int camera = data.getInt("index", 0);
				CameraBlockEntity thisEntity = blockEntities.get(camera);
				ServerWorld world = (ServerWorld) thisEntity.getWorld();
				if (world == null || world.getBlockEntity(thisEntity.getPos()) != thisEntity) return;
				CameraViewer.open(world, thisEntity.getPos(), player);
			}, cameraData, null);
		}

		dialog.addActionButton(Identifier.of(Sylcurity.MOD_ID, "back"), Text.translatable("menu.sylcurity.back"), nbtCompound -> {
			openTerminalDialog(player, parent);
		});

		dialog.openTo(player);
	}
}
