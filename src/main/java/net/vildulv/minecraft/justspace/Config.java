package net.vildulv.minecraft.justspace;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue TELEPORT_HEIGHT = BUILDER
            .comment("The height where teleport to space occurs")
            .defineInRange("teleportHeight", 300, 256, 2000);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> SPACE_DIMENSIONS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("space_dimensions", List.of("minecraft:overworld", "minecraft:the_end"), () -> "", Config::validateDimensionName);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> PLANET_NAMES = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("planet_names", List.of("Earth", "Moon"), () -> "", Config::validateDimensionName);

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> PLANET_X_LOCATION = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("planets_x_pos", List.of(0, 0), () -> 0, e -> e instanceof Integer);

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> PLANET_Z_LOCATION = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("planets_z_pos", List.of(0, 4000), () -> 0, e -> e instanceof Integer);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> PLANET_TEXTURE = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("planets_texture", List.of("textures/block/packed_ice.png", "textures/block/netherrack.png"), () -> "textures/block/stone.png", Config::validateDimensionName);
    /*
   static Map<String, PlanetRecord> DEFAULT_PLANETS = Map.of(
            "Earth", new PlanetRecord("Earth", "minecraft:overworld", 0, 0),
            "Pluto", new PlanetRecord("Pluto", "minecraft:the_end", 2000, 0)
    );

    public static Map<String, PlanetRecordConfigValues> PLANETS = buildFromConfigValues();

    public static Map<String, PlanetRecordConfigValues> buildFromConfigValues() {
        return DEFAULT_PLANETS.entrySet().stream()
                .map(entry -> {String planetName = entry.getKey().toLowerCase();  return entry; })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {var value= new PlanetRecordConfigValues(
                                BUILDER.define(entry.getKey() + ".name", entry.getValue().name()),
                                BUILDER.define(entry.getKey() + ".dimension_id", entry.getValue().dimension_id()),
                                BUILDER.defineInRange(entry.getKey() + ".x", entry.getValue().x(), Integer.MIN_VALUE, Integer.MAX_VALUE),
                                BUILDER.defineInRange(entry.getKey() + ".z", entry.getValue().z(), Integer.MIN_VALUE, Integer.MAX_VALUE));

                            return value;
                        })
                );
    }




    public record PlanetRecordConfigValues(ModConfigSpec.ConfigValue<String> name, ModConfigSpec.ConfigValue<String> dimension_id, ModConfigSpec.IntValue x, ModConfigSpec.IntValue z) {
    } */

    public record PlanetRecord(String name, String dimension_id, int x, int z, ResourceKey<Level> dimensionKey, ResourceLocation texture) {
    }

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateDimensionName(final Object obj) {
        return obj instanceof String;
    }

    public static Map<String, PlanetRecord> PARSED_PLANETS = new HashMap<>();

    @SubscribeEvent
    public static void onConfigLoad(final ModConfigEvent event) {
        System.out.println("CONFIG LOADED");
        PARSED_PLANETS.clear();
        for (int n = 0; n < SPACE_DIMENSIONS.get().size(); n++) {
            String dimension = SPACE_DIMENSIONS.get().get(n);
            String name = PLANET_NAMES.get().get(n);
            int x = PLANET_X_LOCATION.get().get(n);
            int z = PLANET_Z_LOCATION.get().get(n);
            ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimension));
            ResourceLocation texture = ResourceLocation.parse(PLANET_TEXTURE.get().get(n));
            PARSED_PLANETS.put(name, new PlanetRecord(name, dimension, x, z, dimensionKey, texture));
        }
        System.out.println("PARSED PLANETS: " + PARSED_PLANETS);
    }
}
