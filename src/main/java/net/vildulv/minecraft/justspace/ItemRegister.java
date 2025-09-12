package net.vildulv.minecraft.justspace;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.vildulv.minecraft.justspace.justspace.CREATIVE_MODE_TABS;

public class ItemRegister {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(justspace.MODID);


    // Creates a new BlockItem with the id "justspace:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> CREATIVE_AIR_VENT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("creative_air_vent", BlockRegister.CREATIVE_AIR_VENT);
    public static final DeferredItem<BlockItem> POWERED_AIR_VENT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("powered_air_vent", BlockRegister.POWERED_AIR_VENT);
    public static final DeferredItem<Item> SPACE_ZOMBIE_SPAWN_EGG = ITEMS.register("space_zombie_spawn_egg",
            () -> new DeferredSpawnEggItem(MobRegister.SPACE_ZOMBIE, 0x20020, 0x901080,
                    new Item.Properties()));

    public static final DeferredItem<BlockItem> BROKEN_AIR_VENT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("broken_air_vent", BlockRegister.BROKEN_AIR_VENT);
    public static final DeferredItem<BlockItem> CONTROL_DEVICE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("control_device", BlockRegister.CONTROL_DEVICE);
    public static final DeferredItem<BlockItem> TAPE_DEVICE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("tape_device", BlockRegister.TAPE_DEVICE);
    public static final DeferredItem<BlockItem> GAUGE_DEVICE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("gauge_device", BlockRegister.GAUGE_DEVICE);
    public static final DeferredItem<BlockItem> PING_DEVICE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("ping_device", BlockRegister.PING_DEVICE);




    // Creates a creative tab with the id "justspace:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("justspace_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.justspace")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> CREATIVE_AIR_VENT_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CREATIVE_AIR_VENT_BLOCK_ITEM.get());
                output.accept(POWERED_AIR_VENT_BLOCK_ITEM.get());
                output.accept(BROKEN_AIR_VENT_BLOCK_ITEM.get());
                output.accept(CONTROL_DEVICE_BLOCK_ITEM.get());
                output.accept(TAPE_DEVICE_BLOCK_ITEM.get());
                output.accept(GAUGE_DEVICE_BLOCK_ITEM.get());
                output.accept(PING_DEVICE_BLOCK_ITEM.get());
                output.accept(SPACE_ZOMBIE_SPAWN_EGG);
            }).build());


    // Add the example block item to the building blocks tab
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(SPACE_ZOMBIE_SPAWN_EGG);
        }
    }
}
