package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // "EXPENSE" or "INCOME"
    val iconName: String,
    val colorHex: Long,
    val isDefault: Boolean = true,
    val sortOrder: Int = 0
) {
    companion object {
        val DEFAULT_EXPENSE_CATEGORIES = listOf(
            CategoryEntity("food", "餐飲飲食", "EXPENSE", "restaurant", 0xFFFF5252, true, 0),
            CategoryEntity("drinks", "手搖咖啡", "EXPENSE", "local_cafe", 0xFFFB8C00, true, 1),
            CategoryEntity("transport", "交通出行", "EXPENSE", "directions_bus", 0xFF29B6F6, true, 2),
            CategoryEntity("shopping", "購物消費", "EXPENSE", "shopping_bag", 0xFFAB47BC, true, 3),
            CategoryEntity("entertainment", "娛樂休閒", "EXPENSE", "sports_esports", 0xFF7E57C2, true, 4),
            CategoryEntity("bills", "日常繳費", "EXPENSE", "receipt_long", 0xFF26A69A, true, 5),
            CategoryEntity("medical", "醫療保健", "EXPENSE", "medical_services", 0xFFEC4899, true, 6),
            CategoryEntity("other_exp", "其他開銷", "EXPENSE", "more_horiz", 0xFF78909C, true, 7)
        )

        val DEFAULT_INCOME_CATEGORIES = listOf(
            CategoryEntity("salary", "薪資收入", "INCOME", "payments", 0xFF10B981, true, 0),
            CategoryEntity("bonus", "兼職獎金", "INCOME", "card_giftcard", 0xFF3B82F6, true, 1),
            CategoryEntity("investment", "理財投資", "INCOME", "trending_up", 0xFF8B5CF6, true, 2),
            CategoryEntity("allowance", "零用紅包", "INCOME", "savings", 0xFFF43F5E, true, 3),
            CategoryEntity("other_inc", "其他收入", "INCOME", "account_balance_wallet", 0xFF06B6D4, true, 4)
        )
    }
}
