package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CategoryEntity
import com.example.data.PaymentMethodEntity
import com.example.data.TransactionEntity
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.MintIncome
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionSheet(
    sheetState: SheetState,
    editingTransaction: TransactionEntity?,
    categories: List<CategoryEntity>,
    paymentMethods: List<PaymentMethodEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        amount: Double,
        type: String,
        category: CategoryEntity,
        dateMillis: Long,
        note: String,
        paymentMethod: String
    ) -> Unit
) {
    var type by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.type ?: "EXPENSE")
    }

    val filteredCategories = remember(categories, type) {
        categories.filter { it.type == type }.ifEmpty {
            if (type == "EXPENSE") CategoryEntity.DEFAULT_EXPENSE_CATEGORIES
            else CategoryEntity.DEFAULT_INCOME_CATEGORIES
        }
    }

    var selectedCategory by remember(editingTransaction, type) {
        mutableStateOf(
            filteredCategories.find { it.id == editingTransaction?.categoryId }
                ?: filteredCategories.firstOrNull()
                ?: CategoryEntity.DEFAULT_EXPENSE_CATEGORIES.first()
        )
    }

    var amountText by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.let { 
            if (it.amount % 1.0 == 0.0) it.amount.toLong().toString() else it.amount.toString() 
        } ?: "")
    }

    var titleText by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.title ?: "")
    }

    var noteText by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.note ?: "")
    }

    var paymentMethod by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.paymentMethod ?: "現金")
    }

    var dateMillis by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.dateMillis ?: System.currentTimeMillis())
    }

    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("add_edit_transaction_sheet")
            ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (editingTransaction == null) "新增帳務紀錄" else "編輯帳務紀錄",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "關閉",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type Toggle (Expense vs Income)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                val expenseBg by animateColorAsState(
                    targetValue = if (type == "EXPENSE") CoralExpense else Color.Transparent,
                    label = "expBg"
                )
                val incomeBg by animateColorAsState(
                    targetValue = if (type == "INCOME") MintIncome else Color.Transparent,
                    label = "incBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(expenseBg)
                        .clickable {
                            type = "EXPENSE"
                            selectedCategory = CategoryEntity.DEFAULT_EXPENSE_CATEGORIES.first()
                        }
                        .padding(vertical = 12.dp)
                        .testTag("type_expense_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "支出",
                        color = if (type == "EXPENSE") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(incomeBg)
                        .clickable {
                            type = "INCOME"
                            selectedCategory = CategoryEntity.DEFAULT_INCOME_CATEGORIES.first()
                        }
                        .padding(vertical = 12.dp)
                        .testTag("type_income_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "收入",
                        color = if (type == "INCOME") Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Amount Field
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("金額 (NT$)") },
                placeholder = { Text("0") },
                prefix = {
                    Text(
                        text = "$ ",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (type == "EXPENSE") CoralExpense else MintIncome
                        )
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (type == "EXPENSE") CoralExpense else MintIncome
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (type == "EXPENSE") CoralExpense else MintIncome,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category Picker Label
            Text(
                text = "選擇分類",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredCategories.forEach { cat ->
                    val isSelected = selectedCategory.id == cat.id
                    val catColor = Color(cat.colorHex)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) catColor else catColor.copy(alpha = 0.12f)
                            )
                            .border(
                                width = if (isSelected) 2.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = CategoryIconHelper.getIconVector(cat.iconName),
                                contentDescription = cat.name,
                                tint = if (isSelected) Color.White else catColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cat.name,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Date & Payment Method Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    cal.set(y, m, d)
                                    dateMillis = cal.timeInMillis
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }
                        .padding(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "日期",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dateFormatter.format(Date(dateMillis)),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Method selector chips
            Text(
                text = "支付 / 帳戶方式",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            val options = if (paymentMethods.isNotEmpty()) paymentMethods.sortedBy { it.sortOrder }.map { it.name }
                          else listOf("現金", "信用卡", "LINE Pay", "街口支付", "銀行轉帳", "悠遊卡")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { option ->
                    val isSel = paymentMethod == option
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (isSel) 1.5.dp else 0.dp,
                                color = if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { paymentMethod = option }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Note text fields
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("名稱 / 說明 (選填)") },
                placeholder = { Text("例: 午餐排骨飯、手搖飲") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("title_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("詳細備註 (選填)") },
                placeholder = { Text("發票號碼、店家名稱等...") },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_input")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val amountVal = amountText.toDoubleOrNull() ?: 0.0
                    if (amountVal > 0) {
                        onSave(
                            editingTransaction?.id ?: 0L,
                            titleText,
                            amountVal,
                            type,
                            selectedCategory,
                            dateMillis,
                            noteText,
                            paymentMethod
                        )
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_transaction_btn"),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == "EXPENSE") CoralExpense else MintIncome
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (editingTransaction == null) "儲存此筆紀錄" else "更新紀錄",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
}
