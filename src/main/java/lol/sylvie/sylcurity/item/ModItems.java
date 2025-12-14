package lol.sylvie.sylcurity.item;

import lol.sylvie.sylcurity.Sylcurity;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import lol.sylvie.sylcurity.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.function.Function;

public class ModItems {
    public static final ResourceKey<CreativeModeTab> ITEM_GROUP_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, "item_group"));
    public static final CreativeModeTab ITEM_GROUP = PolymerItemGroupUtils.builder()
            .icon(() -> new ItemStack(ModBlocks.CAMERA.asItem()))
            .title(Component.translatable("itemGroup.sylcurity"))
            .build();

    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Sylcurity.MOD_ID, name));
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(ITEM_GROUP_KEY, ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(itemGroup -> {
            // Add items here:
            // itemGroup.add(EXAMPLE_ITEM);

            // Or blocks:
            itemGroup.accept(ModBlocks.CAMERA.asItem());
            itemGroup.accept(ModBlocks.TERMINAL.asItem());
            itemGroup.accept(ModBlocks.ACTIVITY_LOG.asItem());
            itemGroup.accept(ModBlocks.PLAYER_DETECTOR.asItem());
            itemGroup.accept(ModBlocks.EVENT_RECEIVER.asItem());
        });
    }
}