# Kore Examples

Minecraft datapacks written in Kotlin with [Kore](https://kore.ayfri.com), a library that generates datapacks from a
Kotlin DSL instead of hand-written JSON and `.mcfunction` files.

Each folder is a standalone example, from a single recipe to a complete game. Their generated output is committed in
[`out/`](./out), so you can compare the Kotlin with the datapack it produces.

## Examples

### [Ore Tycoon](./ore-tycoon)

An idle mining tycoon on a floating island: punch a giant ore, buy drills that mine for you, grab golden nuggets,
evolve the core through 8 levels and prestige. Co-op, pure vanilla, playable in singleplayer.

It shows how a full game fits together with Kore:

- Clickable interaction entities detected with advancements.
- Live holograms, a sidebar and a boss bar with formatted numbers.
- A pause screen menu built with the `Menu` helper, usable without operator rights.
- Scoreboard math on fake players with the `oop` module, macros, storage and a procedurally generated island.

### [More Apples](./more-apples)

A recreation of the [MoreApples](https://github.com/Stoupy51/MoreApples) datapack by
[Stoupy51](https://github.com/Stoupy51): every type of leaves can drop apples, twice as often as vanilla oak leaves.

It shows how to:

- Override vanilla loot tables for all leaves.
- Use predicates and conditions such as Silk Touch checks.

### [Rotten Flesh To Leather](./rotten-flesh-to-leather)

A recreation of the [rotten-flesh-to-leather](https://modrinth.com/datapack/rotten-flesh-to-leather) datapack: smoke
rotten flesh for leather and smelt it for brown dye.

It shows how to add smelting and smoking recipes.

## How to run

The examples build with the JetBrains Kotlin toolchain, whose wrapper is committed and downloads the toolchain on its
first run.

```shell
./kotlin run -m ore-tycoon       # Windows: .\kotlin.bat run -m ore-tycoon
```

The datapack is generated in `out/<pack name>`. With the `CI` environment variable set, a `.zip` is generated instead.

`project.yaml` includes every folder holding a `module.yaml`, so a new example only needs its own folder.

## Installation

1. Generate the datapack, or download it from Modrinth.
2. In Minecraft, select your world, click **Edit**, then **Open World Folder**.
3. Copy the datapack folder or `.zip` into the `datapacks` folder.
4. Open the world, or run `/reload` if it is already open.

## Publishing

[`.github/workflows/publish.yml`](./.github/workflows/publish.yml) publishes one example to
[Modrinth](https://modrinth.com/) per GitHub release. The release tag picks the example and the version:
`ore-tycoon-v1.0.0` builds `ore-tycoon` and uploads `ore-tycoon-1.0.0.zip` with its `CHANGELOG.md`. The workflow can
also be started by hand from the Actions tab.

The Modrinth project of each example is set in the workflow and needs a `MODRINTH_TOKEN` repository secret, see
[`ore-tycoon/PUBLISHING.md`](./ore-tycoon/PUBLISHING.md).

## License

This project is licensed under the GNU v3 License, see the [LICENSE](./LICENSE) file for details.

---
Created with ❤️ using [Kore](https://kore.ayfri.com/).
