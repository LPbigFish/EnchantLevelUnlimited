package me.lpbigfish.enchantLevelUnlimited.anvil

import me.lpbigfish.enchantLevelUnlimited.config.Settings
import me.lpbigfish.enchantLevelUnlimited.enchant.EnchantLimits
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class EnchantCombinerTest {

    @Test
    fun `two same-level books at config cap stay at cap and produce result`() {
        val sharpness = mockSharpness()

        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val settings = Mockito.mock(Settings::class.java, Mockito.withSettings().stubOnly())
        Mockito.`when`(settings.toggles).thenReturn(emptyMap())
        Mockito.`when`(settings.allowIncompatibleTool).thenReturn(true)

        val combiner = EnchantCombiner(limits, settings)

        val target = mockBook(mapOf(sharpness to 10))
        val sacrifice = mockBook(mapOf(sharpness to 10))

        val result = combiner.compute(target, sacrifice)

        assertNotNull(result)
        result!!
        assertEquals(10, result.enchants[sharpness])
        assertFalse(result.applied.isEmpty())
        assertEquals(10, result.applied.first().resultingLevel)
    }

    @Test
    fun `same-level books below cap increment normally`() {
        val sharpness = mockSharpness()

        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val settings = Mockito.mock(Settings::class.java, Mockito.withSettings().stubOnly())
        Mockito.`when`(settings.toggles).thenReturn(emptyMap())
        Mockito.`when`(settings.allowIncompatibleTool).thenReturn(true)

        val combiner = EnchantCombiner(limits, settings)

        val target = mockBook(mapOf(sharpness to 5))
        val sacrifice = mockBook(mapOf(sharpness to 5))

        val result = combiner.compute(target, sacrifice)

        assertNotNull(result)
        result!!
        assertEquals(6, result.enchants[sharpness])
    }

    @Test
    fun `higher sacrifice level wins up to cap`() {
        val sharpness = mockSharpness()

        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val settings = Mockito.mock(Settings::class.java, Mockito.withSettings().stubOnly())
        Mockito.`when`(settings.toggles).thenReturn(emptyMap())
        Mockito.`when`(settings.allowIncompatibleTool).thenReturn(true)

        val combiner = EnchantCombiner(limits, settings)

        val target = mockBook(mapOf(sharpness to 7))
        val sacrifice = mockBook(mapOf(sharpness to 9))

        val result = combiner.compute(target, sacrifice)

        assertNotNull(result)
        result!!
        assertEquals(9, result.enchants[sharpness])
    }

    @Test
    fun `lower sacrifice level does not reduce target`() {
        val sharpness = mockSharpness()

        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val settings = Mockito.mock(Settings::class.java, Mockito.withSettings().stubOnly())
        Mockito.`when`(settings.toggles).thenReturn(emptyMap())
        Mockito.`when`(settings.allowIncompatibleTool).thenReturn(true)

        val combiner = EnchantCombiner(limits, settings)

        val target = mockBook(mapOf(sharpness to 8))
        val sacrifice = mockBook(mapOf(sharpness to 3))

        val result = combiner.compute(target, sacrifice)

        assertNotNull(result)
        result!!
        assertEquals(8, result.enchants[sharpness])
    }

    @Test
    fun `config max 1 keeps level 1 on combine`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val limits = EnchantLimits(mapOf("sharpness" to 1))
        val combiner = EnchantCombiner(limits, mockSettings())

        val result = combiner.compute(mockBook(mapOf(sharpness to 1)), mockBook(mapOf(sharpness to 1)))

        assertNotNull(result!!)
        assertEquals(1, result.enchants[sharpness])
        assertTrue(result.applied.isNotEmpty(), "hitCap should put it in applied")
    }

    @Test
    fun `config max below existing level never reduces target`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val limits = EnchantLimits(mapOf("sharpness" to 3))
        val combiner = EnchantCombiner(limits, mockSettings())

        val result = combiner.compute(mockBook(mapOf(sharpness to 5)), mockBook(mapOf(sharpness to 5)))

        assertNotNull(result!!)
        assertEquals(5, result.enchants[sharpness])
    }

    @Test
    fun `target at cap with sacrifice same level stays and produces applied`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val combiner = EnchantCombiner(limits, mockSettings())

        val result = combiner.compute(mockBook(mapOf(sharpness to 10)), mockBook(mapOf(sharpness to 10)))

        assertNotNull(result!!)
        assertEquals(10, result.enchants[sharpness])
        assertTrue(result.applied.isNotEmpty(), "hitCap=true should be in applied")
    }

    @Test
    fun `sacrifice adds new enchant target lacks`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val unbreaking = mockEnchant("DURABILITY", 3)
        val limits = EnchantLimits(mapOf("sharpness" to 10, "unbreaking" to 10))
        val combiner = EnchantCombiner(limits, mockSettings())

        val result = combiner.compute(mockBook(mapOf(sharpness to 5)), mockBook(mapOf(unbreaking to 3)))

        assertNotNull(result!!)
        assertEquals(5, result.enchants[sharpness])
        assertEquals(3, result.enchants[unbreaking])
    }

    @Test
    fun `multi-enchant sacrifice - one conflicts one does not`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val smite = mockEnchant("DAMAGE_UNDEAD", 5)
        val limits = EnchantLimits(mapOf("sharpness" to 10, "smite" to 10))
        val toggles = mapOf("allow-damage-conflicts" to false)
        val combiner = EnchantCombiner(limits, mockSettings(toggles))

        val target = mockBook(mapOf(sharpness to 5))
        val sacrifice = mockBook(mapOf(sharpness to 5, smite to 5))

        val result = combiner.compute(target, sacrifice)

        assertNotNull(result!!)
        assertEquals(6, result.enchants[sharpness])
        assertFalse(result.enchants.containsKey(smite), "Smite should be blocked by conflict")
    }

    @Test
    fun `tool-incompatible enchant on non-book target filtered`() {
        val silkTouch = mockEnchant("SILK_TOUCH", 1)
        Mockito.`when`(silkTouch.canEnchantItem(Mockito.any(ItemStack::class.java))).thenReturn(false)
        val limits = EnchantLimits(mapOf("silk_touch" to 1))
        val combiner = EnchantCombiner(limits, mockSettings(allowIncompatibleTool = false))

        val result = combiner.compute(mockItem(emptyMap()), mockItem(mapOf(silkTouch to 1)))

        assertNotNull(result!!)
        assertTrue(result.applied.isEmpty())
        assertEquals(1, result.incompatible)
    }

    @Test
    fun `book target bypasses tool compatibility check`() {
        val silkTouch = mockEnchant("SILK_TOUCH", 1)
        Mockito.`when`(silkTouch.canEnchantItem(Mockito.any(ItemStack::class.java))).thenReturn(false)
        val limits = EnchantLimits(mapOf("silk_touch" to 1))
        val combiner = EnchantCombiner(limits, mockSettings(allowIncompatibleTool = false))

        val result = combiner.compute(mockBook(emptyMap()), mockBook(mapOf(silkTouch to 1)))

        assertNotNull(result!!)
        assertTrue(result.applied.isNotEmpty(), "Books should bypass tool check")
    }

    @Test
    fun `mixed compatible and incompatible sacrifice enchants`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val silkTouch = mockEnchant("SILK_TOUCH", 1)
        Mockito.`when`(sharpness.canEnchantItem(Mockito.any(ItemStack::class.java))).thenReturn(true)
        Mockito.`when`(silkTouch.canEnchantItem(Mockito.any(ItemStack::class.java))).thenReturn(false)
        val limits = EnchantLimits(mapOf("sharpness" to 10, "silk_touch" to 1))
        val combiner = EnchantCombiner(limits, mockSettings(allowIncompatibleTool = false))

        val result = combiner.compute(mockItem(emptyMap()), mockItem(mapOf(sharpness to 5, silkTouch to 1)))

        assertNotNull(result!!)
        assertEquals(5, result.enchants[sharpness])
        assertFalse(result.enchants.containsKey(silkTouch), "Silk touch should be filtered")
        assertEquals(1, result.incompatible)
    }

    @Test
    fun `canEnchantItem throws - caught and enchant allowed`() {
        val silkTouch = mockEnchant("SILK_TOUCH", 1)
        Mockito.`when`(silkTouch.canEnchantItem(Mockito.any(ItemStack::class.java)))
            .thenThrow(RuntimeException("Paper 1.21+ API removed"))
        val limits = EnchantLimits(mapOf("silk_touch" to 1))
        val combiner = EnchantCombiner(limits, mockSettings(allowIncompatibleTool = false))

        val result = combiner.compute(mockItem(emptyMap()), mockItem(mapOf(silkTouch to 1)))

        assertNotNull(result!!)
        assertTrue(result.applied.isNotEmpty(), "Exception should be caught, enchant allowed")
        assertEquals(0, result.incompatible)
    }

    @Test
    fun `sacrifice with no enchants returns null`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val combiner = EnchantCombiner(limits, mockSettings())

        val result = combiner.compute(mockBook(mapOf(sharpness to 5)), mockBook(emptyMap()))

        assertNull(result)
    }

    @Test
    fun `target with null itemMeta still processes sacrifice enchants`() {
        val sharpness = mockEnchant("DAMAGE_ALL", 5)
        val limits = EnchantLimits(mapOf("sharpness" to 10))
        val combiner = EnchantCombiner(limits, mockSettings())

        val target = Mockito.mock(ItemStack::class.java)
        Mockito.`when`(target.itemMeta).thenReturn(null)

        val result = combiner.compute(target, mockBook(mapOf(sharpness to 5)))

        assertNotNull(result!!)
        assertEquals(5, result.enchants[sharpness])
    }

    private fun mockSettings(
        toggles: Map<String, Boolean> = emptyMap(),
        allowIncompatibleTool: Boolean = true
    ): Settings {
        val settings = Mockito.mock(Settings::class.java, Mockito.withSettings().stubOnly())
        Mockito.`when`(settings.toggles).thenReturn(toggles)
        Mockito.`when`(settings.allowIncompatibleTool).thenReturn(allowIncompatibleTool)
        return settings
    }

    private fun mockEnchant(name: String, maxLevel: Int): Enchantment {
        val enchant = Mockito.mock(Enchantment::class.java)
        Mockito.`when`(enchant.name).thenReturn(name)
        Mockito.`when`(enchant.maxLevel).thenReturn(maxLevel)
        return enchant
    }

    private fun mockSharpness(): Enchantment = mockEnchant("DAMAGE_ALL", 5)

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
