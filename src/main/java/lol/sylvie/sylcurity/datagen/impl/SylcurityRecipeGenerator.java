package lol.sylvie.sylcurity.datagen.impl;

import lol.sylvie.sylcurity.Sylcurity;
import lol.sylvie.sylcurity.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class SylcurityRecipeGenerator extends FabricRecipeProvider {
	public SylcurityRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
		return new RecipeGenerator(wrapperLookup, recipeExporter) {
			@Override
			public void generate() {
				Item TERMINAL = ModBlocks.TERMINAL.asItem();
				createShaped(RecipeCategory.MISC, TERMINAL, 1)
						.pattern("iii")
						.pattern("ibi")
						.pattern("igi")
						.input('i', Items.IRON_INGOT)
						.input('g', Items.GLASS_PANE)
						.input('b', Items.REDSTONE_BLOCK)
						.group(Sylcurity.MOD_ID)
						.criterion(hasItem(TERMINAL), conditionsFromItem(TERMINAL))
						.offerTo(exporter);

				Item EVENT_RECEIVER = ModBlocks.EVENT_RECEIVER.asItem();
				createShaped(RecipeCategory.MISC, EVENT_RECEIVER, 1)
						.pattern("iri")
						.pattern("ses")
						.pattern("sss")
						.input('i', Items.REDSTONE_TORCH)
						.input('s', Items.SMOOTH_STONE)
						.input('e', Items.ENDER_PEARL)
						.input('r', Items.REDSTONE)
						.group(Sylcurity.MOD_ID)
						.criterion(hasItem(TERMINAL), conditionsFromItem(TERMINAL))
						.offerTo(exporter);

				Item PLAYER_DETECTOR = ModBlocks.PLAYER_DETECTOR.asItem();
				createShaped(RecipeCategory.MISC, PLAYER_DETECTOR, 1)
						.pattern("rsr")
						.pattern("tbt")
						.pattern("iii")
						.input('i', Items.IRON_INGOT)
						.input('r', Items.REDSTONE_TORCH)
						.input('t', Items.TRIPWIRE_HOOK)
						.input('b', Items.REDSTONE_BLOCK)
						.input('s', Items.SCULK_SENSOR)
						.group(Sylcurity.MOD_ID)
						.criterion(hasItem(TERMINAL), conditionsFromItem(TERMINAL))
						.offerTo(exporter);

				Item ACTIVITY_LOG = ModBlocks.ACTIVITY_LOG.asItem();
				createShaped(RecipeCategory.MISC, ACTIVITY_LOG, 1)
						.pattern("ppp")
						.pattern("plp")
						.pattern("iii")
						.input('i', Items.IRON_INGOT)
						.input('l', Items.LECTERN)
						.input('p', Items.PAPER)
						.group(Sylcurity.MOD_ID)
						.criterion(hasItem(TERMINAL), conditionsFromItem(TERMINAL))
						.offerTo(exporter);

				Item CAMERA = ModBlocks.CAMERA.asItem();
				createShaped(RecipeCategory.MISC, CAMERA, 1)
						.pattern("iii")
						.pattern("ibi")
						.pattern("ege")
						.input('i', Items.IRON_INGOT)
						.input('e', Items.ENDER_PEARL)
						.input('g', Items.GLASS_PANE)
						.input('b', Items.REDSTONE_BLOCK)
						.group(Sylcurity.MOD_ID)
						.criterion(hasItem(TERMINAL), conditionsFromItem(TERMINAL))
						.offerTo(exporter);
			}
		};
	}

	@Override
	public String getName() {
		return "SylcurityRecipeGenerator";
	}
}
