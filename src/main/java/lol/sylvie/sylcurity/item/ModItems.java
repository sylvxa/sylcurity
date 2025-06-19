package lol.sylvie.sylcurity.item;

import lol.sylvie.sylcurity.Sylcurity;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import lol.sylvie.sylcurity.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.trunk.TrunkPlacer;

import java.util.function.Function;

public class ModItems {
    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Sylcurity.MOD_ID, "item_group"));
    public static final ItemGroup ITEM_GROUP = PolymerItemGroupUtils.builder()
            .icon(() -> new ItemStack(ModBlocks.CAMERA.asItem()))
            .displayName(Text.translatable("itemGroup.sylcurity"))
            .build();

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Sylcurity.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(ITEM_GROUP_KEY, ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(itemGroup -> {
            // Add items here:
            // itemGroup.add(EXAMPLE_ITEM);

            // Or blocks:
            itemGroup.add(ModBlocks.CAMERA.asItem());
            itemGroup.add(ModBlocks.TERMINAL.asItem());
            itemGroup.add(ModBlocks.ACTIVITY_LOG.asItem());
            itemGroup.add(ModBlocks.PLAYER_DETECTOR.asItem());
            itemGroup.add(ModBlocks.EVENT_RECEIVER.asItem());
        });
    }
}