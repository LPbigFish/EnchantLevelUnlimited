package me.lpbigfish.enchantLevelUnlimited.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ServerVersionTest {

    @Test
    fun `compareTo by major version`() {
        assertTrue(ServerVersion(1, 21) > ServerVersion(1, 13))
        assertTrue(ServerVersion(2, 0) > ServerVersion(1, 99))
        assertTrue(ServerVersion(1, 8) < ServerVersion(1, 13))
    }

    @Test
    fun `compareTo by minor version when major is equal`() {
        assertTrue(ServerVersion(1, 21) > ServerVersion(1, 20))
        assertTrue(ServerVersion(1, 9) < ServerVersion(1, 13))
        assertEquals(0, ServerVersion(1, 21).compareTo(ServerVersion(1, 21)))
    }

    @Test
    fun `isAtLeast returns true when version meets threshold`() {
        val v = ServerVersion(1, 21)
        assertTrue(v >= ServerVersion(1, 13))
        assertTrue(v >= ServerVersion(1, 21))
    }

    @Test
    fun `isAtLeast returns false when version below threshold`() {
        val v = ServerVersion(1, 8)
        assertFalse(v >= ServerVersion(1, 13))
    }

    @Test
    fun `pair overload comparison works`() {
        assertTrue(ServerVersion(1, 21).compareTo(1 to 13) > 0)
        assertTrue(ServerVersion(1, 8).compareTo(1 to 13) < 0)
        assertEquals(0, ServerVersion(1, 21).compareTo(1 to 21))
    }
}
