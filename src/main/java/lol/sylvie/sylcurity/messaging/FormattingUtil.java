package lol.sylvie.sylcurity.messaging;

import net.minecraft.block.Block;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.text.DecimalFormat;

public class FormattingUtil {
	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

	public static String player(ServerPlayerEntity player) {
		return player.getGameProfile().name();
	}

	public static String pos(Vec3d pos) {
		return String.format("X: %s, Y: %s, Z: %s", DECIMAL_FORMAT.format(pos.x), DECIMAL_FORMAT.format(pos.y), DECIMAL_FORMAT.format(pos.z));
	}

	public static String pos(BlockPos pos) {
		return String.format("X: %s, Y: %s, Z: %s", pos.getX(), pos.getY(), pos.getZ());
	}

	public static String block(Block block) {
		return Text.translatable(block.getTranslationKey()).getString();
	}
}
