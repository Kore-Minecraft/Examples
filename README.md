# Kore Examples

This repository contains various examples of Minecraft datapacks created using [Kore](https://kore.ayfri.com/), a modern Kotlin library for
datapack development.

Kore allows you to create complex Minecraft datapacks using Kotlin, eliminating the need to write raw JSON or `.mcfunction` files manually.

## Examples

### [More Apples](./more-apples)

A recreation of the [MoreApples](https://github.com/Stoupy51/MoreApples) datapack by [Stoupy51](https://github.com/Stoupy51).

This example demonstrates how to:

- Modify loot tables for all types of leaves.
- Use predicates and conditions (e.g., Silk Touch checks).
- Generate a `.zip` datapack file automatically.

The datapack ensures that all leaf types have a chance to drop apples, doubling the drop rate compared to vanilla oak leaves.

## How to run

These examples use [Amper](https://jetbrains.github.io/amper/) as the build system.

To build and generate the datapacks:

1. Clone the repository.
2. Run the main class in the desired example module.
3. The generated datapacks will be located in the `out/` directory.

## Installation

To install one of these datapacks into your Minecraft world:

1. **Generate the datapack**: Follow the [How to run](#how-to-run) instructions to create the `.zip` file.
2. **Locate the file**: Find the generated `.zip` file in the `out/` directory (e.g., `out/more-apples.zip`).
3. **Open your world folder**: In Minecraft, select your world, click **Edit**, then **Open World Folder**.
4. **Copy to datapacks**: Move the `.zip` file into the `datapacks` folder.
5. **Reload**: If the world is already running, use the `/reload` command.

## CI/CD & Publishing

This project uses GitHub Actions to automate the publishing process to [Modrinth](https://modrinth.com/).

The workflow is defined in [`.github/workflows/publish.yml`](./.github/workflows/publish.yml).

### How it works

1. **Trigger**: The workflow runs automatically when a new release is published or can be triggered manually via `workflow_dispatch`.
2. **Build**: It uses [Amper](https://jetbrains.github.io/amper/) to build the project and generate the datapack files.
3. **Publish**: The `Kir-Antipov/mc-publish` action takes the generated `.zip` files from the `out/` directory and uploads them to Modrinth.

This ensures that every release of this repository is automatically available for users to download.

## License

This project is licensed under the GNU v3 License - see the [LICENSE](./LICENSE) file for details.

---
Created with ❤️ using [Kore](https://kore.ayfri.com/).
