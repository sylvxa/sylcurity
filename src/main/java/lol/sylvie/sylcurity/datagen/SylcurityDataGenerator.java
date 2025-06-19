package lol.sylvie.sylcurity.datagen;

import lol.sylvie.sylcurity.datagen.impl.SylcurityBlockLootTableGenerator;
import lol.sylvie.sylcurity.datagen.impl.SylcurityRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SylcurityDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(SylcurityBlockLootTableGenerator::new);
		pack.addProvider(SylcurityRecipeGenerator::new);
	}
}
