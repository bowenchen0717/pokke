package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_methods")
data class PaymentMethodEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconName: String = "account_balance_wallet",
    val sortOrder: Int = 0
) {
    companion object {
        val DEFAULT_PAYMENT_METHODS = listOf(
            PaymentMethodEntity("cash", "現金", "payments", 0),
            PaymentMethodEntity("credit_card", "信用卡", "credit_card", 1),
            PaymentMethodEntity("line_pay", "LINE Pay", "qr_code", 2),
            PaymentMethodEntity("jko_pay", "街口支付", "account_balance_wallet", 3),
            PaymentMethodEntity("bank", "銀行轉帳", "account_balance", 4),
            PaymentMethodEntity("easycard", "悠遊卡", "contactless", 5)
        )
    }
}
