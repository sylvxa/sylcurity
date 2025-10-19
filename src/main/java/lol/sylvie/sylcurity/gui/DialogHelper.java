package lol.sylvie.sylcurity.gui;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class DialogHelper {
	private static final HashMap<Identifier, HashMap<UUID, Consumer<NbtCompound>>> RESPONSE_MAP = new HashMap<>();

	public static void onCustomClickAction(CustomClickActionC2SPacket packet, ServerPlayerEntity player) {
		MinecraftServer server = player.getEntityWorld().getServer();
		if (!server.isOnThread()) {
			server.execute(() -> {
				onCustomClickAction(packet, player);
			});
			return;
		}

		HashMap<UUID, Consumer<NbtCompound>> playerMap = RESPONSE_MAP.get(packet.id());
		if (playerMap == null) return;

		UUID playerId = player.getUuid();
		Consumer<NbtCompound> callback = playerMap.get(playerId);
		if (callback == null) return;

		Optional<NbtElement> nbtCompound = packet.payload();
		if (nbtCompound.isEmpty() || nbtCompound.get().asCompound().isEmpty()) return;

		playerMap.remove(playerId);
		callback.accept(nbtCompound.get().asCompound().get());
	}

	public static void register(ServerPlayerEntity recipient, Identifier action, Consumer<NbtCompound> callback) {
		RESPONSE_MAP
				.computeIfAbsent(action, (i) -> new HashMap<>())
				.put(recipient.getUuid(), callback);
	}
}
