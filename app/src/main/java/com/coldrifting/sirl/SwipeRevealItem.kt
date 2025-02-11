package com.coldrifting.sirl

import android.util.Log
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.coldrifting.sirl.ui.theme.DelColor
import com.coldrifting.sirl.ui.theme.PinColor
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SwipeToRevealValue { Left, Center, Right }

@Composable
fun SwipeRevealItem(onLeftAction: (() -> Unit)? = null, onRightAction: (() -> Unit)? = null, content: @Composable () -> Unit) {

    val density = LocalDensity.current
    val revealWidth = 72.dp
    val actionOffset = with(density) { revealWidth.toPx() }
    val anchors: DraggableAnchors<SwipeToRevealValue> = remember {
        if (onLeftAction != null && onRightAction != null) {
            DraggableAnchors {
                SwipeToRevealValue.Left at actionOffset
                SwipeToRevealValue.Center at 0f
                SwipeToRevealValue.Right at -actionOffset
            }
        }
        else if (onLeftAction != null) {
            DraggableAnchors {
                SwipeToRevealValue.Left at actionOffset
                SwipeToRevealValue.Center at 0f
            }
        }
        else if (onRightAction != null) {
            DraggableAnchors {
                SwipeToRevealValue.Center at 0f
                SwipeToRevealValue.Right at -actionOffset
            }
        }
        else {
            DraggableAnchors {
                SwipeToRevealValue.Center at 0f
            }
        }
    }

    val dragState = remember {
        AnchoredDraggableState(
            initialValue = SwipeToRevealValue.Center,
            anchors = anchors
        )
    }

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = dragState,
        positionalThreshold = { distance -> distance * 0.5f },
        animationSpec = snap()
    )

    val overScrollEffect = rememberOverscrollEffect()

    val color: Color = dragState.offset.let {
        when {
            it < 0.0f -> DelColor
            it > 0.0f -> PinColor
            else -> Color.Transparent
        }
    }

    val coroutineScope = rememberCoroutineScope()

    if (dragState.settledValue == SwipeToRevealValue.Left) {
        LaunchedEffect(key1 = "SwipeFromLeftFull") {
            coroutineScope.launch {
                dragState.animateTo(SwipeToRevealValue.Center, animationSpec = tween(60))
                Log.d("TEST", "Settled Left")
                onLeftAction?.invoke()
            }
        }
    }

    val zOrder: Float = if (dragState.offset >= actionOffset || dragState.offset <= -actionOffset) 2f else 0f


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
    ) {
        // Main content that moves with the swipe
        Box(
            modifier = Modifier
                .zIndex(1f)
                .anchoredDraggable(
                    dragState,
                    Orientation.Horizontal,
                    overscrollEffect = overScrollEffect,
                    flingBehavior = flingBehavior
                )
                .overscroll(overScrollEffect)
                .offset {
                    IntOffset(
                        x = dragState.requireOffset().roundToInt(),
                        y = 0
                    )
                }
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.inverseOnSurface)) {
                content.invoke()
            }
        }

        // actions for "Read" and "Delete"
        Row(
            modifier = Modifier.matchParentSize()
                .zIndex(zOrder),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Read Action
            if (dragState.offset >= 0) {
                IconButton(
                    modifier = Modifier.width(revealWidth),
                    onClick = {
                        coroutineScope.launch {
                            dragState.animateTo(SwipeToRevealValue.Center, TweenSpec(250))
                        }
                        onLeftAction?.invoke()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Read"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Delete Action
            if (dragState.offset <= 0) {
                IconButton(
                    modifier = Modifier.width(revealWidth),
                    onClick = {
                        coroutineScope.launch {
                            dragState.animateTo(SwipeToRevealValue.Center, TweenSpec(125))
                        }
                        onRightAction?.invoke()
                    }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }

        }
    }
}