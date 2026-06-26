package me.lpbigfish.enchantLevelUnlimited.enchant

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConflictGroupsTest {

    private val allTogglesOff: Map<String, Boolean> = mapOf(
        "allow-damage-conflicts" to false,
        "allow-protection-conflicts" to false,
        "allow-bow-conflicts" to false,
        "allow-boots-conflicts" to false,
        "allow-crossbow-conflicts" to false
    )

    private val allTogglesOn: Map<String, Boolean> = mapOf(
        "allow-damage-conflicts" to true,
        "allow-protection-conflicts" to true,
        "allow-bow-conflicts" to true,
        "allow-boots-conflicts" to true,
        "allow-crossbow-conflicts" to true
    )

    @Test
    fun `hard block - Fortune blocks Silk Touch`() {
        assertTrue(ConflictGroups.isBlocked("silk_touch", ["fortune"], allTogglesOn))
        assertTrue(ConflictGroups.isBlocked("fortune", ["silk_touch"], allTogglesOn))
    }

    @Test
    fun `hard block - Loyalty blocks Riptide`() {
        assertTrue(ConflictGroups.isBlocked("riptide", ["loyalty"], allTogglesOn))
        assertTrue(ConflictGroups.isBlocked("loyalty", ["riptide"], allTogglesOn))
    }

    @Test
    fun `hard block - Channeling blocks Riptide`() {
        assertTrue(ConflictGroups.isBlocked("riptide", ["channeling"], allTogglesOn))
        assertTrue(ConflictGroups.isBlocked("channeling", ["riptide"], allTogglesOn))
    }

    @Test
    fun `toggleable off - damage group blocks`() {
        assertTrue(ConflictGroups.isBlocked("smite", ["sharpness"], allTogglesOff))
        assertTrue(ConflictGroups.isBlocked("bane_of_arthropods", ["smite"], allTogglesOff))
        assertTrue(ConflictGroups.isBlocked("sharpness", ["bane_of_arthropods"], allTogglesOff))
    }

    @Test
    fun `toggleable off - protection group blocks`() {
        assertTrue(ConflictGroups.isBlocked("fire_protection", ["protection"], allTogglesOff))
        assertTrue(ConflictGroups.isBlocked("blast_protection", ["projectile_protection"], allTogglesOff))
    }

    @Test
    fun `toggleable off - bow group blocks`() {
        assertTrue(ConflictGroups.isBlocked("mending", ["infinity"], allTogglesOff))
        assertTrue(ConflictGroups.isBlocked("infinity", ["mending"], allTogglesOff))
    }

    @Test
    fun `toggleable off - boots group blocks`() {
        assertTrue(ConflictGroups.isBlocked("frost_walker", ["depth_strider"], allTogglesOff))
    }

    @Test
    fun `toggleable off - crossbow group blocks`() {
        assertTrue(ConflictGroups.isBlocked("piercing", ["multishot"], allTogglesOff))
    }

    @Test
    fun `toggleable on - damage group allows`() {
        assertFalse(ConflictGroups.isBlocked("smite", ["sharpness"], allTogglesOn))
        assertFalse(ConflictGroups.isBlocked("sharpness", ["bane_of_arthropods"], allTogglesOn))
    }

    @Test
    fun `toggleable on - all groups allow`() {
        assertFalse(ConflictGroups.isBlocked("fire_protection", ["protection"], allTogglesOn))
        assertFalse(ConflictGroups.isBlocked("mending", ["infinity"], allTogglesOn))
        assertFalse(ConflictGroups.isBlocked("frost_walker", ["depth_strider"], allTogglesOn))
        assertFalse(ConflictGroups.isBlocked("piercing", ["multishot"], allTogglesOn))
    }

    @Test
    fun `no conflict when key not in any group`() {
        assertFalse(ConflictGroups.isBlocked("unbreaking", ["sharpness"], allTogglesOff))
        assertFalse(ConflictGroups.isBlocked("efficiency", ["fortune"], allTogglesOff))
    }

    @Test
    fun `same key does not block itself in toggleable group`() {
        assertFalse(ConflictGroups.isBlocked("sharpness", ["sharpness"], allTogglesOff))
        assertFalse(ConflictGroups.isBlocked("protection", ["protection"], allTogglesOff))
    }

    @Test
    fun `empty existing keys never blocks`() {
        assertFalse(ConflictGroups.isBlocked("sharpness", [], allTogglesOff))
        assertFalse(ConflictGroups.isBlocked("fortune", [], allTogglesOff))
    }

    @Test
    fun `missing toggle defaults to blocked for toggleable groups`() {
        val emptyToggles: Map<String, Boolean> = emptyMap()
        assertTrue(ConflictGroups.isBlocked("smite", ["sharpness"], emptyToggles))
    }

    @Test
    fun `hard block Fortune Silk Touch survives all toggles on`() {
        assertTrue(ConflictGroups.isBlocked("silk_touch", ["fortune"], allTogglesOn))
        assertTrue(ConflictGroups.isBlocked("fortune", ["silk_touch"], allTogglesOn))
    }

    @Test
    fun `cross-group damage key vs protection existing does not block`() {
        assertFalse(ConflictGroups.isBlocked("smite", ["protection"], allTogglesOff))
        assertFalse(ConflictGroups.isBlocked("sharpness", ["fire_protection"], allTogglesOff))
    }
}
