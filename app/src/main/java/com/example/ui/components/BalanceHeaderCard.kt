package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.GradientEnd
import com.example.ui.theme.GradientStart
import com.example.ui.theme.MintIncome
import java.util.Locale

@Composable
fun BalanceHeaderCard(
    selectedMonthStr: String,
    totalIncome: Double,
    totalExpense: Double,
    netBalance: Double,
    monthlyBudgetLimit: Double = 30000.0,
    onUpdateBudgetLimit: (Double) -> Unit = {},
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBudgetEditDialog by remember { mutableStateOf(false) }

    val gradient = Brush.linearGradient(
        colors = listOf(
            GradientStart,
            Color(0xFF8B5CF6),
            GradientEnd
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .animateContentSize()
            .testTag("balance_header_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Top Row: Month Selector (Left) & Budget Quick Badge (Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Month Switcher Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = onPreviousMonth,
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("prev_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "上個月",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "月份選擇",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedMonthStr,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        )

                        IconButton(
                            onClick = onNextMonth,
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("next_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "下個月",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // Quick Budget Trigger Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { showBudgetEditDialog = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("budget_card_section")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "預算設定",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (monthlyBudgetLimit > 0) "預算 $${String.format(Locale.US, "%,.0f", monthlyBudgetLimit)}" else "設預算",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "修改預算",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(14.dp)
                                .testTag("edit_budget_btn")
                        )
                    }
                }

                // Main Content Row (Horizontal Split: Left Balance, Right Income/Expense Pills)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column: Net Balance
                    Column(
                        modifier = Modifier.weight(1.1f)
                    ) {
                        Text(
                            text = "本月淨結餘",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "NT$ ",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = String.format(Locale.US, "%,.0f", netBalance),
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 26.sp
                                )
                            )
                        }
                    }

                    // Right Column: Income & Expense Summary Side-by-Side
                    Row(
                        modifier = Modifier.weight(1.2f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Income Mini Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(MintIncome),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = "收入",
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "收入",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$${String.format(Locale.US, "%,.0f", totalIncome)}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }

                        // Expense Mini Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(CoralExpense),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = "支出",
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "支出",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$${String.format(Locale.US, "%,.0f", totalExpense)}",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Bottom Budget Progress Line & Status
                if (monthlyBudgetLimit > 0) {
                    val remainingBudget = monthlyBudgetLimit - totalExpense
                    val remainingPercentage = ((remainingBudget / monthlyBudgetLimit) * 100).coerceIn(-100.0, 100.0)
                    val usedPercentage = (totalExpense / monthlyBudgetLimit).coerceIn(0.0, 1.0).toFloat()
                    val isOverBudget = remainingBudget < 0

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isOverBudget) {
                                    "超支 $${String.format(Locale.US, "%,.0f", -remainingBudget)}"
                                } else {
                                    "預算剩餘 $${String.format(Locale.US, "%,.0f", remainingBudget)}"
                                },
                                color = if (isOverBudget) CoralExpense else Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )

                            Text(
                                text = if (isOverBudget) {
                                    "已超額 ${String.format(Locale.US, "%.0f", -remainingPercentage)}%"
                                } else {
                                    "剩餘 ${String.format(Locale.US, "%.0f", remainingPercentage)}%"
                                },
                                color = if (isOverBudget) CoralExpense else if (remainingPercentage < 20.0) Color(0xFFFFB74D) else MintIncome,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { usedPercentage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(CircleShape),
                            color = when {
                                usedPercentage >= 1.0f -> CoralExpense
                                usedPercentage > 0.8f -> Color(0xFFFFB74D)
                                else -> MintIncome
                            },
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                    }
                }
            }
        }
    }

    if (showBudgetEditDialog) {
        var inputVal by remember {
            mutableStateOf(if (monthlyBudgetLimit > 0) monthlyBudgetLimit.toInt().toString() else "30000")
        }
        AlertDialog(
            onDismissRequest = { showBudgetEditDialog = false },
            title = { Text("設定每月預算上限") },
            text = {
                Column {
                    Text(
                        text = "設定控制每月支出上限，即時掌握剩餘金額與預算百分比。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = inputVal,
                        onValueChange = { inputVal = it.filter { char -> char.isDigit() } },
                        label = { Text("每月預算金額 (NT$)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_input_field")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amount = inputVal.toDoubleOrNull() ?: 0.0
                        onUpdateBudgetLimit(amount)
                        showBudgetEditDialog = false
                    },
                    modifier = Modifier.testTag("save_budget_btn")
                ) {
                    Text("儲存設定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetEditDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
