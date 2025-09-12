package net.vildulv.minecraft.justspace;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = justspace.MODID, dist = Dist.CLIENT)
public class justspace {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "justspace";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "justspace" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public static final ResourceKey<Level> SPACE_DIMENSION_KEY = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(justspace.MODID, "space")
    );

    public static DeferredRegister FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, MODID);
    public static final Holder<FluidType> ETHER_FLUID = FLUID_TYPES.register("ether", () -> new FluidType(FluidType.Properties.create().descriptionId("block.minecraft.air").motionScale((double) 1.0F).canPushEntity(false).canSwim(false).canDrown(false).fallDistanceModifier(1.0F).pathType((PathType) null).adjacentPathType((PathType) null).density(0).temperature(0).viscosity(0)) {
        public void setItemMovement(ItemEntity entity) {
            if (!entity.isNoGravity()) {
                entity.setDeltaMovement(entity.getDeltaMovement().add((double) 0.0F, -0.04, (double) 0.0F));
            }

        }

        public boolean canDrownIn(LivingEntity entity) {
            return true;
        }

        @Override
        public boolean canSwim(Entity entity) {
            return false;
        }
    });


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public justspace(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BlockRegister.BLOCKS.register(modEventBus);
        BlockRegister.BLOCK_ENTITY_REGISTER.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ItemRegister.ITEMS.register(modEventBus);
        FLUID_TYPES.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        //Register the mob entity register
        MobRegister.ENTITY_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (justspace) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(ItemRegister::addCreative);
        modEventBus.register(Config.class);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        NeoForge.EVENT_BUS.addListener(TeleportOnTick::onEntityTick);
        NeoForge.EVENT_BUS.register(new OnLivingBreathEventHandler());
        NeoForge.EVENT_BUS.register(new OnDrowningEventHandler());
        NeoForge.EVENT_BUS.register(new OnFalldamageEventHandler());

        //register capabilities
        modEventBus.addListener(BlockRegister::registerCapabilities);

        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent event) -> {
            net.vildulv.minecraft.justspace.command.TeleportMeAndShipCommand.register(event.getDispatcher());
        });

    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));


        Config.PARSED_PLANETS.entrySet().forEach((item) -> LOGGER.info("PLANET >> {} x >> {}", item.getKey(), item.getValue().x()));
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ResourceLocation fromNamespaceAndPath(String s) {
        return ResourceLocation.fromNamespaceAndPath(MODID, s);
    }
}
