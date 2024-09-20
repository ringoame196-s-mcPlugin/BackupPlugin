package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.commands.TabCompleter
import com.github.ringoame196_s_mcPlugin.events.AsyncPlayerChatEvent
import com.github.ringoame196_s_mcPlugin.managers.BackupManager
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        val plugin = this
        val config = plugin.config
        val isStart = config.getBoolean("AutoBackup")
        val backupManager = BackupManager(plugin)
        saveDefaultConfig() // configファイル生成
        backupManager.makeBackupFolder() // バックアップフォルダー作成

        if (isStart) { // 自動バックアップを開始する
            val message = "${ChatColor.GOLD}自動バックアップを開始します"
            backupManager.sendMessageToOP(message)
            backupManager.startAutoBackup()
        }

        // イベント
        server.pluginManager.registerEvents(AsyncPlayerChatEvent(), plugin)

        // コマンド関係
        val command = getCommand("backupmanager")
        command!!.setExecutor(Command(plugin))
        command.tabCompleter = TabCompleter()
    }
}
