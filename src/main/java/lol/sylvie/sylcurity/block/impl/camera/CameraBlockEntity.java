package lol.sylvie.sylcurity.block.impl.camera;

import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.SecurityBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CameraBlockEntity extends SecurityBlockEntity {
	public CameraBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.CAMERA_BLOCK_ENTITY, pos, state);
	}
}
