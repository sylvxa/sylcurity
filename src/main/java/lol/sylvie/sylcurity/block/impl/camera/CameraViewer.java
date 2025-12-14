package lol.sylvie.sylcurity.block.impl.camera;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.ChunkAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.*;
import lol.sylvie.sylcurity.block.ModBlocks;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class CameraViewer {
	public static HashMap<UUID, CameraSession> USERS = new HashMap<>();

	public static boolean inCamera(UUID player) {
		return USERS.containsKey(player);
	}

	public static void open(ServerLevel world, BlockPos pos, ServerPlayer player) {
		USERS.put(player.getUUID(), new CameraSession(world, pos, player));
	}

	public static void close(ServerPlayer player) {
		UUID playerId = player.getUUID();
		if (!USERS.containsKey(playerId)) return;
		USERS.get(playerId).cleanup();
		USERS.remove(playerId);
	}

	public static void initialize() {
		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
			for (ServerPlayer player : List.copyOf(serverWorld.players())) {
				if (!USERS.containsKey(player.getUUID())) continue;
				CameraSession session = USERS.get(player.getUUID());
				session.tick();
			}
		});

		ServerPlayerEvents.LEAVE.register(player -> {
			if (!USERS.containsKey(player.getUUID())) return;
			CameraViewer.close(player);
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
			for (CameraSession session : USERS.values()) {
				session.cleanup();
			}
		});
	}

	public static class CameraSession {
		private final ServerLevel world;
		private final BlockPos pos;
		private final ServerLevel initialWorld;
		private final Vec3 initialPos;
		private final ServerPlayer player;

		private final ElementHolder holder;
		private final HolderAttachment attachment;

		public CameraSession(ServerLevel world, BlockPos pos, ServerPlayer player) {
			this.world = world;
			this.pos = pos;
			this.initialWorld = player.level();
			this.initialPos = player.position();
			this.player = player;

			this.holder = new ElementHolder();
			this.holder.addElement(new PlayerEntityElement(player));
			this.attachment = ChunkAttachment.of(this.holder, initialWorld, initialPos);

			updateGameMode(GameType.SPECTATOR);

			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, MobEffectInstance.INFINITE_DURATION, 255, true, false, false));
		}

		private void updateGameMode(GameType target) {
			GameType previous = player.gameMode();
			player.gameMode.changeGameModeForPlayer(target);
			ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE, player);
			player.gameMode.changeGameModeForPlayer(previous);
			player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, target.getId()));
			player.connection.send(packet);
		}

		public void tick() {
			this.attachment.tick();
			BlockState state = world.getBlockState(pos);
			if (!state.is(ModBlocks.CAMERA) || player.isShiftKeyDown() || player.isDeadOrDying()) {
				CameraViewer.close(player);
				return;
			}
			int rotation = state.getValue(BlockStateProperties.ROTATION_16);

			//player.noClip = true;
			//player.getAbilities().flying = true;
			//player.sendAbilitiesUpdate();

			player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2, 255, true, false, false));
			player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 2, 255, true, false, false));

			float yaw = RotationSegment.convertToDegrees(rotation) - 180;
			Vec3 posVec = player.calculateViewVector(0, yaw).scale(0.5);
			Vec3 displayPos = pos.getCenter().add(posVec);

			player.teleportTo(world, displayPos.x, displayPos.y - 1.7, displayPos.z, Set.of(), yaw, 0, true);
		}

		public void cleanup() {
			this.holder.destroy();

			//player.noClip = false;
			//player.getAbilities().flying = false;
			//player.sendAbilitiesUpdate();
			player.teleportTo(initialWorld, initialPos.x, initialPos.y, initialPos.z, Set.of(), 0, 0, false);
			player.removeEffect(MobEffects.NIGHT_VISION);
			updateGameMode(player.gameMode());
		}
	}

    // code could probably be decomplexified if we used mannequins instead but
	private static class PlayerEntityElement extends SimpleEntityElement {
		private final ServerPlayer player;
		private final FakePlayer fakePlayer;

		public PlayerEntityElement(ServerPlayer player) {
			super(EntityType.PLAYER);
			this.player = player;
			copyDataFromPlayer(Player.DATA_PLAYER_ABSORPTION_ID);
			copyDataFromPlayer(Player.DATA_SCORE_ID);
            copyDataFromPlayer(Avatar.DATA_PLAYER_MODE_CUSTOMISATION);
            copyDataFromPlayer(Avatar.DATA_PLAYER_MAIN_HAND);

			GameProfile profile = new GameProfile(this.getUuid(), player.getGameProfile().name(), player.getGameProfile().properties());
			this.fakePlayer = FakePlayer.get(player.level(), profile);
			this.setPitch(player.getXRot());
			this.setYaw(player.getYRot());

			this.setInteractionHandler(new InteractionHandler() {
				@Override
				public void interact(ServerPlayer attacker, InteractionHand hand) {
                    if (attacker.equals(player)) return;
					attacker.connection.handleInteract(ServerboundInteractPacket.createInteractionPacket(player, player.isShiftKeyDown(), hand));
				}

				@Override
				public void interactAt(ServerPlayer attacker, InteractionHand hand, Vec3 pos) {
                    if (attacker.equals(player)) return;
					attacker.connection.handleInteract(ServerboundInteractPacket.createInteractionPacket(player, player.isShiftKeyDown(), hand, pos));
				}

				@Override
				public void attack(ServerPlayer attacker) {
                    if (attacker.equals(player)) return;
					CameraViewer.close(player);
					attacker.connection.handleInteract(ServerboundInteractPacket.createAttackPacket(player, player.isShiftKeyDown()));
				}
			});
		}

		@Override
		public void startWatching(ServerPlayer observer, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
			packetConsumer.accept(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, fakePlayer));
			super.startWatching(observer, packetConsumer);
		}

		@Override
		public void stopWatching(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
			super.stopWatching(player, packetConsumer);
		}

		private <T> void copyDataFromPlayer(EntityDataAccessor<T> key) {
			dataTracker.set(key, player.getEntityData().get(key));
		}
	}
}
