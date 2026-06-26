package me.lpbigfish.enchantLevelUnlimited.enchant

import org.bukkit.enchantments.Enchantment
import java.lang.reflect.Method

object EnchantResolver {
    private val reverseLegacy: Map<String, String> by lazy {
        LegacyNames.toLegacy.entries.associate { (vanilla, legacy) -> legacy to vanilla }
    }

    // ponytail: Enchantment.getKey() not in 1.12.2 API, reflect at runtime for 1.21+ compat
    private val getKeyMethod: Method? by lazy {
        try { Enchantment::class.java.getMethod("getKey") } catch (_: NoSuchMethodException) { null }
    }
    private val nskGetKeyMethod: Method? by lazy {
        try { Class.forName("org.bukkit.NamespacedKey").getMethod("getKey") } catch (_: Exception) { null }
    }

    fun resolve(key: String): Enchantment? {
        return Enchantment.getByName(key) ?: Enchantment.getByName(LegacyNames.legacyName(key))
    }

    fun keyOf(enchantment: Enchantment): String {
        // 1. Try legacy name → vanilla key mapping (1.8-1.20.x)
        val name = try { enchantment.name } catch (_: Exception) { null }
        if (name != null) {
            reverseLegacy[name]?.let { return it }
        }

        // 2. Try NamespacedKey via reflection (Paper 1.21+ returns vanilla keys)
        try {
            val nsk = getKeyMethod?.invoke(enchantment)
            val key = nsk?.let { nskGetKeyMethod?.invoke(it) as? String }
            if (key != null) return key.lowercase()
        } catch (_: Exception) {}

        // 3. Fallback: lowercase the name
        return name?.lowercase() ?: "unknown"
    }
}
