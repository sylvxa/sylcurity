package lol.sylvie.sylcurity.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class DialogHelper {
	private static final HashMap<Identifier, HashMap<UUID, Consumer<CompoundTag>>> RESPONSE_MAP = new HashMap<>();

	public static void onCustomClickAction(ServerboundCustomClickActionPacket packet, ServerPlayer player) {
		MinecraftServer server = player.level().getServer();
		if (!server.isSameThread()) {
			server.execute(() -> {
				onCustomClickAction(packet, player);
			});
			return;
		}

		HashMap<UUID, Consumer<CompoundTag>> playerMap = RESPONSE_MAP.get(packet.id());
		if (playerMap == null) return;

		UUID playerId = player.getUUID();
		Consumer<CompoundTag> callback = playerMap.get(playerId);
		if (callback == null) return;

		Optional<Tag> nbtCompound = packet.payload();
		if (nbtCompound.isEmpty() || nbtCompound.get().asCompound().isEmpty()) return;

		playerMap.remove(playerId);
		callback.accept(nbtCompound.get().asCompound().get());
	}

	public static void register(ServerPlayer recipient, Identifier action, Consumer<CompoundTag> callback) {
		RESPONSE_MAP
				.computeIfAbsent(action, (i) -> new HashMap<>())
				.put(recipient.getUUID(), callback);
	}
}
