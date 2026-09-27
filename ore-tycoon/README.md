# Ore Tycoon

An idle mining tycoon floating in the sky, in pure vanilla Minecraft: no mods, no resource pack. Punch a giant ore
to mine coins, buy drills that keep mining while you are away, upgrade your pickaxe, evolve the core and mine a
billion coins. Friends share the same factory, so it doubles as a co-op game.

Written in Kotlin with [Kore](https://kore.ayfri.com): 1,700 lines of Kotlin generate 110 datapack files and more than
3,000 commands.

## Features

- **The core**: a giant spinning ore in the middle of the island. Hit it or right-click it to mine, it squashes and
  throws a floating `+N` at you.
- **7 drill stations**: Coal, Copper, Iron, Gold, Diamond, Emerald and Netherite. Each copy mines coins every second and
  costs 15% more than the last, up to 25 copies each. Owned drills throw their ore into the core every second.
- **Pickaxe forge**: 5 pickaxes, the best ones also add a share of the production to every click.
- **Golden nuggets**: a glowing nugget appears every minute or so. Grab it within 15 seconds for 30 seconds of
  production, or a ×3 frenzy on everything for 20 seconds.
- **8 levels**: every lifetime milestone (1K, 10K ... 1B) evolves the core, from stone to a beacon, with fireworks and a
  beacon beam at the end.
- **Prestige**: once the beacon is lit, start over at the altar for a permanent +25% bonus per star, up to 20 stars.
- **Live HUD**: holograms on every station, a sidebar, and a boss bar showing the progress to the next level.
  Numbers are shortened like `12.3K` or `4.56M`.
- **Menu**: a dialog on the pause screen with Play, Leave, how to play and credits. It works without operator rights.
- **Travel**: Play saves your position and dimension, Leave brings you back exactly there.
- **Clean uninstall**: one function removes every block, entity, score, team, boss bar and storage of the pack.

## Try it

Minecraft **Java 26.2**.

1. Download `ore-tycoon-<version>.zip` from Modrinth, or build it (see below).
2. Put the zip in the `datapacks` folder of a world **while the world is closed**, or pick it in the *Data Packs* screen
   when creating a new world. Cheats are not needed.
3. Open the world and click **[▶ Play]** in the chat, or press <kbd>Esc</kbd> and click **Ore Tycoon**.

The factory builds itself above `x=0 z=0`, at `y=280`. Survival players are switched to adventure mode inside it, so the
island can't be broken, and get their gamemode back when they leave.

> Added to a world that is already open? `/reload` loads the game and the chat **[▶ Play]** link, but the menu
> only shows up after leaving and rejoining the world: Minecraft registers dialogs when a world loads.

### Operator commands

| Command                                            | Effect                                                        |
|----------------------------------------------------|---------------------------------------------------------------|
| `/function ore_tycoon:admin/give {amount:1000000}` | Adds coins, up to 100M per call. Handy to test the late game. |
| `/function ore_tycoon:admin/golden_nugget`         | Spawns a golden nugget right away.                            |
| `/function ore_tycoon:admin/rebuild`               | Rebuilds the island and its entities, the progress is kept.   |
| `/function ore_tycoon:admin/reset`                 | Resets the progress, stars included.                          |
| `/function ore_tycoon:admin/uninstall`             | Sends everyone home and deletes the factory and all its data. |

Run `uninstall` before removing the zip, otherwise the island stays in the sky.

## Build

From the repository root, with the JetBrains Kotlin toolchain wrapper:

```shell
./kotlin run -m ore-tycoon       # Windows: .\kotlin.bat run -m ore-tycoon
```

The pack is generated in `out/ore_tycoon`. With the `CI` environment variable set, a zip is generated instead.

## How it is made

| File          | What it does                                                                                           |
|---------------|--------------------------------------------------------------------------------------------------------|
| `main.kt`     | The `dataPack { }` entry: pack metadata, wiring of every system, the tick and per-second loops, `load` |
| `Config.kt`   | Tuning and layout: drills, pickaxes, levels, station positions on a ring around the core               |
| `State.kt`    | Scoreboard objectives, global scores on fake players, the storage                                      |
| `Format.kt`   | `NumberFormatter`: scores to `12.3K` strings with scoreboard math and macros                           |
| `Factory.kt`  | The island (procedural rock, floor, garden, barriers) and its entities, built with run-length `fill`s  |
| `Economy.kt`  | Earning, production and click power, price tables, the shop                                            |
| `Core.kt`     | Mining the core: coins, squash animation, floating `+N`                                                |
| `Flyers.kt`   | Ores flying from the drills to the core                                                                |
| `Golden.kt`   | Golden nuggets: random spawn, expiry, lucky bonus or frenzy                                            |
| `Progress.kt` | Levels, victory fireworks, prestige, reset                                                             |
| `Hud.kt`      | Holograms, sidebar and boss bar                                                                        |
| `Players.kt`  | Play and Leave, saved return points, players entering or leaving the island                            |
| `Clicks.kt`   | Clicks on interaction entities, detected through advancements                                          |
| `Menu.kt`     | The pause screen menu                                                                                  |
| `Admin.kt`    | Operator commands                                                                                      |

### Kore features used

- **Core DSL**: functions, `load`/`tick`, `execute` chains with `return`, scoreboards, storage, `summon`, `fill`,
  particles, sounds, titles, teams, boss bars, macros (`macro("id")` in NBT paths and `tp` arguments).
- **Data-driven features**: advancements (`player_interacted_with_entity`, `player_hurt_entity`), dialogs and a
  dialog tag.
- **`oop` module**: `ScoreboardEntity` math between scores (`coins -= cost`, `gain maxWith ...`) on fake players.
- **`helpers` module**: `Menu` (dialog GUI with trigger buttons, pause screen entry), `Sidebar`, display entity builders
  and transformations, the VFX engine (`drawCircle`, `drawShape` helix).
- **Kotlin at build time**: the price curve (`base × 1.15ⁿ`) becomes lookup tables, the island is carved by
  seeded noise, stations are placed with trigonometry, and every repetitive command (7 drills, 5 pickaxes, 8 levels)
  comes from loops.

### Techniques worth stealing

- **Clickable blocks**: an `interaction` entity records its last user and attacker. An advancement triggers on any
  entity click, its reward function looks for boxes holding click data, and `execute on target` (or `on attacker`) runs
  the action as the player who clicked.
- **Live numbers in holograms**: setting a text display's `text` with `data modify` resolves its score and NBT
  components on the spot, `interpret: true` renders a stored string as plain text.
- **Smooth movement for free**: `teleport_duration` makes the client interpolate each `tp`, so a spinning block or a
  flying ore costs one command per second, not one per tick.
- **Overflow-safe economy**: every gain is capped at 100M and every total at 2B, so no sum can wrap past the 32-bit
  score limit.
- **Menus without operator rights**: dialog buttons run `/trigger`, which any player can use.

## Limitations

- Minecraft 26.2 only.
- One factory per world, shared by everyone.
- Sidebar slots are global in vanilla, so the sidebar is shown through the gold team's slot: players in the factory
  join the `ot.players` team and leave it on their way out, losing any team they had before.

## License

GPL-3.0, like the rest of this repository.
