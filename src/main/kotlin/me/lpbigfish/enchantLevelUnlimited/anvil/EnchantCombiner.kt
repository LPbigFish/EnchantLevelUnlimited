package me.lpbigfish.enchantLevelUnlimited.anvil

import me.lpbigfish.enchantLevelUnlimited.config.Settings
import me.lpbigfish.enchantLevelUnlimited.enchant.ConflictGroups
import me.lpbigfish.enchantLevelUnlimited.enchant.EnchantLimits
import me.lpbigfish.enchantLevelUnlimited.enchant.EnchantResolver
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import kotlin.math.min

class EnchantCombiner(
    private val limits: EnchantLimits,
    private val settings: Settings
) {
    data class AppliedEnchant(val enchant: Enchantment, val key: String, val resultingLevel: Int)

    data class Result(
        val enchants: Map<Enchantment, Int>,
        val applied: List<AppliedEnchant>,
        val sacrificeIsBook: Boolean,
        val incompatible: Int
    )

    fun compute(target: ItemStack, sacrifice: ItemStack): Result? {
        val sacrificeIsBook = sacrifice.itemMeta is EnchantmentStorageMeta
        val sacrificeEnchants = readEnchants(sacrifice) ?: return null
        if (sacrificeEnchants.isEmpty()) return null

        val targetEnchants = readEnchants(target) ?: emptyMap()
        val allEnchants = targetEnchants.toMutableMap()
        val allKeys = allEnchants.keys.map { EnchantResolver.keyOf(it) }.toMutableSet()
        val applied = mutableListOf<AppliedEnchant>()
        var incompatible = 0

        for ((enchant, sLevel) in sacrificeEnchants) {
            if (!settings.allowIncompatibleTool && !isToolCompatible(enchant, target)) {
                incompatible++
                continue
            }
            processEnchant(enchant, sLevel, allEnchants, allKeys, applied)
        }

        return Result(allEnchants.toMap(), applied, sacrificeIsBook, incompatible)
    }

    // ponytail: getItemTarget/canEnchantItem removed in Paper 1.21+ — try-catch for cross-version safety
    private fun isToolCompatible(enchant: Enchantment, target: ItemStack): Boolean {
        if (target.itemMeta is EnchantmentStorageMeta) return true
        return try { enchant.canEnchantItem(target) } catch (_: Exception) { true }
    }

    private fun processEnchant(
        enchant: Enchantment,
        sLevel: Int,
        allEnchants: MutableMap<Enchantment, Int>,
        allKeys: MutableSet<String>,
        applied: MutableList<AppliedEnchant>
    ) {
        val key = EnchantResolver.keyOf(enchant)
        val configuredMax = limits.maxFor(key, enchant)
        val tLevel = allEnchants[enchant] ?: 0
        val wasPresent = enchant in allEnchants

        val rawLevel = when {
            tLevel == sLevel -> sLevel + 1
            sLevel > tLevel -> sLevel
            else -> tLevel
        }
        val newLevel = min(rawLevel, configuredMax).coerceAtLeast(tLevel)

        if (newLevel <= 0) return
        if (ConflictGroups.isBlocked(key, allKeys, settings.toggles)) return

        allEnchants[enchant] = newLevel
        allKeys.add(key)

        val hitCap = rawLevel > newLevel
        if (!wasPresent || tLevel != newLevel || hitCap) {
            applied.add(AppliedEnchant(enchant, key, newLevel))
        }
    }

    companion object {
        fun readEnchants(item: ItemStack): Map<Enchantment, Int>? {
            val meta = item.itemMeta ?: return null
            return readEnchantsFromMeta(meta)
        }

        fun readEnchantsFromMeta(meta: ItemMeta): Map<Enchantment, Int> {
            return if (meta is EnchantmentStorageMeta) meta.storedEnchants else meta.enchants
        }

        fun clearEnchants(meta: ItemMeta) {
            if (meta is EnchantmentStorageMeta) {
                meta.storedEnchants.keys.toList().forEach { meta.removeStoredEnchant(it) }
            } else {
                meta.enchants.keys.toList().forEach { meta.removeEnchant(it) }
            }
        }

        fun applyEnchant(meta: ItemMeta, enchant: Enchantment, level: Int) {
            if (meta is EnchantmentStorageMeta) {
                meta.addStoredEnchant(enchant, level, true)
            } else {
                meta.addEnchant(enchant, level, true)
            }
        }
    }
}
