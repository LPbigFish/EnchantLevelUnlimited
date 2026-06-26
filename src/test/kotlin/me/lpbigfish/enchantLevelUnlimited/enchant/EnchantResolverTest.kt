package me.lpbigfish.enchantLevelUnlimited.enchant

import org.bukkit.enchantments.Enchantment
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class EnchantResolverTest {

    @Test
    fun `keyOf resolves legacy Bukkit name to vanilla key`() {
        val enchant = mockWithName("DAMAGE_ALL")
        assertEquals("sharpness", EnchantResolver.keyOf(enchant))
    }

    @Test
    fun `keyOf resolves another legacy name`() {
        val enchant = mockWithName("PROTECTION_ENVIRONMENTAL")
        assertEquals("protection", EnchantResolver.keyOf(enchant))
    }

    // ponytail: NamespacedKey reflection path (step 2) only active at runtime on 1.21+,
    // not testable in unit tests against 1.12.2 API. Covered by in-game testing.
    // When getName returns vanilla key directly (e.g. "MENDING"), fallback lowercases it.
    @Test
    fun `keyOf lowercases non-legacy name as fallback`() {
        val enchant = mockWithName("MENDING")
        assertEquals("mending", EnchantResolver.keyOf(enchant))
    }

    @Test
    fun `keyOf lowercases unknown enchant names`() {
        val enchant = mockWithName("CUSTOM_POWER")
        assertEquals("custom_power", EnchantResolver.keyOf(enchant))
    }

    @Test
    fun `keyOf returns unknown when getName throws`() {
        val enchant = Mockito.mock(Enchantment::class.java)
        Mockito.`when`(enchant.name).thenThrow(RuntimeException("removed"))
        assertEquals("unknown", EnchantResolver.keyOf(enchant))
    }

    private fun mockWithName(name: String): Enchantment {
        val enchant = Mockito.mock(Enchantment::class.java)
        Mockito.`when`(enchant.name).thenReturn(name)
        return enchant
    }
}
