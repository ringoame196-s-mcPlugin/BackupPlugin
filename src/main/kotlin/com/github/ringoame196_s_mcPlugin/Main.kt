package com.github.ringoame196_s_mcPlugin

import org.bukkit.plugin.java.JavaPlugin
import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.commands.TabCompleter
import com.github.ringoame196_s_mcPlugin.events.Events

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        val plugin = this
        val backupManager = BackupManager(plugin)
        saveDefaultConfig() // configファイル生成
        backupManager.makeBackupFolder() // バックアップフォルダー作成
        server.pluginManager.registerEvents(Events(), plugin)
        // val command = getCommand("command")
        // command!!.setExecutor(Command())
        // command.tabCompleter = TabCompleter()
    }
}
