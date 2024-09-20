package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.Data
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class AsyncPlayerChatEvent : Listener {
    @EventHandler
    fun onAsyncPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val message = e.message
        val playerName = player.name
        Data.chatLog.add("$playerName $message") // チャットを保存する
    }
}
