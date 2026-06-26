package me.lpbigfish.enchantLevelUnlimited.enchant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LegacyNamesTest {

    @Test
    fun `maps vanilla keys to old Bukkit names`() {
        assertEquals("PROTECTION_ENVIRONMENTAL", LegacyNames.legacyName("protection"))
        assertEquals("PROTECTION_FIRE", LegacyNames.legacyName("fire_protection"))
        assertEquals("PROTECTION_FALL", LegacyNames.legacyName("feather_falling"))
        assertEquals("PROTECTION_EXPLOSIONS", LegacyNames.legacyName("blast_protection"))
        assertEquals("PROTECTION_PROJECTILE", LegacyNames.legacyName("projectile_protection"))
        assertEquals("OXYGEN", LegacyNames.legacyName("respiration"))
        assertEquals("WATER_WORKER", LegacyNames.legacyName("aqua_affinity"))
        assertEquals("DAMAGE_ALL", LegacyNames.legacyName("sharpness"))
        assertEquals("DAMAGE_UNDEAD", LegacyNames.legacyName("smite"))
        assertEquals("DAMAGE_ARTHROPODS", LegacyNames.legacyName("bane_of_arthropods"))
        assertEquals("LOOT_BONUS_MOBS", LegacyNames.legacyName("looting"))
        assertEquals("DIG_SPEED", LegacyNames.legacyName("efficiency"))
        assertEquals("DURABILITY", LegacyNames.legacyName("unbreaking"))
        assertEquals("LOOT_BONUS_BLOCKS", LegacyNames.legacyName("fortune"))
        assertEquals("ARROW_DAMAGE", LegacyNames.legacyName("power"))
        assertEquals("ARROW_KNOCKBACK", LegacyNames.legacyName("punch"))
        assertEquals("ARROW_FIRE", LegacyNames.legacyName("flame"))
        assertEquals("ARROW_INFINITE", LegacyNames.legacyName("infinity"))
        assertEquals("LUCK", LegacyNames.legacyName("luck_of_the_sea"))
    }

    @Test
    fun `unmapped key returns uppercase as fallback`() {
        assertEquals("MENDING", LegacyNames.legacyName("mending"))
        assertEquals("FROST_WALKER", LegacyNames.legacyName("frost_walker"))
        assertEquals("CHANNELING", LegacyNames.legacyName("channeling"))
    }

    @Test
    fun `toLegacy map has expected size`() {
        assertEquals(19, LegacyNames.toLegacy.size)
    }
}
