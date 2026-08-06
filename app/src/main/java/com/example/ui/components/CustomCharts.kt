package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CategoryStat
import com.example.ui.DailyTrendItem
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.MintIncome
import java.util.Locale
import kotlin.math.atan2

@Composable
fun CategoryDonutChart(
    categoryStats: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    if (categoryStats.isEmpty()) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "本月尚無支出分類數據",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val totalAmount = categoryStats.sumOf { it.amount }

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "donutAnim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_donut_chart"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "本月開銷分類佔比",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Donut
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(190.dp)
                        .pointerInput(categoryStats) {
                            detectTapGestures { tapOffset ->
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val dx = tapOffset.x - center.x
                                val dy = tapOffset.y - center.y
                                var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                if (angle < 0) angle += 360f

                                // Convert angle starting from top (-90 deg -> 270 deg)
                                var currentAngle = 270f
                                categoryStats.forEachIndexed { index, stat ->
                                    val sweep = (stat.percentage / 100f) * 360f * animProgress
                                    val start = currentAngle % 360f
                                    val end = (currentAngle + sweep) % 360f
                                    
                                    val inRange = if (start < end) {
                                        angle >= start && angle <= end
                                    } else {
                                        angle >= start || angle <= end
                                    }

                                    if (inRange) {
                                        selectedIndex = if (selectedIndex == index) null else index
                                        return@detectTapGestures
                                    }
                                    currentAngle += sweep
                                }
                            }
                        }
                ) {
                    val strokeWidth = 36.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                    val arcSize = Size(diameter, diameter)

                    var startAngle = -90f

                    categoryStats.forEachIndexed { index, stat ->
                        val sweepAngle = (stat.percentage / 100f) * 360f * animProgress
                        val isSelected = selectedIndex == index
                        val color = Color(stat.colorHex)

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 2f, // slice gap
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(
                                width = if (isSelected) strokeWidth + 12.dp.toPx() else strokeWidth,
                                cap = StrokeCap.Round
                            )
                        )
                        startAngle += sweepAngle
                    }
                }

                // Center Label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val selectedStat = selectedIndex?.let { categoryStats.getOrNull(it) }
                    if (selectedStat != null) {
                        Text(
                            text = selectedStat.categoryName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%,.0f", selectedStat.amount)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(selectedStat.colorHex)
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.1f", selectedStat.percentage)}%",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = "總支出",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$${String.format(Locale.US, "%,.0f", totalAmount)}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Category Legend List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryStats.forEachIndexed { index, stat ->
                    val isSelected = selectedIndex == index
                    val categoryColor = Color(stat.colorHex)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                else Color.Transparent
                            )
                            .clickable {
                                selectedIndex = if (isSelected) null else index
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(categoryColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIconVector(stat.iconName),
                                contentDescription = stat.categoryName,
                                tint = categoryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = stat.categoryName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${String.format(Locale.US, "%.1f", stat.percentage)}%",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "$${String.format(Locale.US, "%,.0f", stat.amount)}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyExpenseBarChart(
    dailyItems: List<DailyTrendItem>,
    modifier: Modifier = Modifier
) {
    if (dailyItems.isEmpty()) return

    val maxExpense = dailyItems.maxOfOrNull { it.expenseAmount }?.coerceAtLeast(100.0) ?: 100.0
    var selectedDay by remember { mutableStateOf<DailyTrendItem?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_expense_bar_chart"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "每日支出趨勢",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                selectedDay?.let { day ->
                    Text(
                        text = "${day.dateLabel}: $${String.format(Locale.US, "%,.0f", day.expenseAmount)}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = CoralExpense
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bars Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .pointerInput(dailyItems) {
                            detectTapGestures { offset ->
                                val barWidth = size.width / dailyItems.size
                                val index = (offset.x / barWidth).toInt().coerceIn(0, dailyItems.size - 1)
                                selectedDay = dailyItems.getOrNull(index)
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val barWidth = (width / dailyItems.size) * 0.65f
                    val spacing = (width / dailyItems.size) * 0.35f

                    dailyItems.forEachIndexed { i, item ->
                        val x = i * (barWidth + spacing) + spacing / 2
                        val ratio = (item.expenseAmount / maxExpense).toFloat()
                        val barHeight = (height * ratio).coerceAtLeast(4.dp.toPx())
                        val y = height - barHeight

                        val isSelected = selectedDay?.dayOfMonth == item.dayOfMonth
                        val brush = if (isSelected) {
                            Brush.verticalGradient(listOf(CoralExpense, Color(0xFFFF85A1)))
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    CoralExpense.copy(alpha = 0.8f),
                                    CoralExpense.copy(alpha = 0.3f)
                                )
                            )
                        }

                        drawRoundRect(
                            brush = brush,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // X-Axis sample dates (e.g. 1st, 10th, 20th, 30th)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val sampleDays = listOf(1, 10, 20, dailyItems.lastOrNull()?.dayOfMonth ?: 30)
                sampleDays.forEach { d ->
                    Text(
                        text = "${d}日",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
