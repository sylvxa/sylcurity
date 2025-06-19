package lol.sylvie.sylcurity.block;

import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class BlockStateUtil {
	public static final Map<Direction, Pair<Integer, Integer>> DIRECTION_ROTATIONS = Map.of(
			Direction.NORTH, new Pair<>(0, 0),
			Direction.EAST, new Pair<>(0, 90),
			Direction.SOUTH, new Pair<>(0, 180),
			Direction.WEST, new Pair<>(0, 270),
			Direction.UP, new Pair<>(270, 0),
			Direction.DOWN, new Pair<>(90, 0)
	);
}
