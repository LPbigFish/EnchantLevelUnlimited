package me.lpbigfish.enchantLevelUnlimited.enchant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class EnchantLimitsTest {

    private fun mockEnchantment(maxLevel: Int) = Mockito.mock(
        org.bukkit.enchantments.Enchantment::class.java,
        Mockito.withSettings().stubOnly()
    ).also {
        Mockito.`when`(it.maxLevel).thenReturn(maxLevel)
    }

    @Test
    fun `config value overrides vanilla max`() {
        val limits = EnchantLimits(mapOf("sharpness" to 10))
        assertEquals(10, limits.maxFor("sharpness", mockEnchantment(5)))
    }

    @Test
    fun `falls back to vanilla maxLevel when not in config`() {
        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val enchant = mockEnchantment(3)
        assertEquals(3, limits.maxFor("unbreaking", enchant))
    }

    @Test
    fun `empty config map always falls back to vanilla`() {
        val limits = EnchantLimits(emptyMap())
        assertEquals(5, limits.maxFor("sharpness", mockEnchantment(5)))
        assertEquals(1, limits.maxFor("mending", mockEnchantment(1)))
    }

    @Test
    fun `config value above 255 clamped to hard cap`() {
        val limits = EnchantLimits(mapOf("sharpness" to 40000))
        assertEquals(255, limits.maxFor("sharpness", mockEnchantment(5)))
    }

    @Test
    fun `config value exactly 255 not clamped`() {
        val limits = EnchantLimits(mapOf("sharpness" to 255))
        assertEquals(255, limits.maxFor("sharpness", mockEnchantment(5)))
    }
}
