package lol.sylvie.sylcurity;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import lol.sylvie.sylcurity.block.ModBlockEntities;
import lol.sylvie.sylcurity.block.ModBlocks;
import lol.sylvie.sylcurity.block.impl.camera.CameraViewer;
import lol.sylvie.sylcurity.item.ModItems;
import lol.sylvie.sylcurity.messaging.SecurityRegistry;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sylcurity implements ModInitializer {
	public static final String MOD_ID = "sylcurity";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModItems.initialize();

		SecurityRegistry.initialize();
		CameraViewer.initialize();

		PolymerResourcePackUtils.addModAssets(MOD_ID);

		LOGGER.info("Hello Fabric world!");
	}
}