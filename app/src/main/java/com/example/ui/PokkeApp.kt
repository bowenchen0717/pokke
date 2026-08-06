package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddEditTransactionSheet
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.BalanceHeaderCard
import com.example.ui.components.CategoryManagerSheet
import com.example.ui.components.PaymentMethodManagerSheet
import com.example.ui.components.SoftwareUpdateDialog
import kotlinx.coroutines.launch
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ReportBackupScreen
import com.example.ui.screens.TransactionListScreen
import com.example.ui.theme.GradientEnd
import com.example.ui.theme.GradientStart
import com.example.ui.theme.PokkeTheme
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokkeApp(viewModel: ExpenseViewModel) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val allPaymentMethods by viewModel.allPaymentMethods.collectAsStateWithLifecycle()

    val selectedYearMonth by viewModel.selectedYearMonth.collectAsStateWithLifecycle()
    val totalIncome by viewModel.monthlyTotalIncome.collectAsStateWithLifecycle()
    val totalExpense by viewModel.monthlyTotalExpense.collectAsStateWithLifecycle()
    val netBalance by viewModel.monthlyNetBalance.collectAsStateWithLifecycle()
    val monthlyBudgetLimit by viewModel.monthlyBudgetLimit.collectAsStateWithLifecycle()

    val categoryStats by viewModel.categoryExpenseBreakdown.collectAsStateWithLifecycle()
    val dailyTrendData by viewModel.dailyTrendData.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

    val showDashboardCard by viewModel.showDashboardCard.collectAsStateWithLifecycle()
    val showCategoryFilterBar by viewModel.showCategoryFilterBar.collectAsStateWithLifecycle()

    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isAddEditSheetOpen by viewModel.isAddEditSheetOpen.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    // Software Update state variables
    val currentAppVersion by viewModel.currentAppVersion.collectAsStateWithLifecycle()
    val versionInfo by viewModel.versionInfo.collectAsStateWithLifecycle()
    val updateStatus by viewModel.updateStatus.collectAsStateWithLifecycle()
    val updateProgress by viewModel.updateProgress.collectAsStateWithLifecycle()
    val downloadedMb by viewModel.downloadedMb.collectAsStateWithLifecycle()
    val isAutoCheckUpdateEnabled by viewModel.isAutoCheckUpdateEnabled.collectAsStateWithLifecycle()
    val lastUpdateCheckTime by viewModel.lastUpdateCheckTime.collectAsStateWithLifecycle()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsStateWithLifecycle()

    var showSearchDialog by remember { mutableStateOf(false) }
    var showCategoryManagerSheet by remember { mutableStateOf(false) }
    var showPaymentManagerSheet by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val catSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    PokkeTheme(themeMode = themeMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AppDrawerContent(
                    activeTab = activeTab,
                    themeMode = themeMode,
                    categoryCount = allCategories.size,
                    paymentMethodCount = allPaymentMethods.size,
                    showDashboardCard = showDashboardCard,
                    showCategoryFilterBar = showCategoryFilterBar,
                    onSelectTab = { viewModel.activeTab.value = it },
                    onOpenCategoryManager = { showCategoryManagerSheet = true },
                    onOpenPaymentManager = { showPaymentManagerSheet = true },
                    onToggleTheme = { viewModel.toggleThemeMode() },
                    onToggleShowDashboardCard = { viewModel.toggleShowDashboardCard() },
                    onToggleShowCategoryFilterBar = { viewModel.toggleShowCategoryFilterBar() },
                    onClearAllData = { viewModel.clearAllData() },
                    onCloseDrawer = { coroutineScope.launch { drawerState.close() } },
                    currentAppVersion = currentAppVersion,
                    onCheckForUpdates = { viewModel.checkForUpdates(userInitiated = true) }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(
                                onClick = { coroutineScope.launch { drawerState.open() } },
                                modifier = Modifier.testTag("topbar_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "開啟側邊表單",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(listOf(GradientStart, GradientEnd))
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "愛",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "簡單愛記帳",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                    actions = {
                        // Search Button
                        IconButton(
                            onClick = { showSearchDialog = true },
                            modifier = Modifier.testTag("topbar_search_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "搜尋帳務",
                                tint = if (searchQuery.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Theme Mode Toggle
                        IconButton(
                            onClick = { viewModel.toggleThemeMode() },
                            modifier = Modifier.testTag("topbar_theme_btn")
                        ) {
                            Icon(
                                imageVector = when (themeMode) {
                                    ThemeMode.DARK -> Icons.Default.DarkMode
                                    else -> Icons.Default.LightMode
                                },
                                contentDescription = "切換主題模式",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == 0,
                        onClick = { viewModel.activeTab.value = 0 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "明細日記"
                            )
                        },
                        label = { Text("明細日記") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_list")
                    )

                    NavigationBarItem(
                        selected = activeTab == 1,
                        onClick = { viewModel.activeTab.value = 1 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = "視覺圖表"
                            )
                        },
                        label = { Text("視覺圖表") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_charts")
                    )

                    NavigationBarItem(
                        selected = activeTab == 2,
                        onClick = { viewModel.activeTab.value = 2 },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.FolderZip,
                                contentDescription = "報表備份"
                            )
                        },
                        label = { Text("報表備份") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_backup")
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.openAddSheet() },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(6.dp),
                    modifier = Modifier.testTag("add_transaction_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "新增帳務",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 960.dp)
                ) {
                    // Balance Header Card at top
                    if (showDashboardCard) {
                        BalanceHeaderCard(
                            selectedMonthStr = selectedYearMonth,
                            totalIncome = totalIncome,
                            totalExpense = totalExpense,
                            netBalance = netBalance,
                            monthlyBudgetLimit = monthlyBudgetLimit,
                            onUpdateBudgetLimit = { viewModel.setMonthlyBudgetLimit(it) },
                            onPreviousMonth = { viewModel.selectPreviousMonth() },
                            onNextMonth = { viewModel.selectNextMonth() },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Tab Screen View
                    Box(modifier = Modifier.weight(1f)) {
                        when (activeTab) {
                            0 -> TransactionListScreen(
                                transactions = filteredTransactions,
                                categories = allCategories,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                selectedCategoryFilter = selectedCategoryFilter,
                                onCategoryFilterChange = { viewModel.selectedCategoryFilter.value = it },
                                onEditTransaction = { viewModel.openEditSheet(it) },
                                onDeleteTransaction = { viewModel.deleteTransaction(it) },
                                showCategoryFilterBar = showCategoryFilterBar
                            )

                            1 -> AnalyticsScreen(
                                categoryStats = categoryStats,
                                dailyTrendData = dailyTrendData,
                                transactions = filteredTransactions,
                                totalExpense = totalExpense
                            )

                            2 -> ReportBackupScreen(
                                themeMode = themeMode,
                                onToggleThemeMode = { viewModel.toggleThemeMode() },
                                onExportCsv = { ctx -> viewModel.exportCsv(ctx) },
                                onExportJson = { ctx -> viewModel.exportJsonBackup(ctx) },
                                onRestoreJson = { json -> viewModel.restoreJsonBackup(json) },
                                onLoadSampleData = { viewModel.loadSampleData() },
                                onClearAllData = { viewModel.clearAllData() },
                                currentAppVersion = currentAppVersion,
                                lastUpdateCheckTime = lastUpdateCheckTime,
                                isAutoCheckUpdateEnabled = isAutoCheckUpdateEnabled,
                                onCheckForUpdates = { viewModel.checkForUpdates(userInitiated = true) },
                                onToggleAutoCheckUpdate = { viewModel.toggleAutoCheckUpdate() }
                            )
                        }
                    }
                }
            }
        }

        // Add/Edit Transaction Bottom Sheet
        if (isAddEditSheetOpen) {
            AddEditTransactionSheet(
                sheetState = sheetState,
                editingTransaction = editingTransaction,
                categories = allCategories,
                paymentMethods = allPaymentMethods,
                onDismiss = { viewModel.closeAddEditSheet() },
                onSave = { id, title, amount, type, category, dateMillis, note, paymentMethod ->
                    viewModel.saveTransaction(
                        id, title, amount, type, category, dateMillis, note, paymentMethod
                    )
                }
            )
        }

        // Category Manager Sheet
        if (showCategoryManagerSheet) {
            CategoryManagerSheet(
                sheetState = catSheetState,
                categories = allCategories,
                onDismiss = { showCategoryManagerSheet = false },
                onAddCategory = { name, type, iconName, colorHex ->
                    viewModel.addCategory(name, type, iconName, colorHex)
                },
                onDeleteCategory = { cat -> viewModel.deleteCategory(cat) },
                onMoveUp = { cat -> viewModel.moveCategoryUp(cat) },
                onMoveDown = { cat -> viewModel.moveCategoryDown(cat) }
            )
        }

        // Payment Method Manager Sheet
        if (showPaymentManagerSheet) {
            PaymentMethodManagerSheet(
                sheetState = pmSheetState,
                paymentMethods = allPaymentMethods,
                onDismiss = { showPaymentManagerSheet = false },
                onAddPaymentMethod = { name, iconName ->
                    viewModel.addPaymentMethod(name, iconName)
                },
                onDeletePaymentMethod = { pm -> viewModel.deletePaymentMethod(pm) },
                onMoveUp = { pm -> viewModel.movePaymentMethodUp(pm) },
                onMoveDown = { pm -> viewModel.movePaymentMethodDown(pm) }
            )
        }

        // Search Dialog Popup
        if (showSearchDialog) {
            AlertDialog(
                onDismissRequest = { showSearchDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                title = { Text("搜尋帳務紀錄", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            placeholder = { Text("輸入關鍵字、備註或金額...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "清除搜尋",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dialog_search_input")
                        )
                        if (searchQuery.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "共找到 ${filteredTransactions.size} 筆相符紀錄",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showSearchDialog = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("完成")
                    }
                },
                dismissButton = {
                    if (searchQuery.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.searchQuery.value = "" }
                        ) {
                            Text("清除搜尋")
                        }
                    }
                }
            )
        }

        // Software Update Online Dialog
        if (showUpdateDialog) {
            SoftwareUpdateDialog(
                versionInfo = versionInfo,
                currentVersion = currentAppVersion,
                updateStatus = updateStatus,
                downloadProgress = updateProgress,
                downloadedMb = downloadedMb,
                onStartDownload = { viewModel.startDownloadingUpdate() },
                onInstallUpdate = { viewModel.installUpdate() },
                onOpenPlayStore = { viewModel.openPlayStorePage(context) },
                onDismiss = { viewModel.dismissUpdateDialog() }
            )
        }
    }
}
}
