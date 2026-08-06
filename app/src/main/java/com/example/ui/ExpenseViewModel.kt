package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CategoryEntity
import com.example.data.ExpenseRepository
import com.example.data.PaymentMethodEntity
import com.example.data.TransactionEntity
import com.example.ui.theme.ThemeMode
import com.example.update.AppUpdateRepository
import com.example.update.AppVersionInfo
import com.example.update.UpdateStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



data class CategoryStat(
    val categoryId: String,
    val categoryName: String,
    val iconName: String,
    val colorHex: Long,
    val amount: Double,
    val percentage: Float
)

data class DailyTrendItem(
    val dateLabel: String,
    val dayOfMonth: Int,
    val dateMillis: Long,
    val expenseAmount: Double,
    val incomeAmount: Double
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ExpenseRepository(AppDatabase.getDatabase(application))

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allPaymentMethods: StateFlow<List<PaymentMethodEntity>> = repository.allPaymentMethods.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current YearMonth format: "yyyy-MM"
    private val currentYearMonthStr: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            return sdf.format(Date())
        }

    val selectedYearMonth = MutableStateFlow(currentYearMonthStr)
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val activeTab = MutableStateFlow(0) // 0: Transactions, 1: Analytics, 2: Backup/Export

    private val prefs = application.getSharedPreferences("pokke_prefs", Context.MODE_PRIVATE)
    val monthlyBudgetLimit = MutableStateFlow(
        prefs.getFloat("monthly_budget_limit", 30000f).toDouble()
    )

    fun setMonthlyBudgetLimit(limit: Double) {
        monthlyBudgetLimit.value = limit
        prefs.edit().putFloat("monthly_budget_limit", limit.toFloat()).apply()
        toastMessage.value = "已更新每月預算上限為 $${String.format(Locale.US, "%,.0f", limit)}"
    }

    val showDashboardCard = MutableStateFlow(true)
    val showCategoryFilterBar = MutableStateFlow(true)

    // Modularized Software Update Repository & State Management
    private val appUpdateRepository = AppUpdateRepository(application)

    val versionInfo = MutableStateFlow(
        AppVersionInfo(
            currentVersion = appUpdateRepository.getInstalledVersionName(),
            buildNumber = appUpdateRepository.getInstalledVersionCode().toString(),
            packageName = appUpdateRepository.getPackageName(),
            latestVersion = appUpdateRepository.getInstalledVersionName()
        )
    )
    val currentAppVersion = MutableStateFlow(appUpdateRepository.getInstalledVersionName())
    val updateStatus = MutableStateFlow(UpdateStatus.IDLE)
    val updateProgress = MutableStateFlow(0f)
    val downloadedMb = MutableStateFlow(0.0)
    val isAutoCheckUpdateEnabled = MutableStateFlow(
        prefs.getBoolean("auto_check_update", true)
    )
    val lastUpdateCheckTime = MutableStateFlow(
        prefs.getString("last_update_check", "今天 18:30") ?: "今天 18:30"
    )
    val showUpdateDialog = MutableStateFlow(false)

    fun toggleAutoCheckUpdate() {
        val newValue = !isAutoCheckUpdateEnabled.value
        isAutoCheckUpdateEnabled.value = newValue
        prefs.edit().putBoolean("auto_check_update", newValue).apply()
        toastMessage.value = if (newValue) "已開啟自動檢查 Google Play 商店更新" else "已關閉自動檢查更新"
    }

    fun checkForUpdates(userInitiated: Boolean = true) {
        viewModelScope.launch {
            updateStatus.value = UpdateStatus.CHECKING
            if (userInitiated) {
                showUpdateDialog.value = true
            }

            val currentTimeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeStr = "今天 ${currentTimeSdf.format(Date())}"
            lastUpdateCheckTime.value = timeStr
            prefs.edit().putString("last_update_check", timeStr).apply()

            val installedVerName = appUpdateRepository.getInstalledVersionName()
            val installedVerCode = appUpdateRepository.getInstalledVersionCode()
            val pkgName = appUpdateRepository.getPackageName()

            versionInfo.value = versionInfo.value.copy(
                currentVersion = installedVerName,
                buildNumber = installedVerCode.toString(),
                packageName = pkgName
            )
            currentAppVersion.value = installedVerName

            val (status, latestVer) = appUpdateRepository.checkGooglePlayUpdate()
            if (latestVer != null) {
                versionInfo.value = versionInfo.value.copy(latestVersion = latestVer)
            }
            updateStatus.value = status

            when (status) {
                UpdateStatus.UPDATE_AVAILABLE -> {
                    showUpdateDialog.value = true
                    toastMessage.value = "Google Play 商店發現全新版本，可立即更新！"
                }
                UpdateStatus.UP_TO_DATE -> {
                    if (!userInitiated) showUpdateDialog.value = false
                    toastMessage.value = "目前已是 Google Play 商店最新版本 ($installedVerName)"
                }
                UpdateStatus.DEV_ENVIRONMENT_CHECKED -> {
                    if (userInitiated) {
                        showUpdateDialog.value = true
                    } else {
                        showUpdateDialog.value = false
                    }
                }
                else -> {}
            }
        }
    }

    fun openPlayStorePage(context: Context) {
        appUpdateRepository.openPlayStorePage(context)
    }

    fun startDownloadingUpdate() {
        viewModelScope.launch {
            updateStatus.value = UpdateStatus.DOWNLOADING
            updateProgress.value = 0f
            downloadedMb.value = 0.0
            val totalSize = versionInfo.value.updateSizeMb
            
            val steps = 20
            for (i in 1..steps) {
                delay(100)
                val p = i.toFloat() / steps
                updateProgress.value = p
                downloadedMb.value = (p * totalSize).coerceAtMost(totalSize)
            }
            
            updateStatus.value = UpdateStatus.READY_TO_INSTALL
            toastMessage.value = "Google Play 更新包已下載完成！"
        }
    }

    fun installUpdate() {
        viewModelScope.launch {
            delay(600)
            updateStatus.value = UpdateStatus.UP_TO_DATE
            showUpdateDialog.value = false
            toastMessage.value = "🎉 軟體已成功更新至 Google Play 最新版本！"
        }
    }

    fun dismissUpdateDialog() {
        showUpdateDialog.value = false
        if (updateStatus.value == UpdateStatus.CHECKING) {
            updateStatus.value = UpdateStatus.IDLE
        }
    }

    val isAddEditSheetOpen = MutableStateFlow(false)
    val editingTransaction = MutableStateFlow<TransactionEntity?>(null)

    val toastMessage = MutableStateFlow<String?>(null)

    fun toggleShowDashboardCard() {
        showDashboardCard.value = !showDashboardCard.value
    }

    fun toggleShowCategoryFilterBar() {
        showCategoryFilterBar.value = !showCategoryFilterBar.value
    }

    init {
        viewModelScope.launch {
            repository.ensureDefaultCategories()
            repository.ensureDefaultPaymentMethods()
        }
    }

    // Filtered monthly transactions
    val filteredTransactions = combine(
        allTransactions,
        selectedYearMonth,
        searchQuery,
        selectedCategoryFilter
    ) { list, yearMonth, query, catFilter ->
        val ymFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        list.filter { item ->
            val itemYm = ymFormat.format(Date(item.dateMillis))
            val matchesMonth = itemYm == yearMonth
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.note.contains(query, ignoreCase = true) ||
                    item.categoryName.contains(query, ignoreCase = true) ||
                    item.paymentMethod.contains(query, ignoreCase = true)
            val matchesCategory = catFilter == null || item.categoryId == catFilter
            matchesMonth && matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Monthly Overview Calculations
    val monthlyTotalIncome = filteredTransactions.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.type == "INCOME" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyTotalExpense = filteredTransactions.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyNetBalance = combine(monthlyTotalIncome, monthlyTotalExpense) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Category Expense Breakdown for Analytics
    val categoryExpenseBreakdown = filteredTransactions.combine(MutableStateFlow(Unit)) { list, _ ->
        val expenses = list.filter { it.type == "EXPENSE" }
        val total = expenses.sumOf { it.amount }
        if (total <= 0.0) return@combine emptyList<CategoryStat>()

        val grouped = expenses.groupBy { it.categoryId }
        grouped.map { (catId, items) ->
            val sum = items.sumOf { it.amount }
            val first = items.first()
            CategoryStat(
                categoryId = catId,
                categoryName = first.categoryName,
                iconName = first.categoryIcon,
                colorHex = first.categoryColorHex,
                amount = sum,
                percentage = ((sum / total) * 100).toFloat()
            )
        }.sortedByDescending { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily Trend Data for Analytics
    val dailyTrendData = combine(filteredTransactions, selectedYearMonth) { list, yearMonth ->
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        
        // Group by day string
        val map = list.groupBy { 
            cal.timeInMillis = it.dateMillis
            cal.get(Calendar.DAY_OF_MONTH)
        }

        // Get max days in month
        val parseFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val date = parseFormat.parse(yearMonth) ?: Date()
        cal.time = date
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        (1..daysInMonth).map { day ->
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dateLabel = sdf.format(cal.time)
            val dayItems = map[day] ?: emptyList()
            DailyTrendItem(
                dateLabel = dateLabel,
                dayOfMonth = day,
                dateMillis = cal.timeInMillis,
                expenseAmount = dayItems.filter { it.type == "EXPENSE" }.sumOf { it.amount },
                incomeAmount = dayItems.filter { it.type == "INCOME" }.sumOf { it.amount }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPreviousMonth() {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val cal = Calendar.getInstance()
        val current = sdf.parse(selectedYearMonth.value) ?: Date()
        cal.time = current
        cal.add(Calendar.MONTH, -1)
        selectedYearMonth.value = sdf.format(cal.time)
    }

    fun selectNextMonth() {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val cal = Calendar.getInstance()
        val current = sdf.parse(selectedYearMonth.value) ?: Date()
        cal.time = current
        cal.add(Calendar.MONTH, 1)
        selectedYearMonth.value = sdf.format(cal.time)
    }

    fun openAddSheet() {
        editingTransaction.value = null
        isAddEditSheetOpen.value = true
    }

    fun openEditSheet(transaction: TransactionEntity) {
        editingTransaction.value = transaction
        isAddEditSheetOpen.value = true
    }

    fun closeAddEditSheet() {
        isAddEditSheetOpen.value = false
        editingTransaction.value = null
    }

    fun saveTransaction(
        id: Long,
        title: String,
        amount: Double,
        type: String,
        category: CategoryEntity,
        dateMillis: Long,
        note: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                id = id,
                title = title.ifBlank { category.name },
                amount = amount,
                type = type,
                categoryId = category.id,
                categoryName = category.name,
                categoryIcon = category.iconName,
                categoryColorHex = category.colorHex,
                dateMillis = dateMillis,
                note = note,
                paymentMethod = paymentMethod
            )
            if (id == 0L) {
                repository.insertTransaction(entity)
                toastMessage.value = "已新增「${entity.title}」"
            } else {
                repository.updateTransaction(entity)
                toastMessage.value = "已更新紀錄"
            }
            closeAddEditSheet()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            toastMessage.value = "已刪除「${transaction.title}」"
        }
    }

    fun toggleThemeMode() {
        themeMode.value = when (themeMode.value) {
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
        }
    }

    fun exportCsv(context: Context) {
        viewModelScope.launch {
            val transactions = filteredTransactions.value
            if (transactions.isEmpty()) {
                toastMessage.value = "當前月份無帳務可匯出"
                return@launch
            }
            val csvText = repository.generateCsvReport(transactions)
            repository.shareTextContent(context, "匯出 ${selectedYearMonth.value} 記帳報表", csvText)
        }
    }

    fun exportJsonBackup(context: Context) {
        viewModelScope.launch {
            val transactions = allTransactions.value
            if (transactions.isEmpty()) {
                toastMessage.value = "沒有帳務資料可供備份"
                return@launch
            }
            val jsonText = repository.generateJsonBackup(transactions)
            repository.shareTextContent(context, "簡單愛記帳 完整 JSON 備份檔", jsonText)
        }
    }

    fun restoreJsonBackup(jsonString: String) {
        viewModelScope.launch {
            val result = repository.restoreFromJson(jsonString)
            result.onSuccess { count ->
                toastMessage.value = "成功復原 $count 筆帳務紀錄！"
            }.onFailure { err ->
                toastMessage.value = "復原失敗：${err.localizedMessage ?: "無效的 JSON 格式"}"
            }
        }
    }

    fun loadSampleData() {
        viewModelScope.launch {
            repository.loadSampleData()
            toastMessage.value = "已為您匯入精美青年日常帳務數據！"
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            toastMessage.value = "已清空所有帳務資料"
        }
    }

    fun clearToast() {
        toastMessage.value = null
    }

    // Category Management
    fun addCategory(name: String, type: String, iconName: String, colorHex: Long) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val currentList = allCategories.value.filter { it.type == type }
            val newOrder = if (currentList.isNotEmpty()) currentList.maxOf { it.sortOrder } + 1 else 0
            val id = "cat_${System.currentTimeMillis()}"
            val newCategory = CategoryEntity(
                id = id,
                name = name,
                type = type,
                iconName = iconName,
                colorHex = colorHex,
                isDefault = false,
                sortOrder = newOrder
            )
            repository.insertCategory(newCategory)
            toastMessage.value = "已新增「$name」分類"
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            toastMessage.value = "已刪除「${category.name}」分類"
        }
    }

    fun moveCategoryUp(category: CategoryEntity) {
        viewModelScope.launch {
            val list = allCategories.value.filter { it.type == category.type }.sortedBy { it.sortOrder }
            val index = list.indexOfFirst { it.id == category.id }
            if (index > 0) {
                val prev = list[index - 1]
                val updatedCurr = category.copy(sortOrder = prev.sortOrder)
                val updatedPrev = prev.copy(sortOrder = category.sortOrder)
                repository.updateCategories(listOf(updatedCurr, updatedPrev))
            }
        }
    }

    fun moveCategoryDown(category: CategoryEntity) {
        viewModelScope.launch {
            val list = allCategories.value.filter { it.type == category.type }.sortedBy { it.sortOrder }
            val index = list.indexOfFirst { it.id == category.id }
            if (index >= 0 && index < list.size - 1) {
                val next = list[index + 1]
                val updatedCurr = category.copy(sortOrder = next.sortOrder)
                val updatedNext = next.copy(sortOrder = category.sortOrder)
                repository.updateCategories(listOf(updatedCurr, updatedNext))
            }
        }
    }

    // Payment Method Management
    fun addPaymentMethod(name: String, iconName: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val currentList = allPaymentMethods.value
            val newOrder = if (currentList.isNotEmpty()) currentList.maxOf { it.sortOrder } + 1 else 0
            val id = "pm_${System.currentTimeMillis()}"
            val newPm = PaymentMethodEntity(
                id = id,
                name = name,
                iconName = iconName,
                sortOrder = newOrder
            )
            repository.insertPaymentMethod(newPm)
            toastMessage.value = "已新增支付方式「$name」"
        }
    }

    fun deletePaymentMethod(pm: PaymentMethodEntity) {
        viewModelScope.launch {
            repository.deletePaymentMethod(pm)
            toastMessage.value = "已刪除支付方式「${pm.name}」"
        }
    }

    fun movePaymentMethodUp(pm: PaymentMethodEntity) {
        viewModelScope.launch {
            val list = allPaymentMethods.value.sortedBy { it.sortOrder }
            val index = list.indexOfFirst { it.id == pm.id }
            if (index > 0) {
                val prev = list[index - 1]
                val updatedCurr = pm.copy(sortOrder = prev.sortOrder)
                val updatedPrev = prev.copy(sortOrder = pm.sortOrder)
                repository.updatePaymentMethods(listOf(updatedCurr, updatedPrev))
            }
        }
    }

    fun movePaymentMethodDown(pm: PaymentMethodEntity) {
        viewModelScope.launch {
            val list = allPaymentMethods.value.sortedBy { it.sortOrder }
            val index = list.indexOfFirst { it.id == pm.id }
            if (index >= 0 && index < list.size - 1) {
                val next = list[index + 1]
                val updatedCurr = pm.copy(sortOrder = next.sortOrder)
                val updatedNext = next.copy(sortOrder = pm.sortOrder)
                repository.updatePaymentMethods(listOf(updatedCurr, updatedNext))
            }
        }
    }
}
