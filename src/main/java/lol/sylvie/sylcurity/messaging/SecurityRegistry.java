package lol.sylvie.sylcurity.messaging;

import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SecurityRegistry {
	public static final HashMap<String, ArrayList<SecurityBlockEntity>> REGISTRY = new HashMap<>();

	public static void initialize() {
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, serverWorld) -> {
			if (!(blockEntity instanceof SecurityBlockEntity securityBlock)) return;
			REGISTRY.computeIfAbsent(securityBlock.getChannel(), s -> new ArrayList<>()).add(securityBlock);
		});

		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, serverWorld) -> {
			if (!(blockEntity instanceof SecurityBlockEntity securityBlock)) return;
			REGISTRY.get(securityBlock.getChannel()).remove(securityBlock);
		});
	}

	public static void post(SecurityMessage message, UUID owner) {
		REGISTRY.getOrDefault(message.getTerminal(), new ArrayList<>())
				.stream()
				.filter(b -> b.checkAccess(owner))
				.filter(b -> b.getGroups().contains(message.getGroup()))
				.forEach(b -> b.accept(message));
	}
}
