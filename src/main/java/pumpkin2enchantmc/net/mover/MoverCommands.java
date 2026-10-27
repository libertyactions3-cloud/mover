package pumpkin2enchantmc.net.mover;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class MoverCommands {
    private MoverCommands() {
    }

    public static void register(
            CommandDispatcher<ServerCommandSource> dispatcher,
            MoverController controller
    ) {
        dispatcher.register(CommandManager.literal("mover_mod")
                .then(CommandManager.literal("start")
                        .executes(context -> start(context, controller)))
                .then(CommandManager.literal("stop")
                        .executes(context -> stop(context, controller))));
    }

    private static int start(
            CommandContext<ServerCommandSource> context,
            MoverController controller
    ) {
        ServerPlayerEntity player;
        try {
            player = context.getSource().getPlayer();
        } catch (Exception exception) {
            context.getSource().sendError(Text.literal("/mover_mod start can only be used by a player."));
            return 0;
        }

        if (!controller.start(player)) {
            context.getSource().sendError(Text.literal("Your Mover loop is already running."));
            return 0;
        }

        context.getSource().sendFeedback(
                () -> Text.literal("Mover started. Use /mover_mod stop to stop immediately."),
                false
        );
        return 1;
    }

    private static int stop(
            CommandContext<ServerCommandSource> context,
            MoverController controller
    ) {
        ServerPlayerEntity player;
        try {
            player = context.getSource().getPlayer();
        } catch (Exception exception) {
            player = null;
        }

        if (player != null) {
            if (!controller.stop(player)) {
                context.getSource().sendError(Text.literal("Your Mover loop is not running."));
                return 0;
            }

            context.getSource().sendFeedback(
                    () -> Text.literal("Mover stopped immediately."),
                    false
            );
            return 1;
        }

        int stopped = controller.stopAll();
        context.getSource().sendFeedback(
                () -> Text.literal("Stopped " + stopped + " active Mover loop(s)."),
                true
        );
        return 1;
    }
}
