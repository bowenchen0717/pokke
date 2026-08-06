package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.ThemeMode

@Composable
fun ReportBackupScreen(
    themeMode: ThemeMode,
    onToggleThemeMode: () -> Unit,
    onExportCsv: (Context) -> Unit,
    onExportJson: (Context) -> Unit,
    onRestoreJson: (String) -> Unit,
    onLoadSampleData: () -> Unit,
    onClearAllData: () -> Unit,
    currentAppVersion: String = "v1.2.0",
    lastUpdateCheckTime: String = "今天 18:30",
    isAutoCheckUpdateEnabled: Boolean = true,
    onCheckForUpdates: () -> Unit = {},
    onToggleAutoCheckUpdate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("report_backup_screen")
    ) {
        val isWideScreen = maxWidth >= 640.dp
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Mode Toggle Option Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleThemeMode() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "主題模式",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "多彩深色 / 淺色主題",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (themeMode) {
                                ThemeMode.SYSTEM -> "跟隨系統設定"
                                ThemeMode.LIGHT -> "時尚明亮模式"
                                ThemeMode.DARK -> "質感極簡深色模式"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = when (themeMode) {
                                ThemeMode.SYSTEM -> "系統"
                                ThemeMode.LIGHT -> "淺色"
                                ThemeMode.DARK -> "深色"
                            },
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Software Update Option Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = "軟體線上更新",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "軟體線上更新檢查",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = currentAppVersion,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                }
                                Text(
                                    text = "上次檢查：$lastUpdateCheckTime",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "開啟自動檢查新版本",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isAutoCheckUpdateEnabled,
                            onCheckedChange = { onToggleAutoCheckUpdate() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onCheckForUpdates,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("check_updates_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "立即檢查最新軟體版本",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Section Title: Report & Data Export
            Text(
                text = "報表匯出與備份",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )

            if (isWideScreen) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ActionOptionCard(
                            icon = Icons.Default.Description,
                            title = "匯出 CSV 報表",
                            description = "將本月開銷明細匯出為標準 CSV 檔案，方便在 Excel 或 Google 試算表查看。",
                            buttonText = "匯出 CSV 檔案",
                            onClick = { onExportCsv(context) },
                            testTag = "export_csv_btn"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ActionOptionCard(
                            icon = Icons.Default.CloudUpload,
                            title = "完整 JSON 資料備份",
                            description = "將所有歷年帳務與分類備份為加密 JSON 檔案，方便換手機或雲端備份。",
                            buttonText = "匯出一鍵備份",
                            onClick = { onExportJson(context) },
                            testTag = "export_json_btn"
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ActionOptionCard(
                            icon = Icons.Default.CloudDownload,
                            title = "復原 JSON 備份檔",
                            description = "匯入先前備份的 JSON 檔案內容，無縫還原過去的所有記帳紀錄。",
                            buttonText = "貼上並復原備份",
                            onClick = { showRestoreDialog = true },
                            testTag = "restore_json_btn"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        ActionOptionCard(
                            icon = Icons.Default.AutoAwesome,
                            title = "匯入日常風格範例帳務",
                            description = "一鍵注入示範數據，立即體驗豐富繽紛圖表與完整數據！",
                            buttonText = "匯入示範數據",
                            onClick = onLoadSampleData,
                            testTag = "load_sample_btn"
                        )
                    }
                }

                ActionOptionCard(
                    icon = Icons.Default.DeleteSweep,
                    title = "重設 / 清空所有資料",
                    description = "清除本地 Room 資料庫中的所有記帳資料（不可逆）。",
                    buttonText = "清空所有資料",
                    isDanger = true,
                    onClick = { showClearDialog = true },
                    testTag = "clear_all_data_btn"
                )
            } else {
                // CSV Export Card
                ActionOptionCard(
                    icon = Icons.Default.Description,
                    title = "匯出 CSV 報表",
                    description = "將本月開銷明細匯出為標準 CSV 檔案，方便在 Excel、Google 試算表或 LINE 共享查看。",
                    buttonText = "匯出 CSV 檔案",
                    onClick = { onExportCsv(context) },
                    testTag = "export_csv_btn"
                )

                // JSON Backup Card
                ActionOptionCard(
                    icon = Icons.Default.CloudUpload,
                    title = "完整 JSON 資料備份",
                    description = "將所有歷年帳務與分類備份為加密 JSON 檔案，方便換手機或備份至雲端。",
                    buttonText = "匯出一鍵備份",
                    onClick = { onExportJson(context) },
                    testTag = "export_json_btn"
                )

                // JSON Restore Card
                ActionOptionCard(
                    icon = Icons.Default.CloudDownload,
                    title = "復原 JSON 備份檔",
                    description = "匯入先前備份的 JSON 檔案文字內容，無縫還原過去的所有記帳歷史紀錄。",
                    buttonText = "貼上並復原備份",
                    onClick = { showRestoreDialog = true },
                    testTag = "restore_json_btn"
                )

                // Section Title: Preset & Maintenance
                Text(
                    text = "示範與維護",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                // Sample Data Import
                ActionOptionCard(
                    icon = Icons.Default.AutoAwesome,
                    title = "匯入青年日常風格範例帳務",
                    description = "一鍵注入精美咖啡、聚餐、定期票與兼職獎金樣本數據，立即體驗豐富繽紛圖表！",
                    buttonText = "匯入示範數據",
                    onClick = onLoadSampleData,
                    testTag = "load_sample_btn"
                )

                // Clear All Data
                ActionOptionCard(
                    icon = Icons.Default.DeleteSweep,
                    title = "重設 / 清空所有資料",
                    description = "清除本地 Room 資料庫中的所有記帳資料（不可逆）。",
                    buttonText = "清空所有資料",
                    isDanger = true,
                    onClick = { showClearDialog = true },
                    testTag = "clear_all_data_btn"
                )
            }
        }
    }

    // Restore JSON Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("復原 JSON 備份", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "請在下方貼上先前匯出的 JSON 備份內容：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = restoreJsonText,
                        onValueChange = { restoreJsonText = it },
                        placeholder = { Text("{\"version\": 1, \"transactions\": [...]}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("restore_json_input"),
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (restoreJsonText.isNotBlank()) {
                            onRestoreJson(restoreJsonText)
                            showRestoreDialog = false
                            restoreJsonText = ""
                        }
                    },
                    enabled = restoreJsonText.isNotBlank()
                ) {
                    Text("確認復原")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // Clear All Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("確認清空所有帳務資料？", fontWeight = FontWeight.Bold) },
            text = {
                Text("此操作將永久刪除本地所有的開銷與收入紀錄，此動作無法復原。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralExpense)
                ) {
                    Text("確認清空")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun ActionOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit,
    isDanger: Boolean = false,
    testTag: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDanger) CoralExpense.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDanger) CoralExpense else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isDanger) CoralExpense else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isDanger) CoralExpense else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
