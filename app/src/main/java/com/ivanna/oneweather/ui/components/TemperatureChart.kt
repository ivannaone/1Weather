package com.ivanna.oneweather.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TemperatureChart(
    temperatures: List<Double>,
    times: List<String>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    if (temperatures.isEmpty() || times.isEmpty()) return

    val count = minOf(temperatures.size, times.size)
    val values = temperatures.take(count)
    val labels = times.take(count)

    val minTemp = (values.minOrNull() ?: 0.0) - 2
    val maxTemp = (values.maxOrNull() ?: 0.0) + 2
    val range = (maxTemp - minTemp).coerceAtLeast(1.0)

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Perubahan Suhu",
            fontSize = 18.sp,
            color = colors.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            val spacing = size.width / (count - 1).coerceAtLeast(1)

            val points = values.mapIndexed { index, temperature ->
                Offset(
                    x = spacing * index,
                    y = size.height -
                            ((temperature - minTemp) / range * size.height).toFloat()
                )
            }

            for (i in 0 until count - 1) {
                drawLine(
                    color = colors.outlineVariant,
                    start = Offset(points[i].x, size.height),
                    end = Offset(points[i + 1].x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach {
                    lineTo(it.x, it.y)
                }
            }

            drawPath(
                path = path,
                color = colors.primary,
                style = Stroke(width = 3.dp.toPx())
            )

            points.forEach {
                drawCircle(
                    color = colors.primary,
                    radius = 5.dp.toPx(),
                    center = it
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = labels.first().substringAfter("T"),
                fontSize = 11.sp,
                color = colors.onSurfaceVariant
            )

            Text(
                text = labels.last().substringAfter("T"),
                fontSize = 11.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}