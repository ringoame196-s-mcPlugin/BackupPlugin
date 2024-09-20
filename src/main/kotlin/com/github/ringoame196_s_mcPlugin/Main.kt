package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.commands.TabCompleter
import com.github.ringoame196_s_mcPlugin.events.Events
import com.github.ringoame196_s_mcPlugin.managers.BackupManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        val plugin = this
        val backupManager = BackupManager(plugin)
        saveDefaultConfig() // configファイル生成
        backupManager.makeBackupFolder() // バックアップフォルダー作成
        server.pluginManager.registerEvents(Events(), plugin)

        // コマンド関係
        val command = getCommand("backupmanager")
        command!!.setExecutor(Command(plugin))
        command.tabCompleter = TabCompleter()
    }
}
