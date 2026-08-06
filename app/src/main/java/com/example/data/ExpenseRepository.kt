package com.example.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExpenseRepository(private val db: AppDatabase) {
    private val transactionDao = db.transactionDao()
    private val categoryDao = db.categoryDao()
    private val paymentMethodDao = db.paymentMethodDao()

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allPaymentMethods: Flow<List<PaymentMethodEntity>> = paymentMethodDao.getAllPaymentMethods()

    suspend fun ensureDefaultCategories() = withContext(Dispatchers.IO) {
        if (categoryDao.getCategoryCount() == 0) {
            val defaults = CategoryEntity.DEFAULT_EXPENSE_CATEGORIES + CategoryEntity.DEFAULT_INCOME_CATEGORIES
            categoryDao.insertCategories(defaults)
        }
    }

    suspend fun ensureDefaultPaymentMethods() = withContext(Dispatchers.IO) {
        if (paymentMethodDao.getPaymentMethodCount() == 0) {
            paymentMethodDao.insertPaymentMethods(PaymentMethodEntity.DEFAULT_PAYMENT_METHODS)
        }
    }

    suspend fun insertCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category)
    }

    suspend fun updateCategories(categories: List<CategoryEntity>) = withContext(Dispatchers.IO) {
        categoryDao.insertCategories(categories)
    }

    suspend fun deleteCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
        categoryDao.deleteCategory(category)
    }

    suspend fun insertPaymentMethod(paymentMethod: PaymentMethodEntity) = withContext(Dispatchers.IO) {
        paymentMethodDao.insertPaymentMethod(paymentMethod)
    }

    suspend fun updatePaymentMethods(paymentMethods: List<PaymentMethodEntity>) = withContext(Dispatchers.IO) {
        paymentMethodDao.insertPaymentMethods(paymentMethods)
    }

    suspend fun deletePaymentMethod(paymentMethod: PaymentMethodEntity) = withContext(Dispatchers.IO) {
        paymentMethodDao.deletePaymentMethod(paymentMethod)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        transactionDao.deleteAllTransactions()
    }

    suspend fun generateCsvReport(transactions: List<TransactionEntity>): String = withContext(Dispatchers.Default) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.TAIWAN)
        val sb = StringBuilder()
        // UTF-8 BOM for Excel compatibility
        sb.append("\uFEFF")
        sb.append("ID,日期,類型,分類,名稱,金額(TWD),支付方式,備註\n")
        
        for (item in transactions) {
            val dateStr = dateFormat.format(Date(item.dateMillis))
            val typeStr = if (item.type == "EXPENSE") "支出" else "收入"
            val titleEscaped = item.title.replace("\"", "\"\"")
            val noteEscaped = item.note.replace("\"", "\"\"")
            val amountFormatted = String.format(Locale.US, "%.2f", item.amount)
            
            sb.append("${item.id},")
              .append("\"$dateStr\",")
              .append("\"$typeStr\",")
              .append("\"${item.categoryName}\",")
              .append("\"$titleEscaped\",")
              .append("$amountFormatted,")
              .append("\"${item.paymentMethod}\",")
              .append("\"$noteEscaped\"\n")
        }
        sb.toString()
    }

    suspend fun generateJsonBackup(transactions: List<TransactionEntity>): String = withContext(Dispatchers.Default) {
        val array = JSONArray()
        for (t in transactions) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("amount", t.amount)
                put("type", t.type)
                put("categoryId", t.categoryId)
                put("categoryName", t.categoryName)
                put("categoryIcon", t.categoryIcon)
                put("categoryColorHex", t.categoryColorHex)
                put("dateMillis", t.dateMillis)
                put("note", t.note)
                put("paymentMethod", t.paymentMethod)
            }
            array.put(obj)
        }
        val wrapper = JSONObject().apply {
            put("version", 1)
            put("exportDate", System.currentTimeMillis())
            put("transactions", array)
        }
        wrapper.toString(2)
    }

    suspend fun restoreFromJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val wrapper = JSONObject(jsonString)
            val array = wrapper.getJSONArray("transactions")
            val restoredList = mutableListOf<TransactionEntity>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val entity = TransactionEntity(
                    id = 0, // Auto re-assign primary key or keep
                    title = obj.optString("title", "未命名紀錄"),
                    amount = obj.optDouble("amount", 0.0),
                    type = obj.optString("type", "EXPENSE"),
                    categoryId = obj.optString("categoryId", "other_exp"),
                    categoryName = obj.optString("categoryName", "其他"),
                    categoryIcon = obj.optString("categoryIcon", "more_horiz"),
                    categoryColorHex = obj.optLong("categoryColorHex", 0xFF78909C),
                    dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                    note = obj.optString("note", ""),
                    paymentMethod = obj.optString("paymentMethod", "現金")
                )
                restoredList.add(entity)
            }

            if (restoredList.isNotEmpty()) {
                transactionDao.deleteAllTransactions()
                transactionDao.insertAllTransactions(restoredList)
                Result.success(restoredList.size)
            } else {
                Result.failure(Exception("備份檔案中沒有有效的帳務資料"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadSampleData() = withContext(Dispatchers.IO) {
        ensureDefaultCategories()
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        val dayMillis = 86400000L

        val samples = listOf(
            TransactionEntity(
                title = "月薪發放",
                amount = 45000.0,
                type = "INCOME",
                categoryId = "salary",
                categoryName = "薪資收入",
                categoryIcon = "payments",
                categoryColorHex = 0xFF10B981,
                dateMillis = now - (15 * dayMillis),
                note = "本月薪資已入帳",
                paymentMethod = "銀行轉帳"
            ),
            TransactionEntity(
                title = "星巴克燕麥拿鐵",
                amount = 175.0,
                type = "EXPENSE",
                categoryId = "drinks",
                categoryName = "手搖咖啡",
                categoryIcon = "local_cafe",
                categoryColorHex = 0xFFFB8C00,
                dateMillis = now - (12 * dayMillis),
                note = "與同事特大杯買一送一",
                paymentMethod = "LINE Pay"
            ),
            TransactionEntity(
                title = "捷運定期票",
                amount = 1200.0,
                type = "EXPENSE",
                categoryId = "transport",
                categoryName = "交通出行",
                categoryIcon = "directions_bus",
                categoryColorHex = 0xFF29B6F6,
                dateMillis = now - (10 * dayMillis),
                note = "TPASS 30日月票",
                paymentMethod = "信用卡"
            ),
            TransactionEntity(
                title = "鼎泰豐小籠包聚餐",
                amount = 860.0,
                type = "EXPENSE",
                categoryId = "food",
                categoryName = "餐飲飲食",
                categoryIcon = "restaurant",
                categoryColorHex = 0xFFFF5252,
                dateMillis = now - (8 * dayMillis),
                note = "高中同學聚會",
                paymentMethod = "現金"
            ),
            TransactionEntity(
                title = "Nike 潮牌慢跑鞋",
                amount = 3200.0,
                type = "EXPENSE",
                categoryId = "shopping",
                categoryName = "購物消費",
                categoryIcon = "shopping_bag",
                categoryColorHex = 0xFFAB47BC,
                dateMillis = now - (6 * dayMillis),
                note = "季末特價購入",
                paymentMethod = "信用卡"
            ),
            TransactionEntity(
                title = "副業設計稿接案",
                amount = 8000.0,
                type = "INCOME",
                categoryId = "bonus",
                categoryName = "兼職獎金",
                categoryIcon = "card_giftcard",
                categoryColorHex = 0xFF3B82F6,
                dateMillis = now - (4 * dayMillis),
                note = "UI設計委託尾款",
                paymentMethod = "銀行轉帳"
            ),
            TransactionEntity(
                title = "威秀影城電影票",
                amount = 380.0,
                type = "EXPENSE",
                categoryId = "entertainment",
                categoryName = "娛樂休閒",
                categoryIcon = "sports_esports",
                categoryColorHex = 0xFF7E57C2,
                dateMillis = now - (3 * dayMillis),
                note = "週末院線大片",
                paymentMethod = "街口支付"
            ),
            TransactionEntity(
                title = "五桐號楊枝甘露",
                amount = 85.0,
                type = "EXPENSE",
                categoryId = "drinks",
                categoryName = "手搖咖啡",
                categoryIcon = "local_cafe",
                categoryColorHex = 0xFFFB8C00,
                dateMillis = now - (2 * dayMillis),
                note = "下午茶點心",
                paymentMethod = "LINE Pay"
            ),
            TransactionEntity(
                title = "電信費繳納",
                amount = 699.0,
                type = "EXPENSE",
                categoryId = "bills",
                categoryName = "日常繳費",
                categoryIcon = "receipt_long",
                categoryColorHex = 0xFF26A69A,
                dateMillis = now - (1 * dayMillis),
                note = "5G 吃到飽月租",
                paymentMethod = "信用卡"
            ),
            TransactionEntity(
                title = "拉麵午餐",
                amount = 260.0,
                type = "EXPENSE",
                categoryId = "food",
                categoryName = "餐飲飲食",
                categoryIcon = "restaurant",
                categoryColorHex = 0xFFFF5252,
                dateMillis = now,
                note = "豚骨叉燒拉麵",
                paymentMethod = "現金"
            )
        )

        transactionDao.insertAllTransactions(samples)
    }

    fun shareTextContent(context: Context, title: String, content: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }
}
