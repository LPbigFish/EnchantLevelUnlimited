package me.lpbigfish.enchantLevelUnlimited.anvil

import me.lpbigfish.enchantLevelUnlimited.config.CostStrategy
import me.lpbigfish.enchantLevelUnlimited.config.Settings
import me.lpbigfish.enchantLevelUnlimited.enchant.EnchantLimits
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class AnvilHandlerTest {

    @Test
    fun `all incompatible enchants returns block signal`() {
        val silkTouch = mockEnchant("SILK_TOUCH", 1)
        Mockito.`when`(silkTouch.canEnchantItem(Mockito.any(ItemStack::class.java))).thenReturn(false)

        val limits = EnchantLimits(mapOf("silk_touch" to 1))
        val settings = mockSettings(allowIncompatibleTool = false, limits = limits)
        val handler = AnvilHandler(settings)

        val anvil = mockAnvil(mockItem(emptyMap()), mockItem(mapOf(silkTouch to 1)))
        val vanillaResult = Mockito.mock(ItemStack::class.java)
        Mockito.`when`(vanillaResult.type).thenReturn(Material.DIAMOND_SWORD)

        val result = handler.handle(anvil, vanillaResult)

        assertNotNull(result)
        assertNull(result!!.item, "Should signal block via null item")
    }

    @Test
    fun `all incompatible without vanilla result also blocks`() {
        val silkTouch = mockEnchant("SILK_TOUCH", 1)
        Mockito.`when`(silkTouch.canEnchantItem(Mockito.any(ItemStack::class.java))).thenReturn(false)

        val limits = EnchantLimits(mapOf("silk_touch" to 1))
        val settings = mockSettings(allowIncompatibleTool = false, limits = limits)
        val handler = AnvilHandler(settings)

        val anvil = mockAnvil(mockItem(emptyMap()), mockItem(mapOf(silkTouch to 1)))

        val result = handler.handle(anvil, null)

        assertNotNull(result, "Should still return block signal even without vanilla result")
        assertNull(result!!.item)
    }

    @Test
    fun `no enchant change returns null no-op`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val settings = mockSettings(limits = limits)
        val handler = AnvilHandler(settings)

        val target = mockBook(mapOf(sharpness to 5))
        val sacrifice = mockBook(mapOf(sharpness to 3))
        val anvil = mockAnvil(target, sacrifice)

        val result = handler.handle(anvil, null)

        assertNull(result, "Lower sacrifice level = no change = no-op")
    }

    // ponytail: processing chain (clone, itemMeta setter, clearEnchants) requires full mock setup
    // covered by EnchantCombiner unit tests + in-game testing, not worth the mock boilerplate here

    private fun mockSettings(
        toggles: Map<String, Boolean> = emptyMap(),
        allowIncompatibleTool: Boolean = true,
        limits: EnchantLimits = EnchantLimits(emptyMap())
    ): Settings {
        val settings = Mockito.mock(Settings::class.java, Mockito.withSettings().stubOnly())
        Mockito.`when`(settings.toggles).thenReturn(toggles)
        Mockito.`when`(settings.allowIncompatibleTool).thenReturn(allowIncompatibleTool)
        Mockito.`when`(settings.limits).thenReturn(limits)
        Mockito.`when`(settings.resetPenalty).thenReturn(false)
        Mockito.`when`(settings.costStrategy).thenReturn(CostStrategy.CLAMP)
        return settings
    }

    private fun mockEnchant(name: String, maxLevel: Int): Enchantment {
        val enchant = Mockito.mock(Enchantment::class.java)
        Mockito.`when`(enchant.name).thenReturn(name)
        Mockito.`when`(enchant.maxLevel).thenReturn(maxLevel)
        return enchant
    }

    private fun mockAnvil(target: ItemStack, sacrifice: ItemStack): AnvilInventory {
        val anvil = Mockito.mock(AnvilInventory::class.java)
        Mockito.`when`(anvil.getItem(0)).thenReturn(target)
        Mockito.`when`(anvil.getItem(1)).thenReturn(sacrifice)
        return anvil
    }

    private fun mockBook(enchants: Map<Enchantment, Int>): ItemStack {
        val item = Mockito.mock(ItemStack::class.java)
        val meta = Mockito.mock(EnchantmentStorageMeta::class.java)
        Mockito.`when`(item.itemMeta).thenReturn(meta)
        Mockito.`when`(meta.storedEnchants).thenReturn(enchants)
        return item
    }

    private fun mockItem(enchants: Map<Enchantment, Int>): ItemStack {
        val item = Mockito.mock(ItemStack::class.java)
        val meta = Mockito.mock(ItemMeta::class.java)
        Mockito.`when`(item.itemMeta).thenReturn(meta)
        Mockito.`when`(meta.enchants).thenReturn(enchants)
        return item
    }
}
