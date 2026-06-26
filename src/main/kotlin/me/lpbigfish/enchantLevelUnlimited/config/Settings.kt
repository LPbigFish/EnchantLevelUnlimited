package me.lpbigfish.enchantLevelUnlimited.config

import me.lpbigfish.enchantLevelUnlimited.EnchantLevelUnlimited
import me.lpbigfish.enchantLevelUnlimited.anvil.CostTable
import me.lpbigfish.enchantLevelUnlimited.enchant.EnchantLimits
import org.bukkit.configuration.file.FileConfiguration

enum class CostStrategy {
    CLAMP, RAISE_CAP;

    companion object {
        fun fromString(value: String?): CostStrategy =
            if (value.equals("raise-cap", ignoreCase = true)) RAISE_CAP else CLAMP
    }
}

class Settings(plugin: EnchantLevelUnlimited) {
    val costStrategy: CostStrategy
    val resetPenalty: Boolean
    val allowIncompatibleTool: Boolean
    val toggles: Map<String, Boolean>
    val limits: EnchantLimits

    init {
        plugin.reloadConfig()
        val config: FileConfiguration = plugin.config
        val section = config.getConfigurationSection("settings")
        val logger = plugin.logger

        costStrategy = CostStrategy.fromString(section?.getString("cost-strategy"))
        resetPenalty = section?.getBoolean("reset-penalty", true) ?: true
        allowIncompatibleTool = section?.getBoolean("allow-incompatible-tool", false) ?: false

        toggles = mapOf(
            "allow-damage-conflicts" to (section?.getBoolean("allow-damage-conflicts", false) ?: false),
            "allow-protection-conflicts" to (section?.getBoolean("allow-protection-conflicts", false) ?: false),
            "allow-bow-conflicts" to (section?.getBoolean("allow-bow-conflicts", false) ?: false),
            "allow-boots-conflicts" to (section?.getBoolean("allow-boots-conflicts", false) ?: false),
            "allow-crossbow-conflicts" to (section?.getBoolean("allow-crossbow-conflicts", false) ?: false)
        )

        val knownEnchants = CostTable.knownKeys
        val enchantMap = mutableMapOf<String, Int>()
        val enchantsSection = config.getConfigurationSection("enchants")

        enchantsSection?.getKeys(false)?.forEach { key ->
            if (key !in knownEnchants) {
                logger.warning("Unknown enchant key '$key' in config.yml enchants section")
            }
            val value = enchantsSection.getInt(key)
            if (value < 1) {
                logger.warning("Enchant '$key' has value $value, clamping to 1")
                enchantMap[key] = 1
            } else {
                enchantMap[key] = value
            }
        }

        limits = EnchantLimits(enchantMap)
    }
}
