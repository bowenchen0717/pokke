package com.example.update

enum class UpdateStatus {
    IDLE,
    CHECKING,
    UP_TO_DATE,
    UPDATE_AVAILABLE,
    DOWNLOADING,
    READY_TO_INSTALL,
    DEV_ENVIRONMENT_CHECKED,
    ERROR
}

data class AppVersionInfo(
    val currentVersion: String = "v1.0.0",
    val buildNumber: String = "1",
    val packageName: String = "",
    val latestVersion: String = "v1.0.0",
    val releaseDate: String = "2026-08-05",
    val releaseNotes: List<String> = listOf(
        "整合 Google Play 官方 In-App Update 自動升級 API",
        "支援 Google Play 商店線上版本自動檢測與推送機制",
        "具備動態套件版本驗證與 Play 商店頁面快速跳轉功能",
        "優化 Room 本地帳務寫入效能與 CSV 報表匯出功能"
    ),
    val updateSizeMb: Double = 12.5
)
