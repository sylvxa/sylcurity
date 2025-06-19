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
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class CameraViewer {
	public static HashMap<UUID, CameraSession> USERS = new HashMap<>();

	public static boolean inCamera(UUID player) {
		return USERS.containsKey(player);
	}

	public static void open(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
		USERS.put(player.getUuid(), new CameraSession(world, pos, player));
	}

	public static void close(ServerPlayerEntity player) {
		UUID playerId = player.getUuid();
		if (!USERS.containsKey(playerId)) return;
		USERS.get(playerId).cleanup();
		USERS.remove(playerId);
	}

	public static void initialize() {
		ServerTickEvents.START_WORLD_TICK.register(serverWorld -> {
			for (ServerPlayerEntity player : serverWorld.getPlayers()) {
				if (!USERS.containsKey(player.getUuid())) continue;
				CameraSession session = USERS.get(player.getUuid());
				session.tick();
			}
		});

		ServerPlayerEvents.LEAVE.register(player -> {
			if (!USERS.containsKey(player.getUuid())) return;
			CameraViewer.close(player);
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
			for (CameraSession session : USERS.values()) {
				session.cleanup();
			}
		});
	}

	public static class CameraSession {
		private final ServerWorld world;
		private final BlockPos pos;
		private final ServerWorld initialWorld;
		private final Vec3d initialPos;
		private final ServerPlayerEntity player;

		private final ElementHolder holder;
		private final HolderAttachment attachment;

		public CameraSession(ServerWorld world, BlockPos pos, ServerPlayerEntity player) {
			this.world = world;
			this.pos = pos;
			this.initialWorld = player.getWorld();
			this.initialPos = player.getPos();
			this.player = player;

			this.holder = new ElementHolder();
			this.holder.addElement(new PlayerEntityElement(player));
			this.attachment = ChunkAttachment.of(this.holder, initialWorld, initialPos);

			updateGameMode(GameMode.SPECTATOR);

			player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, StatusEffectInstance.INFINITE, 255, true, false, false));
		}

		private void updateGameMode(GameMode target) {
			GameMode previous = player.getGameMode();
			player.interactionManager.changeGameMode(target);
			PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAME_MODE, player);
			player.interactionManager.changeGameMode(previous);
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, target.getIndex()));
			player.networkHandler.sendPacket(packet);
		}

		public void tick() {
			this.attachment.tick();
			BlockState state = world.getBlockState(pos);
			if (!state.isOf(ModBlocks.CAMERA) || player.isSneaking() || player.isDead()) {
				CameraViewer.close(player);
				return;
			}
			int rotation = state.get(Properties.ROTATION);

			//player.noClip = true;
			//player.getAbilities().flying = true;
			//player.sendAbilitiesUpdate();

			player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 2, 255, true, false, false));
			player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 2, 255, true, false, false));

			float yaw = RotationPropertyHelper.toDegrees(rotation) - 180;
			Vec3d posVec = player.getRotationVector(0, yaw).multiply(0.5);
			Vec3d displayPos = pos.toCenterPos().add(posVec);

			player.teleport(world, displayPos.x, displayPos.y - 1.7, displayPos.z, Set.of(), yaw, 0, true);
		}

		public void cleanup() {
			this.holder.destroy();

			//player.noClip = false;
			//player.getAbilities().flying = false;
			//player.sendAbilitiesUpdate();
			player.teleport(initialWorld, initialPos.x, initialPos.y, initialPos.z, Set.of(), 0, 0, false);
			player.removeStatusEffect(StatusEffects.NIGHT_VISION);
			updateGameMode(player.getGameMode());
		}
	}

	private static class PlayerEntityElement extends SimpleEntityElement {
		private final ServerPlayerEntity player;
		private final FakePlayer fakePlayer;

		public PlayerEntityElement(ServerPlayerEntity player) {
			super(EntityType.PLAYER);
			this.player = player;
			copyDataFromPlayer(PlayerEntity.ABSORPTION_AMOUNT);
			copyDataFromPlayer(PlayerEntity.SCORE);
			copyDataFromPlayer(PlayerEntity.PLAYER_MODEL_PARTS);
			copyDataFromPlayer(PlayerEntity.MAIN_ARM);
			copyDataFromPlayer(PlayerEntity.LEFT_SHOULDER_ENTITY);
			copyDataFromPlayer(PlayerEntity.RIGHT_SHOULDER_ENTITY);

			GameProfile profile = new GameProfile(this.getUuid(), player.getGameProfile().getName());
			PropertyMap map = player.getGameProfile().getProperties();
			if (map.containsKey("textures"))
				profile.getProperties().put("textures", map.get("textures").iterator().next());

			this.fakePlayer = FakePlayer.get(player.getWorld(), profile);
			this.setPitch(player.getPitch());
			this.setYaw(player.getYaw());

			this.setInteractionHandler(new InteractionHandler() {
				@Override
				public void interact(ServerPlayerEntity attacker, Hand hand) {
					attacker.networkHandler.onPlayerInteractEntity(PlayerInteractEntityC2SPacket.interact(player, player.isSneaking(), hand));
				}

				@Override
				public void interactAt(ServerPlayerEntity attacker, Hand hand, Vec3d pos) {
					attacker.networkHandler.onPlayerInteractEntity(PlayerInteractEntityC2SPacket.interactAt(player, player.isSneaking(), hand, pos));
				}

				@Override
				public void attack(ServerPlayerEntity attacker) {
					CameraViewer.close(player);
					attacker.networkHandler.onPlayerInteractEntity(PlayerInteractEntityC2SPacket.attack(player, player.isSneaking()));
				}
			});
		}

		@Override
		public void startWatching(ServerPlayerEntity observer, Consumer<Packet<ClientPlayPacketListener>> packetConsumer) {
			packetConsumer.accept(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, fakePlayer));
			super.startWatching(observer, packetConsumer);
		}

		@Override
		public void stopWatching(ServerPlayerEntity player, Consumer<Packet<ClientPlayPacketListener>> packetConsumer) {
			super.stopWatching(player, packetConsumer);
		}

		private <T> void copyDataFromPlayer(TrackedData<T> key) {
			dataTracker.set(key, player.getDataTracker().get(key));
		}
	}
}
