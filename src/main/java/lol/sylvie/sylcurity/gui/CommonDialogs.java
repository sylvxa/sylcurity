package lol.sylvie.sylcurity.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import lol.sylvie.sylcurity.block.impl.camera.CameraBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;

public class CommonDialogs {
	public static void openSecurityBlockSettings(ServerPlayerEntity player, SecurityBlockEntity entity, Text title, Consumer<NbtCompound> onExit, Runnable openThisMenu) {
		if (!entity.checkAccessVisibly(player)) return;

		DialogBuilder settings = createSecurityBlockSettings(player, entity, title, onExit, openThisMenu);

		settings.addActionButton(Identifier.of(Sylcurity.MOD_ID, "back"), Text.translatable("menu.sylcurity.back"), onExit);

		settings.openTo(player);
	}

	public static DialogBuilder createSecurityBlockSettings(ServerPlayerEntity player, SecurityBlockEntity block, Text title, Consumer<NbtCompound> onExit, Runnable openPrevious) {
		DialogBuilder settings = new DialogBuilder(player, title);

		settings.addTextInput("name", 200, Text.translatable("menu.sylcurity.name"), block.getName(), CameraBlockEntity.MAX_NAME_LENGTH);

		settings.addTextInput("channel", 200, Text.translatable("menu.sylcurity.security_options.channel"), block.getChannel(), SecurityBlockEntity.MAX_TAG_LENGTH);

		int groupStrLen = (SecurityBlockEntity.MAX_TAG_LENGTH * SecurityBlockEntity.MAX_GROUPS) + (2 * (SecurityBlockEntity.MAX_GROUPS - 1));
		settings.addTextInput("groups", 200, Text.translatable("menu.sylcurity.security_options.groups"), String.join(", ", block.getGroups()), groupStrLen);

		settings.addActionButton(Identifier.of(Sylcurity.MOD_ID, "edit_trusted_players"), Text.translatable("menu.sylcurity.trusted_players"), data -> {
			openTrustedDialog(player, block, openPrevious);
		});

		settings.addActionButton(Identifier.of(Sylcurity.MOD_ID, "save_security_options"), Text.translatable("menu.sylcurity.save"), data -> {
			onExit.accept(data);

			String name = data.getString("name", "");
			if (name.length() > SecurityBlockEntity.MAX_NAME_LENGTH) {
				player.sendMessage(Text.translatable("menu.sylcurity.input_error", "Name too long").formatted(Formatting.RED), true);
			} else block.setName(name);

			String channel = data.getString("channel", "");
			if (channel.length() > SecurityBlockEntity.MAX_TAG_LENGTH) {
				player.sendMessage(Text.translatable("menu.sylcurity.input_error", "Channel too long").formatted(Formatting.RED), true);
			} else block.setChannel(channel);

			String groupTags = data.getString("groups", "");
			List<String> split = Arrays.stream(groupTags.split(","))
					.map(String::strip)
					.filter(s -> s.length() <= SecurityBlockEntity.MAX_TAG_LENGTH)
					.distinct()
					.toList();
			if (split.size() > SecurityBlockEntity.MAX_GROUPS) {
				player.sendMessage(Text.translatable("menu.sylcurity.input_error", "Too many groups").formatted(Formatting.RED), true);
			} else block.setGroups(new ArrayList<>(split));
		});

		return settings;
	}

	public static void openTrustedDialog(ServerPlayerEntity player, SecurityBlockEntity entity, Runnable openPrevious) {
		DialogBuilder builder = new DialogBuilder(player, Text.translatable("menu.sylcurity.trusted_players"));

		for (String user : entity.getTrusted().keySet()) {
			builder.addText(Text.literal(user));
		}

		builder.addTextInput("username", 200, Text.translatable("menu.sylcurity.name"), "", 16);

		builder.addActionButton(Identifier.of(Sylcurity.MOD_ID, "add_player"), Text.translatable("menu.sylcurity.trusted.add"), data -> {
			Optional<String> usernameOptional = data.getString("username");
			if (usernameOptional.isEmpty()) {
				openTrustedDialog(player, entity, openPrevious);
				return;
			}

			String username = usernameOptional.get();
			assert player.getServer() != null;
			ServerPlayerEntity target = player.getServer().getPlayerManager().getPlayer(username);
			if (target == null) {
				player.sendMessage(Text.translatable("menu.sylcurity.input_error", "Player must be online!").formatted(Formatting.RED), true);
			} else {
				GameProfile profile = target.getGameProfile();
				UUID id = profile.getId();
				if (id.equals(entity.getOwner())) {
					player.sendMessage(Text.translatable("menu.sylcurity.input_error", "Owners are implicitly trusted!").formatted(Formatting.RED), true);
				} else entity.getTrusted().put(profile.getName(), profile.getId());
			}

			openTrustedDialog(player, entity, openPrevious);
		});

		builder.addActionButton(Identifier.of(Sylcurity.MOD_ID, "remove_player"), Text.translatable("menu.sylcurity.trusted.remove"), data -> {
			Optional<String> usernameOptional = data.getString("username");
			if (usernameOptional.isEmpty()) {
				player.sendMessage(Text.translatable("menu.sylcurity.input_error", "Player not found!").formatted(Formatting.RED), true);
			} else {
				String username = usernameOptional.get();
				entity.getTrusted().remove(username);
			}

			openTrustedDialog(player, entity, openPrevious);
		});

		builder.addActionButton(Identifier.of(Sylcurity.MOD_ID, "back"), Text.translatable("menu.sylcurity.back"), data -> {
			openPrevious.run();
		});

		builder.openTo(player);
	}
}
