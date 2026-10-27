package pumpkin2enchantmc.net.mover;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class MoverModInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        MoverController controller = new MoverController();
        controller.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                MoverCommands.register(dispatcher, controller));
    }
}
