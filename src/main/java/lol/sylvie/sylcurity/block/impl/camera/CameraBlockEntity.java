package lol.sylvie.sylcurity.block.impl.camera;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class CameraBlockEntity extends SecurityBlockEntity {
	public CameraBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CAMERA_BLOCK_ENTITY, pos, state);
	}
}
