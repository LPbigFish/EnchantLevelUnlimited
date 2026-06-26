# EnchantLevelUnlimited - Test Guide

## Unit Tests

Run all unit tests:
```bash
./gradlew test
```

### Test Files

| File | Tests | Description |
|---|---|---|
| `anvil/EnchantCombinerTest.kt` | 15 | Level math, conflict filtering, tool-incompatible filtering, book bypass, exception handling, null/empty edge cases |
| `anvil/CostTableTest.kt` | 4 | Item/book XP multipliers, unknown fallback, book <= item pattern |
| `anvil/AnvilHandlerTest.kt` | 3 | Block signal on incompatible, no-op on no change |
| `enchant/ConflictGroupsTest.kt` | 16 | Hard-blocked pairs, toggleable groups, cross-group, hard block vs toggles |
| `enchant/EnchantResolverTest.kt` | 5 | Legacy name resolution, fallback, getName exception handling |
| `enchant/EnchantLimitsTest.kt` | 5 | Config override, vanilla fallback, 255 hard cap clamp |
| `enchant/LegacyNamesTest.kt` | 3 | All 19 vanilla-to-Bukkit name mappings, fallback, map size |
| `config/CostStrategyTest.kt` | 5 | String-to-enum parsing, case-insensitivity, defaults |
| `util/ServerVersionTest.kt` | 5 | Version comparison, isAtLeast, pair overload |

**Total: 61 tests**

---

## In-Game Tests

These require a running Minecraft server (1.9+ for PrepareAnvilEvent support) with the plugin installed.

> **Note:** Enchant levels are hard-capped at **255** by Minecraft/Paper. The `/give` command also enforces this max.

### Setup

1. Build the plugin: `./gradlew build`
2. Copy `build/libs/EnchantLevelUnlimited-*-all.jar` to your server's `plugins/` folder
3. Start the server (or `/elu reload` after updating)
4. Use `/give @p diamond_sword[enchantments={"minecraft:sharpness":5}]` (1.20.5+) or `/give @p diamond_sword{Enchantments:[{id:"sharpness",lvl:5}]}` (pre-1.20.5)

### Test 1: Basic Level-Up Combine

**Goal:** Two Sharpness V swords combine to Sharpness VI (beyond vanilla max of 5)

1. Give yourself two diamond swords with Sharpness V:
   ```
   /give @p diamond_sword[enchantments={"minecraft:sharpness":5}]
   ```
   (Pre-1.20.5: `/give @p diamond_sword{Enchantments:[{id:"sharpness",lvl:5}]}`)
2. Place both in an anvil
3. **Expected:** Result shows Sharpness VI (or higher if config max > 10)
4. Take the result, verify the enchantment level in the tooltip

### Test 2: Config Max Level Cap

**Goal:** Enchant does not exceed configured max

1. Set `sharpness: 7` in `config.yml`, reload
2. Combine two Sharpness VII swords
3. **Expected:** Result stays at Sharpness VII (capped at config max, not upgraded to VIII)
4. Combine Sharpness VI + Sharpness VII
5. **Expected:** Result is Sharpness VII (higher level wins)

### Test 3: Higher Sacrifice Level Wins

**Goal:** When sacrifice has higher level, target upgrades

1. Place a Sharpness III sword in slot 0
2. Place a Sharpness V sword in slot 1
3. **Expected:** Result has Sharpness V

### Test 4: Lower Sacrifice Level Ignored

**Goal:** When sacrifice has lower level, target keeps its level

1. Place a Sharpness V sword in slot 0
2. Place a Sharpness III sword in slot 1
3. **Expected:** Result has Sharpness V (unchanged, no cost charged for this enchant)

### Test 5: Enchanted Book Transfer

**Goal:** Enchant from a book applies to an item, beyond vanilla max

1. Give yourself a sword with Sharpness V
2. Give yourself an enchanted book with Sharpness V
3. Combine in anvil
4. **Expected:** Result has Sharpness VI

### Test 6: Hard-Blocked Conflicts

**Goal:** Fortune and Silk Touch never combine

1. Give yourself a pickaxe with Fortune III
2. Give yourself an enchanted book with Silk Touch
3. Combine in anvil
4. **Expected:** Silk Touch is NOT applied to the result (only Fortune remains)

### Test 7: Toggleable Conflicts - Off (Default)

**Goal:** Damage enchants (Sharpness/Smite/BoA) conflict by default

1. Give yourself a sword with Sharpness V
2. Give yourself an enchanted book with Smite V
3. Combine in anvil
4. **Expected:** Smite is NOT applied (only Sharpness remains)

### Test 8: Toggleable Conflicts - On

**Goal:** Damage enchants coexist when config allows

1. Set `allow-damage-conflicts: true` in `config.yml`, reload
2. Give yourself a sword with Sharpness V
3. Give yourself an enchanted book with Smite V
4. Combine in anvil
5. **Expected:** Result has BOTH Sharpness V and Smite V

