package com.github.ringoame196_s_mcPlugin.managers

import com.github.ringoame196_s_mcPlugin.Data
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

    private fun copyFolder(timeFolderName: String, folderName: String) {
        val copySourcePath = "$worldFolder/$folderName"
        val backupFolderPath = Paths.get("$backupFolderPath/$timeFolderName/$folderName")

        copyDir(Paths.get(copySourcePath), backupFolderPath)
    }

    private fun copyDir(source: Path, destination: Path) {
        // コピー先のフォルダが存在しない場合は作成
        if (Files.notExists(destination)) {
            Files.createDirectories(destination)
        }

        // ファイルとフォルダを再帰的にコピー
        Files.walkFileTree(
            source,
            object : SimpleFileVisitor<Path>() {
                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                    val targetPath = destination.resolve(source.relativize(dir))
                    if (Files.notExists(targetPath)) {
                        Files.createDirectory(targetPath)
                    }
                    return FileVisitResult.CONTINUE
                }

                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    // session.lock ファイルをスキップ
                    if (file.fileName.toString() == "session.lock") {
                        return FileVisitResult.CONTINUE
                    }
                    Files.copy(file, destination.resolve(source.relativize(file)), StandardCopyOption.REPLACE_EXISTING)
                    return FileVisitResult.CONTINUE
                }
            }
        )
    }

    private fun makeTimeFolder(timeFolderName: String) {
        val path = "$backupFolderPath/$timeFolderName"
        if (!File(path).exists()) { // ファイルが無ければ作成
            Files.createDirectory(Paths.get(path))
        }
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

    fun acquisitionBackupList(): MutableList<String> {
        val backupFolder = File(backupFolderPath)
        // フォルダーが存在し、ディレクトリであるかを確認
        return if (backupFolder.exists() && backupFolder.isDirectory) {
            // 直下のディレクトリのみをリストにして返す
            backupFolder.listFiles { file -> file.isDirectory }?.map { it.name }?.toMutableList() ?: mutableListOf()
        } else {
            // フォルダーが存在しない場合、空のリストを返す
            mutableListOf()
        }
    }

    fun deleteALLBackupFolder() {
        val backupList = acquisitionBackupList()

        for (folderName in backupList) {
            val path = "$backupFolderPath/$folderName"
            File(path).deleteRecursively() // 強制削除する
        }
    }
}
