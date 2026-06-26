package me.lpbigfish.enchantLevelUnlimited

import me.lpbigfish.enchantLevelUnlimited.anvil.AnvilHandler
import me.lpbigfish.enchantLevelUnlimited.config.CostStrategy
import me.lpbigfish.enchantLevelUnlimited.config.Settings
import me.lpbigfish.enchantLevelUnlimited.util.ServerVersion
import org.bstats.bukkit.Metrics
import org.bstats.charts.SimplePie
import org.bstats.charts.SingleLineChart
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.plugin.java.JavaPlugin

class EnchantLevelUnlimited : JavaPlugin(), Listener {

    private var settings: Settings? = null
    private var handler: AnvilHandler? = null

    override fun onEnable() {
        saveDefaultConfig()
        server.pluginManager.registerEvents(this, this)
        loadState()
        registerCommand()
        setupMetrics()

        logger.info("EnchantLevelUnlimited v${description.version} enabled")
        logger.info("Server: ${ServerVersion.current()}, strategy: ${settings?.costStrategy}," +
            " enchants: ${settings?.limits?.configuredCount}")
    }

    override fun onDisable() {
        logger.info("EnchantLevelUnlimited disabled")
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPrepareAnvil(event: PrepareAnvilEvent) {
        val anvil = event.inventory
        val result = handler?.handle(anvil, event.result) ?: return
        if (result.item == null) {
            event.result = null
            return
        }
        event.result = result.item
        handler?.applyCost(anvil, result.cost)
    }

    private fun loadState() {
        settings = Settings(this)
        handler = AnvilHandler(settings!!)
    }

    private fun registerCommand() {
        getCommand("enchantlevelunlimited")?.also { cmd ->
            cmd.setExecutor { sender, _, _, args ->
                when (args.firstOrNull()?.lowercase()) {
                    "reload" -> {
                        if (!sender.hasPermission("enchantlevelunlimited.reload")) {
                            sender.sendMessage("§cYou don't have permission to reload the configuration")
                            return@setExecutor true
                        }
                        reloadConfig()
                        loadState()
                        sender.sendMessage("§aEnchantLevelUnlimited configuration reloaded")
                        logger.info("Configuration reloaded by ${sender.name}")
                    }
                    "info", "help" -> {
                        if (!sender.hasPermission("enchantlevelunlimited.info")) {
                            sender.sendMessage("§cYou don't have permission")
                            return@setExecutor true
                        }
                        sendInfo(sender)
                    }
                    else -> {
                        sender.sendMessage("§eEnchantLevelUnlimited §7v${description.version}")
                        sender.sendMessage("§e/elu reload §7- Reload configuration")
                        sender.sendMessage("§e/elu info §7- Plugin information")
                    }
                }
                true
            }
            cmd.setTabCompleter { _, _, _, args ->
                if (args.size == 1) {
                    listOf("reload", "info").filter { it.startsWith(args[0], ignoreCase = true) }
                } else {
                    emptyList()
                }
            }
        }
    }

    private fun sendInfo(sender: CommandSender) {
        sender.sendMessage("§eEnchantLevelUnlimited §7v${description.version}")
        sender.sendMessage("§7Server: §f${ServerVersion.current()}")
        sender.sendMessage("§7Cost strategy: §f${settings?.costStrategy ?: "N/A"}")
        sender.sendMessage("§7Reset penalty: §f${if (settings?.resetPenalty == true) "§aYes" else "§cNo"}")
        sender.sendMessage("§7Configured enchants: §f${settings?.limits?.configuredCount ?: 0}")
        sender.sendMessage("§7Anvil handler: ${if (handler != null) "§aActive" else "§cInactive"}")
    }

    private fun setupMetrics() {
        val pluginId = 32187
        Metrics(this, pluginId).also { metrics ->
            metrics.addCustomChart(SimplePie("cost_strategy") {
                settings?.costStrategy?.name?.lowercase() ?: "unknown"
            })
            metrics.addCustomChart(SimplePie("toggle_damage") {
                (settings?.toggles?.get("allow-damage-conflicts") == true).toString()
            })
            metrics.addCustomChart(SimplePie("toggle_protection") {
                (settings?.toggles?.get("allow-protection-conflicts") == true).toString()
            })
            metrics.addCustomChart(SimplePie("toggle_bow") {
                (settings?.toggles?.get("allow-bow-conflicts") == true).toString()
            })
            metrics.addCustomChart(SimplePie("toggle_boots") {
                (settings?.toggles?.get("allow-boots-conflicts") == true).toString()
            })
            metrics.addCustomChart(SimplePie("toggle_crossbow") {
                (settings?.toggles?.get("allow-crossbow-conflicts") == true).toString()
            })
            metrics.addCustomChart(SingleLineChart("configured_enchants") {
                settings?.limits?.configuredCount ?: 0
            })
        }
    }
}