### Test 9: Protection Conflict Toggle

**Goal:** All 4 protection types coexist when toggled on

1. Set `allow-protection-conflicts: true`
2. Give a chestplate with Protection IV
3. Combine with a book of Fire Protection IV
4. **Expected:** Result has both Protection IV and Fire Protection IV

### Test 10: Prior-Work Penalty Reset

**Goal:** Combined items don't escalate in cost from prior anvil uses

1. Combine sword A + sword B → result C (note the XP cost)
2. Combine result C + sword D → result E
3. **Expected:** The cost for the second combine is NOT exponentially higher (penalty was reset to 0)
4. Repeat combining several times — cost should remain stable, not escalate

### Test 11: Cost Strategy - Clamp (Default)

**Goal:** Cost never shows "Too Expensive" — it caps at 39

1. Set `cost-strategy: clamp`
2. Give yourself a sword with 5 high-level enchants (Sharpness V, Unbreaking III, etc.)
3. Combine with another heavily enchanted sword
4. **Expected:** Cost is capped at 39 levels, combine always works

### Test 12: Cost Strategy - Raise Cap (1.13+)

**Goal:** Cost reflects actual enchantment value, cap removed

1. Set `cost-strategy: raise-cap`
2. Combine two heavily enchanted swords
3. **Expected:** Cost may exceed 39 (e.g., 50+), player must have enough XP
4. **Note:** This only works on 1.13+ servers (falls back to clamp on 1.8-1.12)

### Test 13: Rename Preserved

**Goal:** Renaming an item in the anvil works alongside enchant combining

1. Place a Sharpness V sword in slot 0
2. Place a Sharpness V sword in slot 1
3. Type "Excalibur" in the rename field
4. **Expected:** Result has Sharpness VI AND is named "Excalibur"

### Test 14: Durability Repair Preserved

**Goal:** Combining two damaged items still repairs durability

1. Give yourself a damaged diamond sword (50% durability) with Sharpness V
2. Give yourself another damaged diamond sword (30% durability) with Sharpness V
3. Combine in anvil
4. **Expected:** Result has Sharpness VI AND repaired durability (more than either input)

### Test 15: No Enchant Change = Vanilla Behavior

**Goal:** When sacrifice enchants are all lower level, vanilla handles it

1. Place a Sharpness V, Unbreaking III sword in slot 0
2. Place a Sharpness I sword in slot 1
3. **Expected:** Plugin does not override — vanilla result shows (Sharpness V, Unbreaking III, plus any repair)

### Test 16: Book-to-Book Combine

**Goal:** Two enchanted books combine their stored enchants

1. Give yourself a book with Sharpness V
2. Give yourself a book with Unbreaking III
3. Combine in anvil
4. **Expected:** Result book has both Sharpness V and Unbreaking III (stored enchants)

### Test 17: Server Version Detection

**Goal:** Plugin logs correct version on startup

1. Check server console on startup
2. **Expected:** Log message "Anvil combine handler registered" on 1.9+
3. On 1.8: Log message "PrepareAnvilEvent not available on this server version - anvil combine disabled"

### Test 18: Config Reload After Edit

**Goal:** Config changes take effect after restart

1. Set `sharpness: 20` in config
2. Restart server (or reload)
3. Combine two Sharpness X swords
4. **Expected:** Result has Sharpness XI (config max 20 allows it)

### Test 19: Incompatible Tool Blocking

**Goal:** Silk Touch cannot be applied to a sword when `allow-incompatible-tool: false`

1. Ensure `allow-incompatible-tool: false` in config
2. Give yourself a diamond sword with Sharpness V
3. Give yourself an enchanted book with Silk Touch
4. Combine in anvil
5. **Expected:** Silk Touch is NOT applied — result has only Sharpness (or Sharpness VI if combining)

### Test 20: Incompatible Tool Allowed

**Goal:** Silk Touch CAN be applied to a sword when `allow-incompatible-tool: true`

1. Set `allow-incompatible-tool: true` in config, reload
2. Give yourself a diamond sword
3. Give yourself an enchanted book with Silk Touch
4. Combine in anvil
5. **Expected:** Result has Silk Touch on the sword

### Test 21: 255 Hard Cap

**Goal:** Enchant levels cannot exceed 255

1. Set `sharpness: 255` in config, reload
2. Give two swords with Sharpness 255:
   ```
   /give @p diamond_sword[enchantments={"minecraft:sharpness":255}]
   ```
3. Combine in anvil
4. **Expected:** Result stays at Sharpness 255 (cannot go to 256)
5. **Expected:** Cost matches the level (no overcharge)

### Test 22: Tab Completion

**Goal:** `/elu` command has tab completion

1. Type `/elu ` and press Tab
2. **Expected:** Shows `reload` and `info`
3. Type `/elu re` and press Tab
4. **Expected:** Completes to `/elu reload`
