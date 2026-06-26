package me.lpbigfish.enchantLevelUnlimited.enchant

object ConflictGroups {
    val DAMAGE: Set<String> = ["sharpness", "smite", "bane_of_arthropods"]
    val PROTECTION: Set<String> = ["protection", "fire_protection", "blast_protection", "projectile_protection"]
    val BOW: Set<String> = ["infinity", "mending"]
    val BOOTS: Set<String> = ["depth_strider", "frost_walker"]
    val CROSSBOW: Set<String> = ["multishot", "piercing"]

    val HARD_BLOCKS: Set<Set<String>> = [
        ["fortune", "silk_touch"],
        ["loyalty", "riptide"],
        ["channeling", "riptide"]
    ]

    val TOGGLEABLE: Map<Set<String>, String> = mapOf(
        DAMAGE to "allow-damage-conflicts",
        PROTECTION to "allow-protection-conflicts",
        BOW to "allow-bow-conflicts",
        BOOTS to "allow-boots-conflicts",
        CROSSBOW to "allow-crossbow-conflicts"
    )

    fun isBlocked(key: String, existingKeys: Set<String>, toggles: Map<String, Boolean>): Boolean {
        for (pair in HARD_BLOCKS) {
            if (key !in pair) continue
            val other = pair - key
            if (existingKeys.any { it in other }) return true
        }

        for ((group, configKey) in TOGGLEABLE) {
            if (key !in group) continue
            if (existingKeys.any { it in group && it != key }) {
                if (toggles[configKey] != true) return true
            }
        }

        return false
    }
}
