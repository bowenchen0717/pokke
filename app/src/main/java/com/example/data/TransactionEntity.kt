package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE,
    INCOME
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "EXPENSE" or "INCOME"
    val categoryId: String,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColorHex: Long,
    val dateMillis: Long,
    val note: String = "",
    val paymentMethod: String = "現金"
)
