package lol.sylvie.sylcurity.messaging;

import java.text.DecimalFormat;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

public class FormattingUtil {
	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

	public static String player(ServerPlayer player) {
		return player.getGameProfile().name();
	}

	public static String pos(Vec3 pos) {
		return String.format("X: %s, Y: %s, Z: %s", DECIMAL_FORMAT.format(pos.x), DECIMAL_FORMAT.format(pos.y), DECIMAL_FORMAT.format(pos.z));
	}

	public static String pos(BlockPos pos) {
		return String.format("X: %s, Y: %s, Z: %s", pos.getX(), pos.getY(), pos.getZ());
	}

	public static String block(Block block) {
		return Component.translatable(block.getDescriptionId()).getString();
	}
}
