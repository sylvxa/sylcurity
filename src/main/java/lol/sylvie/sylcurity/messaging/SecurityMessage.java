package lol.sylvie.sylcurity.messaging;

import net.minecraft.block.Block;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SecurityMessage {
	protected final Type type;
	protected final String terminal;
	protected final @Nullable String group;
	protected final String name;
	protected final @Nullable ServerPlayerEntity player;
	protected final Timestamp timestamp;

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

	public SecurityMessage(Type type, String terminal, @Nullable String group, String name, @Nullable ServerPlayerEntity player) {
		this.type = type;
		this.terminal = terminal;
		this.group = group;
		this.name = name;
		this.player = player;
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	public String getTerminal() {
		return terminal;
	}

	public @Nullable String getGroup() {
		return group;
	}

	public @Nullable ServerPlayerEntity getPlayer() {
		return player;
	}

	public String asString() {
		return String.format("[%s] %s detected", this.timestamp(), this.getClass().getSimpleName());
	}

	public String timestamp() {
		return FORMAT.format(this.timestamp);
	}

	public Type getType() {
		return type;
	}

	public enum Type {
		PLAYER_DETECTION(Text.translatable("message.sylcurity.player_detection")),
		SECURITY_ACCESS(Text.translatable("message.sylcurity.security_access"));

		private final Text text;

		Type(Text text) {
			this.text = text;
		}

		public Text getText() {
			return text;
		}
	}

	public static class PlayerDetection extends SecurityMessage {
		public PlayerDetection(String terminal, String group, String name, ServerPlayerEntity offender) {
			super(Type.PLAYER_DETECTION, terminal, group, name, offender);

			if (offender == null) throw new IllegalStateException("Player detection needs a player, dummy!");
		}

		@Override
		public String asString() {
			assert player != null;
			return String.format("[%s] Player %s detected at %s",
					this.timestamp(),
					FormattingUtil.player(player),
					this.name);
		}
	}

	public static class SecurityAccess extends SecurityMessage {
		public SecurityAccess(String terminal, String group, String name, ServerPlayerEntity offender) {
			super(Type.SECURITY_ACCESS, terminal, group, name, offender);

			if (offender == null) throw new IllegalStateException("Access log needs a player!");
		}

		@Override
		public String asString() {
			assert player != null;
			return String.format("[%s] %s accessed %s",
					this.timestamp(),
					FormattingUtil.player(player),
					this.name);
		}
	}
}
