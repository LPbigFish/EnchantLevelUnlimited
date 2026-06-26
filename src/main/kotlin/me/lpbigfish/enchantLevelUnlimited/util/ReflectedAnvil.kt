package me.lpbigfish.enchantLevelUnlimited.util

import org.bukkit.inventory.AnvilInventory
import java.lang.reflect.Method

object ReflectedAnvil {
    private val setMaximumRepairCostMethod: Method? by lazy {
        try {
            AnvilInventory::class.java.getMethod("setMaximumRepairCost", Int::class.javaPrimitiveType)
        } catch (e: NoSuchMethodException) { null }
    }

    fun canRaiseCap(): Boolean = setMaximumRepairCostMethod != null

    fun setMaximumRepairCost(anvil: AnvilInventory, cost: Int) {
        setMaximumRepairCostMethod?.invoke(anvil, cost)
    }
}
