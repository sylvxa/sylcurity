package lol.sylvie.sylcurity.block;

import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;

public class BlockStateUtil {
	public static final Map<Direction, Tuple<Integer, Integer>> DIRECTION_ROTATIONS = Map.of(
			Direction.NORTH, new Tuple<>(0, 0),
			Direction.EAST, new Tuple<>(0, 90),
			Direction.SOUTH, new Tuple<>(0, 180),
			Direction.WEST, new Tuple<>(0, 270),
			Direction.UP, new Tuple<>(270, 0),
			Direction.DOWN, new Tuple<>(90, 0)
	);
}
