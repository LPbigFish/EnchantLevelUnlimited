package me.lpbigfish.enchantLevelUnlimited.enchant

import org.bukkit.enchantments.Enchantment

class EnchantLimits(private val configEnchants: Map<String, Int>) {
    val configuredCount: Int get() = configEnchants.size

    fun maxFor(key: String, enchant: Enchantment): Int {
        return (configEnchants[key] ?: enchant.maxLevel).coerceAtMost(255)
    }
}
