package com.github.ringoame196_s_mcPlugin.managers

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BackupManager(private val plugin: Plugin) {
    private val backupFolderPath = "${plugin.dataFolder}/backups"
    private val worldFolder = Bukkit.getWorldContainer()
    private val config = plugin.config

    fun makeBackupFolder() {
        if (!File(backupFolderPath).exists()) { // フォルダーが存在していなかったら
            Files.createDirectory(Paths.get(backupFolderPath))
        }
    }

    fun backupFolder(): Boolean {
        var normallyExecution = true // 正常に実行できたか
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm-ss")
        val timeFolderName = currentDateTime.format(formatter)
        val folderNames = config.getList("BackupFolderNames")?.toMutableList() ?: mutableListOf()

        try {
            makeTimeFolder(timeFolderName) // 時間フォルダを作成
            makeServerInfo(timeFolderName)
            for (folderName in folderNames) {
                copyFolder(timeFolderName, folderName.toString())
            }
        } catch (e: Exception) {
            val errorMessage = e.message
            println(errorMessage) // エラーメッセージを送信
            normallyExecution = false
        }
        return normallyExecution
    }

    fun startAutoBackup() {
        val interval = (config.getInt("BackupInterval") * 20).toLong()

        object : BukkitRunnable() {
            override fun run() {
                val commentaryStatus = backupFolder()

                if (commentaryStatus) {
                    val message = "${ChatColor.AQUA}自動バックアップ完了"
                    sendMessageToOP(message)
                } else {
                    val message = "${ChatColor.RED}自動バックアップ中にエラーが起きました 詳細はコンソール"
                    sendMessageToOP(message)
                }
            }
        }.runTaskTimer(plugin, 0L, interval) // 1秒間隔 (20 ticks) でタスクを実行
    }

    fun sendMessageToOP(message: String) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isOp) {
                player.sendMessage(message)
            }
        }
    }

    private fun makeServerInfo(timeFolderName: String) {
        val size = Bukkit.getOnlinePlayers().size
        val lastChat = acquisitionLatestChat()
        val path = "$backupFolderPath/$timeFolderName/serverInfo.txt"
        val serverInfoFile = File(path)

        var serverInfoText = "サーバー人数:${size}人 \n直近チャット:\n"
        for (chat in lastChat) { // 直近チャットを記載
            serverInfoText += "$chat\n"
        }
        serverInfoFile.writeText(serverInfoText)
    }

    private fun acquisitionLatestChat(): MutableList<String> {
        val chatLog = Data.chatLog
        val lastChat = mutableListOf<String>()
        val lastSize = 5
        val chatSize = chatLog.size
        val startNumber = if (chatLog.size >= lastSize) {
            chatSize - lastSize
        } else {
            0
        }

        if (chatSize == 0) {
            return mutableListOf()
        }

        for (i in startNumber until chatSize) {
            lastChat.add(chatLog[i])
        }

        Data.chatLog.clear() // チャットログをリセット

        return lastChat
    }
}
