package pumpkin2enchantmc.net.mover;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

final class MoverController {
    private static final int TICKS_BETWEEN_MOVES = 10;
    private static final int FORWARD_TELEPORTS_PER_CYCLE = 305;
    private static final int CYCLES = 305;
    private static final double BLOCKS_PER_FORWARD_TELEPORT = 20.0D;
    private static final double BLOCKS_PER_CYCLE = 6100.0D;

    private final Map<UUID, MovementTask> tasks = new HashMap<>();

    void register() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                this.tasks.remove(handler.getPlayer().getUuid()));
    }

    boolean start(ServerPlayerEntity player) {
        if (this.tasks.containsKey(player.getUuid())) {
            return false;
        }

        this.tasks.put(player.getUuid(), new MovementTask());
        return true;
    }

    boolean stop(ServerPlayerEntity player) {
        return this.tasks.remove(player.getUuid()) != null;
    }

    int stopAll() {
        int stopped = this.tasks.size();
        this.tasks.clear();
        return stopped;
    }

    private void tick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, MovementTask>> iterator = this.tasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, MovementTask> entry = iterator.next();
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) {
                iterator.remove();
                continue;
            }

            MovementTask task = entry.getValue();
            task.ticksUntilMove--;
            if (task.ticksUntilMove > 0) {
                continue;
            }
            task.ticksUntilMove = TICKS_BETWEEN_MOVES;

            if (!teleportRelativeX(player, BLOCKS_PER_FORWARD_TELEPORT)) {
                player.sendMessage(Text.literal("Mover stopped because a teleport failed."), false);
                iterator.remove();
                continue;
            }

            task.forwardTeleports++;
            if (task.forwardTeleports < FORWARD_TELEPORTS_PER_CYCLE) {
                continue;
            }

            // This is the equivalent of /tp ~-6100 ~ ~ immediately after the 305th forward move.
            if (!teleportRelativeX(player, -BLOCKS_PER_CYCLE)) {
                player.sendMessage(Text.literal("Mover stopped because the return teleport failed."), false);
                iterator.remove();
                continue;
            }

            task.forwardTeleports = 0;
            task.completedCycles++;
            if (task.completedCycles >= CYCLES) {
                player.sendMessage(Text.literal("Mover finished all 305 movement cycles."), false);
                iterator.remove();
            }
        }
    }

    private static boolean teleportRelativeX(ServerPlayerEntity player, double offsetX) {
        ServerWorld world = (ServerWorld) player.getWorld();
        return player.teleport(
                world,
                player.getX() + offsetX,
                player.getY(),
                player.getZ(),
                Set.<PositionFlag>of(),
                player.getYaw(),
                player.getPitch(),
                false
        );
    }

    private static final class MovementTask {
        private int ticksUntilMove = TICKS_BETWEEN_MOVES;
        private int forwardTeleports;
        private int completedCycles;
    }
}
