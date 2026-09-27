# Publishing Ore Tycoon

[`.github/workflows/publish.yml`](../.github/workflows/publish.yml) builds the zip and uploads it to Modrinth with
[`mc-publish`](https://github.com/Kira-NT/mc-publish) when a GitHub release tagged `ore-tycoon-v<version>` is published.

## Listing

| Field         | Value                                                                                                                           |
|---------------|---------------------------------------------------------------------------------------------------------------------------------|
| Name          | Ore Tycoon                                                                                                                      |
| Modrinth slug | `ore-tycoon` (the `modrinth` value of the workflow's `ore-tycoon` case)                                                         |
| Project type  | Data Pack                                                                                                                       |
| Summary       | An idle mining tycoon in the sky: punch a giant ore, buy drills, evolve the core and mine a billion coins. Pure vanilla, co-op. |
| Versions      | 26.2                                                                                                                            |
| Environment   | Server side, works in singleplayer                                                                                              |
| Categories    | Game Mechanics, Minigame, Multiplayer                                                                                           |
| License       | GPL-3.0-or-later                                                                                                                |
| Source        | https://github.com/Kore-Minecraft/Examples/tree/main/ore-tycoon                                                                 |
| Issues        | https://github.com/Kore-Minecraft/Examples/issues                                                                               |

### Description

> **Ore Tycoon** is an idle mining tycoon floating high above your world, made of pure vanilla commands: no mods, no
> resource pack.
>
> Punch the giant ore in the middle of the island to mine coins, then spend them on drills that keep mining while you
> are away. Watch every drill throw its ore into the core, grab the golden nuggets that pop up for a bonus or a ×3
> frenzy, and upgrade your pickaxe at the forge.
>
> The core evolves as you mine, from stone to diamond ore to a beacon. Light the beacon by mining a billion coins, then
> prestige at the altar for a permanent bonus and do it all again, faster.
>
> **Features**
> - 7 drills, 5 pickaxes, 8 core levels and a prestige system
> - Golden nuggets with lucky bonuses and frenzies
> - Holograms, a sidebar and a boss bar, all live
> - A pause screen menu that works without operator rights
> - Play and Leave buttons that bring you back where you were
> - Co-op: everyone on the server shares the same factory
> - A clean uninstall command
>
> **How to start**: add the pack to your world while it is closed, open it and click **[▶ Play]** in the chat, or press
> Esc and click **Ore Tycoon**.
>
> Written in Kotlin with [Kore](https://kore.ayfri.com), the source is open on
> [GitHub](https://github.com/Kore-Minecraft/Examples/tree/main/ore-tycoon).

### Gallery ideas

The island from below with the beacon beam, the core mid-click with `+N` texts, a station hologram, the pause screen
menu, the sidebar and boss bar during a frenzy.

## One-time setup

1. Create the project on Modrinth as a **Data Pack** with the slug `ore-tycoon`. `mc-publish` only uploads versions to
   an existing project.
2. Create a personal access token at <https://modrinth.com/settings/pats> with the **Create versions** scope.
3. Add it to the repository secrets as `MODRINTH_TOKEN` (Settings, Secrets and variables, Actions).

## Releasing a version

1. Add the version to [`CHANGELOG.md`](CHANGELOG.md).
2. Create a GitHub release with the tag `ore-tycoon-v1.0.0`. The workflow uploads `ore-tycoon-1.0.0.zip`.

The workflow can also be started by hand from the Actions tab, picking the module and typing the version.
