package com.github.ringoame196_s_mcPlugin.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter : TabCompleter {
    override fun onTabComplete(commandSender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> mutableListOf(CommandConst.BACKUP_COMMAND, CommandConst.RELOAD_CONFIG_COMMAND, CommandConst.LIST_COMMAND, CommandConst.DELETE_ALL_COMMAND)
            else -> mutableListOf()
        }
    }
}
