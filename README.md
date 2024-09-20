# BackupPlugin

## プラグイン説明
ワールドフォルダーをバックアップ <br>
バックアップしたときをわかりやすくするために、サーバー人数と直近チャット(5件)をserverinfo.txtに保存

![image](https://github.com/Ringoame196/VideoStorage/blob/main/images/backupmanager1.png)
![image](https://github.com/Ringoame196/VideoStorage/blob/main/images/backupmanager2.png)

## コマンド
 - /backupmanager backup - 手動でバックアップ
 - /backupmanager deleteall - バックアップを全削除する
 - /backupmanager list - バックアップリストを一覧で出す
 - /backupmanager reloadconfig - configを再読み込み

## 使い方
 - 手動でバックアップする場合は/backupmanager backup
 - 自動でバックアップする場合は configのAutoBackupをtrue
 - バックアップしたファイルは /plugin/backupmanager/backup/(日にち-時間)/(ワールド名)

## configファイル
 - AutoBackup : false - 自動でバックアップするか
 - BackupInterval : 30 - 自動バックアップをする間隔(秒)
 - BackupFolderNames: - バックアップしたいフォルダーの名前一覧

## 開発環境
- Minecraft Version : 1.20.1
- Kotlin Version : 1.8.0

## プロジェクト情報
- プロジェクトパス : ringoame196-s-mcPlugin/BackupPlugin.git
- 開発者名 : ringoame196_s_mcPlugin
- 開発開始日 : 2024-09-20
