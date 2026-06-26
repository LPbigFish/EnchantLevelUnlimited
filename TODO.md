# EnchantLevelUnlimited — v1.0.0 Release Checklist

## Infrastructure
- [x] Set up Gradle + Kotlin 2.4.0 + Shadow 9.4.2
- [x] Detekt config with relaxed reflection rules
- [x] bStats metrics (bstats-bukkit 3.2.1)
- [x] VERSION file for gitraw update checking
- [x] Register plugin on bStats.org and update plugin ID (32187)

## Plugin Functionality
- [x] Anvil combine logic (same-level +1, higher wins, never reduces)
- [x] Configurable max levels per enchant (capped at 255)
- [x] XP cost strategies (clamp / raise-cap)
- [x] Conflict groups (hard-blocked + 5 toggleable groups)
- [x] Cross-version support via reflection (1.8 — 1.21+)
- [x] Prior-work penalty reset
- [x] Rename text preserved
- [x] `/enchantlevelunlimited reload` command
- [x] `/enchantlevelunlimited info` command
- [x] Tab completion for `/elu` command
- [x] Config validation (warn on unknown enchants, clamp values)
- [x] Tool-incompatible enchant blocking (`allow-incompatible-tool`)
- [x] EnchantResolver keyOf with reflection fallback for Paper 1.21+

## Polish
- [x] README.md with full documentation
- [x] plugin.yml with commands, permissions, website
- [x] Log server version on startup
- [x] bStats custom charts (cost strategy, conflict toggles, override count)
- [ ] Plugin icon (128x128+) — create for resource pages
- [ ] Screenshots for SpigotMC / BuiltByBit / Modrinth
- [ ] GitHub Actions CI workflow
- [ ] Register on SpigotMC, BuiltByBit, Modrinth

## Testing
- [x] 61 unit tests across 9 test files
- [x] Config at-cap combine produces result (not vanilla fallback)
- [x] In-game test scenarios documented in TEST.md (22 tests)
- [x] 255 hard cap verified on Paper 1.21+
- [x] Incompatible tool blocking verified
