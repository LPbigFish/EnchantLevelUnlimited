# EnchantLevelUnlimited

Remove enchantment level limits in anvils. Combine enchants beyond vanilla caps on Minecraft 1.8—1.21+.

## Features

- **Unlimited combining**: Two Sharpness V books produce Sharpness VI, not V. Keep going until you hit your configured cap (max 255).
- **Per-enchant config**: Set max levels individually (1—255). Enchants not listed use vanilla defaults.
- **Tool compatibility**: Optional `allow-incompatible-tool` setting blocks enchants from going on wrong item types (e.g., Silk Touch on a sword).
- **Cross-version**: Works on CraftBukkit, Spigot, Paper, and Folia from 1.12.2 through 1.21+.
- **Cost strategies**: `clamp` — always under "Too Expensive" (works everywhere); `raise-cap` — lift the cap entirely on 1.13+.
- **Conflict control**: Hard-blocked pairs (Fortune/Silk Touch, Loyalty/Channeling + Riptide) always enforced. Five toggleable groups (damage, protection, bow, boots, crossbow).
- **Prior-work penalty reset**: Combined items come out with 0 prior work cost — no exponential XP escalation.
- **Lightweight**: No dependencies, no NMS, no world edits. Drop in and go.

## Installation

1. Download the jar from [SpigotMC](https://www.spigotmc.org/) / [BuiltByBit](https://builtbybit.com/) / [Modrinth](https://modrinth.com/).
2. Place it in your server's `plugins/` folder.
3. Restart the server (or `/reload` — though restart is recommended).
4. Edit `plugins/EnchantLevelUnlimited/config.yml` to set your preferred max levels.
5. Run `/elu reload` to apply changes without restarting.

## Commands

| Command | Permission | Description |
|---|---|---|
| `/elu reload` | `enchantlevelunlimited.reload` | Reload configuration from disk |
| `/elu info` | `enchantlevelunlimited.info` | Show plugin version, strategy, status |
| `/elu` | — | Show command help |

Alias: `/enchantlevelunlimited`

## Permissions

| Permission | Default | Description |
|---|---|---|
| `enchantlevelunlimited.reload` | `op` | Reload the configuration |
| `enchantlevelunlimited.info` | `true` | View plugin information |
| `enchantlevelunlimited.*` | `op` | All permissions |

## Configuration

### `settings`

```yaml
settings:
  cost-strategy: clamp
  reset-penalty: true
  allow-damage-conflicts: false
  allow-protection-conflicts: false
  allow-bow-conflicts: false
  allow-boots-conflicts: false
  allow-crossbow-conflicts: false
  allow-incompatible-tool: false
```

| Option | Values | Description |
|---|---|---|
| `cost-strategy` | `clamp` / `raise-cap` | XP cost when combine exceeds "Too Expensive" (cost ≥ 40). `clamp` caps at 39 levels, works on all versions. `raise-cap` removes the cap on 1.13+ but falls back to `clamp` on older versions. |
| `reset-penalty` | `true` / `false` | Reset prior-work penalty to 0 on combined results. Prevents exponential cost growth. |
| `allow-*-conflicts` | `true` / `false` | Allow normally incompatible enchants to coexist (e.g., Sharpness + Smite). Hard-blocked pairs (Fortune/Silk Touch, Loyalty/Riptide, Channeling/Riptide) ignore this setting. |
| `allow-incompatible-tool` | `true` / `false` | `false` (default) blocks enchants from going on wrong item types (e.g., Silk Touch on a sword). `true` allows any enchant on any item. Enchanted books always bypass this check. |

### `enchants`

Each entry is `enchant_key: max_level`. Keys are the Minecraft namespaced ID (e.g., `sharpness`, `protection`, `mending`). Enchants not listed use their vanilla `getMaxLevel()`.

```yaml
enchants:
  sharpness: 10
  protection: 10
  mending: 5
```

Defaults are sensible for most servers: combat enchants cap at 10, utility enchants at 1—5. The full generated config has all 40 enchants annotated by Minecraft version.

**Hard cap**: Enchant levels are limited to **255** by Minecraft/Paper. Values above 255 are clamped automatically.

## Supported Versions

| Version | Status |
|---|---|
| 1.12.2 — 1.16.5 | Full support (both cost strategies) |
| 1.17 — 1.21+ | Full support (both cost strategies) |

Works on CraftBukkit, Spigot, Paper, and Folia.

## Building from source

```bash
git clone https://github.com/LPbigFish/EnchantLevelUnlimited.git
cd EnchantLevelUnlimited
./gradlew build
```

The output jar is at `build/libs/EnchantLevelUnlimited-1.0.0-all.jar`.

## Metrics

This plugin uses bStats (https://bstats.org) to collect anonymous usage statistics. You can opt out in `plugins/bStats/config.yml`.

## Links

<!-- - [SpigotMC](https://www.spigotmc.org/) -->
- [BuiltByBit](https://builtbybit.com/resources/enchantlevelunlimited.114351/)
<!-- - [Modrinth](https://modrinth.com/) -->
- [GitHub](https://github.com/LPbigFish/EnchantLevelUnlimited)
