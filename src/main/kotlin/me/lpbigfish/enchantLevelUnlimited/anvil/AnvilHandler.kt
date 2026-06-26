package me.lpbigfish.enchantLevelUnlimited.anvil

import me.lpbigfish.enchantLevelUnlimited.config.CostStrategy
import me.lpbigfish.enchantLevelUnlimited.config.Settings
import me.lpbigfish.enchantLevelUnlimited.util.ReflectedAnvil
import org.bukkit.Material
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Repairable

class AnvilHandler(private val settings: Settings) {
    private val combiner = EnchantCombiner(settings.limits, settings)

    data class HandleResult(val item: ItemStack?, val cost: Int)

    fun handle(anvil: AnvilInventory, vanillaResult: ItemStack?): HandleResult? {
        val target = anvil.getItem(0) ?: return null
        val sacrifice = anvil.getItem(1) ?: return null

        val combineResult = combiner.compute(target, sacrifice) ?: return null

        // ponytail: block vanilla result when sacrifice enchants were all tool-incompatible
        if (combineResult.applied.isEmpty()) {
            return if (combineResult.incompatible > 0) HandleResult(null, 0) else null
        }

        val hasVanillaResult = vanillaResult != null && vanillaResult.type != Material.AIR
        val baseItem = vanillaResult?.clone() ?: target.clone()
        val meta = baseItem.itemMeta ?: return null

        EnchantCombiner.clearEnchants(meta)
        for ((enchant, level) in combineResult.enchants) {
            EnchantCombiner.applyEnchant(meta, enchant, level)
        }

        if (settings.resetPenalty && meta is Repairable) {
            meta.repairCost = 0
        }

        if (!hasVanillaResult) {
            val renameText = anvil.renameText
            if (!renameText.isNullOrEmpty()) {
                meta.setDisplayName(renameText)
            }
        }

        baseItem.itemMeta = meta
        return HandleResult(baseItem, calculateCost(combineResult))
    }

    private fun calculateCost(result: EnchantCombiner.Result): Int =
        result.applied.sumOf { it.resultingLevel * CostTable.multiplier(it.key, result.sacrificeIsBook) }

    fun applyCost(anvil: AnvilInventory, cost: Int) {
        val minCost = cost.coerceAtLeast(1)
        when (settings.costStrategy) {
            CostStrategy.CLAMP -> {
                anvil.repairCost = minOf(minCost, 39)
            }
            CostStrategy.RAISE_CAP -> {
                if (ReflectedAnvil.canRaiseCap()) {
                    ReflectedAnvil.setMaximumRepairCost(anvil, Int.MAX_VALUE)
                    anvil.repairCost = minCost
                } else {
                    anvil.repairCost = minOf(minCost, 39)
                }
            }
        }
    }
}
