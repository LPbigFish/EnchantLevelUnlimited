package me.lpbigfish.enchantLevelUnlimited.enchant

object LegacyNames {
    val toLegacy: Map<String, String> = mapOf(
        "protection" to "PROTECTION_ENVIRONMENTAL",
        "fire_protection" to "PROTECTION_FIRE",
        "feather_falling" to "PROTECTION_FALL",
        "blast_protection" to "PROTECTION_EXPLOSIONS",
        "projectile_protection" to "PROTECTION_PROJECTILE",
        "respiration" to "OXYGEN",
        "aqua_affinity" to "WATER_WORKER",
        "sharpness" to "DAMAGE_ALL",
        "smite" to "DAMAGE_UNDEAD",
        "bane_of_arthropods" to "DAMAGE_ARTHROPODS",
        "looting" to "LOOT_BONUS_MOBS",
        "efficiency" to "DIG_SPEED",
        "unbreaking" to "DURABILITY",
        "fortune" to "LOOT_BONUS_BLOCKS",
        "power" to "ARROW_DAMAGE",
        "punch" to "ARROW_KNOCKBACK",
        "flame" to "ARROW_FIRE",
        "infinity" to "ARROW_INFINITE",
        "luck_of_the_sea" to "LUCK"
    )

    fun legacyName(key: String): String = toLegacy[key] ?: key.uppercase()
}
