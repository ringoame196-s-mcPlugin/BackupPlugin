package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.managers.BackupManager
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin

class Command(plugin: Plugin) : CommandExecutor {
    private val backupManager = BackupManager(plugin)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val subCommand = args[0]
        when(subCommand) {
            CommandConst.BACKUP_COMMAND -> backupCommand(sender)
            else -> {
                val message = "${ChatColor.RED}コマンド構文がありません"
                sender.sendMessage(message)
            }
        }
        return true
    }

    private fun backupCommand(sender: CommandSender) {
        val commentaryStatus = backupManager.backupFolder()

        if (commentaryStatus) {
            val message = "${ChatColor.GOLD}正常にバックアップされました"
            sender.sendMessage(message)
        } else {
            val message = "${ChatColor.RED}バックアップ中にエラーが起きました 詳細はコンソールをご覧ください"
            sender.sendMessage(message)
        }
    }
}
