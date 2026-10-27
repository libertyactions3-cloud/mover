# Mover

Server-side Fabric mod for Minecraft 1.21.11, configured with the same Gradle, Loom, mappings, loader, and Fabric API versions as BarrelLoot.

## Commands

- `/mover_mod start` starts the caller's movement loop.
- `/mover_mod stop` immediately stops the caller's loop. When used from the server console, it stops all active movement loops.

Each loop waits 10 server ticks (0.5 seconds) between forward teleports. It moves the player 20 blocks in the positive X direction 305 times, then immediately moves them 6100 blocks in the negative X direction. That sequence repeats 305 times before the loop ends automatically. Players can stop their own loop at any point.

The movement runs server-side and does not require the player to have permission to use vanilla `/tp`.

## Building

On Windows, run:

```powershell
./gradlew.bat clean build
```

On macOS/Linux, run:

```sh
./gradlew clean build
```

Gradle 9.5.1 is downloaded by the included launcher if it is not already cached. Java 21 is required.
