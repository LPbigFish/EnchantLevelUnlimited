package me.lpbigfish.enchantLevelUnlimited.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CostStrategyTest {

    @Test
    fun `raise-cap string maps to RAISE_CAP`() {
        assertEquals(CostStrategy.RAISE_CAP, CostStrategy.fromString("raise-cap"))
    }

    @Test
    fun `raise-cap is case-insensitive`() {
        assertEquals(CostStrategy.RAISE_CAP, CostStrategy.fromString("RAISE-CAP"))
        assertEquals(CostStrategy.RAISE_CAP, CostStrategy.fromString("Raise-Cap"))
    }

    @Test
    fun `clamp string maps to CLAMP`() {
        assertEquals(CostStrategy.CLAMP, CostStrategy.fromString("clamp"))
    }

    @Test
    fun `null defaults to CLAMP`() {
        assertEquals(CostStrategy.CLAMP, CostStrategy.fromString(null))
    }

    @Test
    fun `unknown value defaults to CLAMP`() {
        assertEquals(CostStrategy.CLAMP, CostStrategy.fromString("garbage"))
        assertEquals(CostStrategy.CLAMP, CostStrategy.fromString(""))
    }
}
