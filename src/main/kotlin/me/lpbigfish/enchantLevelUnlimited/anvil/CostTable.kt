package me.lpbigfish.enchantLevelUnlimited.anvil

object CostTable {
    private val ITEM: Map<String, Int> = mapOf(
        "protection" to 1, "fire_protection" to 2, "feather_falling" to 2,
        "blast_protection" to 4, "projectile_protection" to 2, "thorns" to 8,
        "respiration" to 4, "depth_strider" to 4, "aqua_affinity" to 4,
        "sharpness" to 1, "smite" to 2, "bane_of_arthropods" to 2,
        "knockback" to 2, "fire_aspect" to 4, "looting" to 4, "sweeping_edge" to 4,
        "efficiency" to 1, "silk_touch" to 8, "unbreaking" to 2, "fortune" to 4,
        "power" to 1, "punch" to 4, "flame" to 4, "infinity" to 8,
        "luck_of_the_sea" to 4, "lure" to 4,
        "frost_walker" to 4, "mending" to 4,
        "binding_curse" to 8, "vanishing_curse" to 8,
        "loyalty" to 1, "impaling" to 4, "riptide" to 4, "channeling" to 8,
        "multishot" to 4, "quick_charge" to 2, "piercing" to 1,
        "soul_speed" to 8, "swift_sneak" to 8,
        "density" to 2, "breach" to 4, "wind_burst" to 4
    )

    private val BOOK: Map<String, Int> = mapOf(
        "protection" to 1, "fire_protection" to 1, "feather_falling" to 1,
        "blast_protection" to 2, "projectile_protection" to 1, "thorns" to 4,
        "respiration" to 2, "depth_strider" to 2, "aqua_affinity" to 2,
        "sharpness" to 1, "smite" to 1, "bane_of_arthropods" to 1,
        "knockback" to 1, "fire_aspect" to 2, "looting" to 2, "sweeping_edge" to 2,
        "efficiency" to 1, "silk_touch" to 4, "unbreaking" to 1, "fortune" to 2,
        "power" to 1, "punch" to 2, "flame" to 2, "infinity" to 4,
        "luck_of_the_sea" to 2, "lure" to 2,
        "frost_walker" to 2, "mending" to 2,
        "binding_curse" to 4, "vanishing_curse" to 4,
        "loyalty" to 1, "impaling" to 2, "riptide" to 2, "channeling" to 4,
        "multishot" to 2, "quick_charge" to 1, "piercing" to 1,
        "soul_speed" to 4, "swift_sneak" to 4,
        "density" to 1, "breach" to 2, "wind_burst" to 2
    )

    val knownKeys: Set<String> get() = ITEM.keys

    fun multiplier(key: String, isBook: Boolean): Int {
        val table = if (isBook) BOOK else ITEM
        return table[key] ?: 1
    }
}
