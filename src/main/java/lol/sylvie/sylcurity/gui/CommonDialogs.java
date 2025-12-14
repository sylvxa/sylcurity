package lol.sylvie.sylcurity.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.block.impl.camera.CameraBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import java.util.*;
import java.util.function.Consumer;

public class CommonDialogs {
	public static void openSecurityBlockSettings(ServerPlayer player, SecurityBlockEntity entity, Component title, Consumer<CompoundTag> onExit, Runnable openThisMenu) {
		if (!entity.checkAccessVisibly(player)) return;

		DialogBuilder settings = createSecurityBlockSettings(player, entity, title, onExit, openThisMenu);

		settings.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "back"), Component.translatable("menu.sylcurity.back"), onExit);

		settings.openTo(player);
	}

	public static DialogBuilder createSecurityBlockSettings(ServerPlayer player, SecurityBlockEntity block, Component title, Consumer<CompoundTag> onExit, Runnable openPrevious) {
		DialogBuilder settings = new DialogBuilder(player, title);

		settings.addTextInput("name", 200, Component.translatable("menu.sylcurity.name"), block.getName(), CameraBlockEntity.MAX_NAME_LENGTH);

		settings.addTextInput("channel", 200, Component.translatable("menu.sylcurity.security_options.channel"), block.getChannel(), SecurityBlockEntity.MAX_TAG_LENGTH);

		int groupStrLen = (SecurityBlockEntity.MAX_TAG_LENGTH * SecurityBlockEntity.MAX_GROUPS) + (2 * (SecurityBlockEntity.MAX_GROUPS - 1));
		settings.addTextInput("groups", 200, Component.translatable("menu.sylcurity.security_options.groups"), String.join(", ", block.getGroups()), groupStrLen);

		settings.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "edit_trusted_players"), Component.translatable("menu.sylcurity.trusted_players"), data -> {
			openTrustedDialog(player, block, openPrevious);
		});

		settings.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "save_security_options"), Component.translatable("menu.sylcurity.save"), data -> {
			onExit.accept(data);

			String name = data.getStringOr("name", "");
			if (name.length() > SecurityBlockEntity.MAX_NAME_LENGTH) {
				player.displayClientMessage(Component.translatable("menu.sylcurity.error.name_too_long").withStyle(ChatFormatting.RED), true);
			} else block.setName(name);

			String channel = data.getStringOr("channel", "");
			if (channel.length() > SecurityBlockEntity.MAX_TAG_LENGTH) {
				player.displayClientMessage(Component.translatable("menu.sylcurity.error.channel_too_long").withStyle(ChatFormatting.RED), true);
			} else block.setChannel(channel);

			String groupTags = data.getStringOr("groups", "");
			List<String> split = Arrays.stream(groupTags.split(","))
					.map(String::strip)
					.filter(s -> s.length() <= SecurityBlockEntity.MAX_TAG_LENGTH)
					.distinct()
					.toList();
			if (split.size() > SecurityBlockEntity.MAX_GROUPS) {
				player.displayClientMessage(Component.translatable("menu.sylcurity.error.too_many_groups").withStyle(ChatFormatting.RED), true);
			} else block.setGroups(new ArrayList<>(split));
		});

		return settings;
	}

	public static void openTrustedDialog(ServerPlayer player, SecurityBlockEntity entity, Runnable openPrevious) {
		DialogBuilder builder = new DialogBuilder(player, Component.translatable("menu.sylcurity.trusted_players"));

		for (String user : entity.getTrusted().keySet()) {
			builder.addText(Component.literal(user));
		}

		builder.addTextInput("username", 200, Component.translatable("menu.sylcurity.name"), "", 16);

		builder.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "add_player"), Component.translatable("menu.sylcurity.trusted.add"), data -> {
			Optional<String> usernameOptional = data.getString("username");
			if (usernameOptional.isEmpty()) {
				openTrustedDialog(player, entity, openPrevious);
				return;
			}

			String username = usernameOptional.get();
			ServerPlayer target = player.level().getServer().getPlayerList().getPlayerByName(username);
			if (target == null) {
				player.displayClientMessage(Component.translatable("menu.sylcurity.error.player_not_found").withStyle(ChatFormatting.RED), true);
			} else {
				GameProfile profile = target.getGameProfile();
				UUID id = profile.id();
				if (id.equals(entity.getOwner())) {
					player.displayClientMessage(Component.translatable("menu.sylcurity.error.no_trust_self").withStyle(ChatFormatting.RED), true);
				} else entity.getTrusted().put(profile.name(), profile.id());
			}

			openTrustedDialog(player, entity, openPrevious);
		});

		builder.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "remove_player"), Component.translatable("menu.sylcurity.trusted.remove"), data -> {
			Optional<String> usernameOptional = data.getString("username");
			if (usernameOptional.isEmpty()) {
				player.displayClientMessage(Component.translatable("menu.sylcurity.error.player_not_trusted").withStyle(ChatFormatting.RED), true);
			} else {
				String username = usernameOptional.get();
				entity.getTrusted().remove(username);
			}

			openTrustedDialog(player, entity, openPrevious);
		});

		builder.addActionButton(Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "back"), Component.translatable("menu.sylcurity.back"), data -> {
			openPrevious.run();
		});

		builder.openTo(player);
	}
}
