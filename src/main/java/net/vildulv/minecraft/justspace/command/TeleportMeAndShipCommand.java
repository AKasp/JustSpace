package net.vildulv.minecraft.justspace.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.vildulv.minecraft.justspace.block.compat.VsCompatibility;

public class TeleportMeAndShipCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("teleportmeandship")
                .then(Commands.argument("dimension", DimensionArgument.dimension())
                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                .executes(context -> {
                                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                                    double x = DoubleArgumentType.getDouble(context, "x");
                                                    double y = DoubleArgumentType.getDouble(context, "y");
                                                    double z = DoubleArgumentType.getDouble(context, "z");
                                                    ServerLevel dimension = DimensionArgument.getDimension(context, "dimension");


                                                    VsCompatibility.teleportToValkyrienSkies((ServerLevel) player.level(), dimension, player, x, y, z, player.getYRot(), player.getXRot());

                                                    context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Teleported you and your ship!"), true);
                                                    return 1;
                                                }))))));
    }
}
