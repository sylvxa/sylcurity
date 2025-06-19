package lol.sylvie.sylcurity.block;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.impl.ActivityLogBlockEntity;
import lol.sylvie.sylcurity.block.impl.EventReceiverBlockEntity;
import lol.sylvie.sylcurity.block.impl.PlayerDetectorBlockEntity;
import lol.sylvie.sylcurity.block.impl.camera.CameraBlockEntity;
import lol.sylvie.sylcurity.block.impl.TerminalBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
	public static final BlockEntityType<CameraBlockEntity> CAMERA_BLOCK_ENTITY =
			register("camera", CameraBlockEntity::new, ModBlocks.CAMERA);

	public static final BlockEntityType<TerminalBlockEntity> TERMINAL_BLOCK_ENTITY =
			register("terminal", TerminalBlockEntity::new, ModBlocks.TERMINAL);

	public static final BlockEntityType<ActivityLogBlockEntity> ACTIVITY_LOG_BLOCK_ENTITY =
			register("activity_log", ActivityLogBlockEntity::new, ModBlocks.ACTIVITY_LOG);

	public static final BlockEntityType<EventReceiverBlockEntity> EVENT_RECEIVER_BLOCK_ENTITY =
			register("event_receiver", EventReceiverBlockEntity::new, ModBlocks.EVENT_RECEIVER);

	public static final BlockEntityType<PlayerDetectorBlockEntity> PLAYER_DETECTOR_BLOCK_ENTITY =
			register("player_detector", PlayerDetectorBlockEntity::new, ModBlocks.PLAYER_DETECTOR);


	private static <T extends BlockEntity> BlockEntityType<T> register(
			String name,
			FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
			Block... blocks
	) {
		Identifier id = Identifier.of(Sylcurity.MOD_ID, name);
		BlockEntityType<T> type = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
		PolymerBlockUtils.registerBlockEntity(type);
		return type;
	}

	public static void initialize() {}
}
