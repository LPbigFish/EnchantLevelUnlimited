package me.lpbigfish.enchantLevelUnlimited.anvil

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CostTableTest {

    @Test
    fun `item multipliers for known enchants`() {
        assertEquals(1, CostTable.multiplier("sharpness", false))
        assertEquals(1, CostTable.multiplier("efficiency", false))
        assertEquals(1, CostTable.multiplier("power", false))
        assertEquals(8, CostTable.multiplier("thorns", false))
        assertEquals(8, CostTable.multiplier("silk_touch", false))
        assertEquals(4, CostTable.multiplier("blast_protection", false))
        assertEquals(2, CostTable.multiplier("fire_protection", false))
    }

    @Test
    fun `book multipliers for known enchants`() {
        assertEquals(1, CostTable.multiplier("sharpness", true))
        assertEquals(4, CostTable.multiplier("thorns", true))
        assertEquals(4, CostTable.multiplier("silk_touch", true))
        assertEquals(2, CostTable.multiplier("blast_protection", true))
        assertEquals(1, CostTable.multiplier("fire_protection", true))
    }

    @Test
    fun `unknown enchant defaults to multiplier 1`() {
        assertEquals(1, CostTable.multiplier("nonexistent_enchant", false))
        assertEquals(1, CostTable.multiplier("nonexistent_enchant", true))
    }

    @Test
    fun `book multiplier is at most equal to item multiplier`() {
        val testKeys = ["sharpness", "thorns", "silk_touch", "protection",
            "blast_protection", "fire_aspect", "mending", "infinity",
            "soul_speed", "swift_sneak", "density", "breach"]

        for (key in testKeys) {
            val item = CostTable.multiplier(key, false)
            val book = CostTable.multiplier(key, true)
            assertTrue(book <= item, "Book multiplier ($book) should be <= item ($item) for $key")
        }
    }
}
