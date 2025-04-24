package com.coldrifting.sirl.ui.components.checklist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CollapsableHeader(
    title: String,
    indentLevel: Int = 1,
    isMainHeader: Boolean = false,
    expanded: Boolean = true,
    onClick: () -> Unit = {}
) {
    val padOffset = 16.dp
    val padHeight = if (isMainHeader) 16.dp else 12.dp

    val elevation = if (isMainHeader) 3.dp else 2.dp

    Surface(tonalElevation = elevation, shadowElevation = elevation) {
        Row(
            modifier = Modifier
                .clickable { onClick.invoke() }
                .padding(
                    start = 10.dp * indentLevel,
                    end = padOffset,
                    top = padHeight,
                    bottom = padHeight
                )
        ) {
            Text(
                text = title,
                fontSize = if (isMainHeader) 18.sp else 16.sp
            )

            Spacer(Modifier.weight(1f))

            val iconAngle by animateFloatAsState(targetValue = if (expanded == true) 0f else 90f)

            Icon(
                modifier = Modifier.rotate(iconAngle),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "ArrowDown"
            )
        }
    }
}