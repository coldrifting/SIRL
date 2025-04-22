package com.coldrifting.sirl.components

import android.view.View
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.TargetedFlingBehavior
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.view.HapticFeedbackConstantsCompat
import androidx.core.view.ViewCompat
import com.coldrifting.sirl.ui.theme.DelColor
import com.coldrifting.sirl.ui.theme.EditColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class SwipeToRevealValue { Left, Center, Right }

data class SwipeTapAction(val colorFg: Color,
                          val colorBg: Color,
                          val icon: ImageVector,
                          val action: (Int) -> Unit,
                          val snapBack: Boolean = false,
                          val desc: String = "")

fun swipeEditAction(action: (Int) -> Unit): SwipeTapAction {
    return SwipeTapAction(
        Color.White,
        EditColor,
        Icons.Default.Edit,
        {action.invoke(it)},
        true,
        "Edit"
    )
}

fun swipeDeleteAction(action: (Int) -> Unit): SwipeTapAction {
    return SwipeTapAction(
        Color.White,
        DelColor,
        Icons.Default.Delete,
        {action.invoke(it)},
        false,
        "Delete"
    )
}

@Composable
fun SwipeRevealItem(
    index: Int = -1,
    curIndex: MutableIntState? = null,
    leftAction: SwipeTapAction? = null,
    rightAction: SwipeTapAction? = null,
    cornerRadius: Dp = 0.dp,
    content: @Composable () -> Unit) {

    val animTime = 125
    val density: Density = LocalDensity.current
    val revealWidth: Dp = 72.dp
    val actionOffset: Float = with(density) { revealWidth.toPx() }

    val anchors: DraggableAnchors<SwipeToRevealValue> = remember {
        if (leftAction != null && rightAction != null) {
            DraggableAnchors {
                SwipeToRevealValue.Left at actionOffset
                SwipeToRevealValue.Center at 0f
                SwipeToRevealValue.Right at -actionOffset
            }
        }
        else if (leftAction != null) {
            DraggableAnchors {
                SwipeToRevealValue.Left at actionOffset
                SwipeToRevealValue.Center at 0f
            }
        }
        else if (rightAction != null) {
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

    val state: AnchoredDraggableState<SwipeToRevealValue> = remember {
        AnchoredDraggableState(
            initialValue = SwipeToRevealValue.Center,
            anchors = anchors
        )
    }

    val overScrollEffect: OverscrollEffect? = rememberOverscrollEffect()
    val flingBehavior: TargetedFlingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = state,
        positionalThreshold = { distance -> distance * 0.5f },
        animationSpec = snap()
    )

    // Ensure action buttons show behind content when disabled and are clickable when enabled
    val zOrder: Float = if (state.currentValue != SwipeToRevealValue.Center) 2f else 0f

    val color: Color = state.offset.let {
        when {
            it < 0.0f -> rightAction?.colorBg ?: Color.Transparent
            it > 0.0f -> leftAction?.colorBg ?: Color.Transparent
            else -> Color.Transparent
        }
    }

    val view: View = LocalView.current
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    var leftActionColorFg: State<Color> = animateColorAsState(leftAction?.colorFg ?: Color.Transparent, animationSpec = tween(animTime))
    if (leftAction?.snapBack == true && state.settledValue == SwipeToRevealValue.Left) {
        leftActionColorFg = animateColorAsState(Color.Transparent, animationSpec = tween(animTime))
        LaunchedEffect(key1 = "SwipeFromLeftFull") {
            coroutineScope.launch {
                state.animateTo(SwipeToRevealValue.Center, animationSpec = tween(animTime))

                ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_START)
                leftAction.action.invoke(index)
                ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_END)
            }
        }
    }
    var rightActionColorFg = animateColorAsState(rightAction?.colorFg ?: Color.Transparent, animationSpec = tween(animTime))
    if (rightAction?.snapBack == true && state.settledValue == SwipeToRevealValue.Right) {
        rightActionColorFg = animateColorAsState(Color.Transparent, animationSpec = tween(animTime))
        LaunchedEffect(key1 = "SwipeFromRightFull") {
            coroutineScope.launch {
                state.animateTo(SwipeToRevealValue.Center, animationSpec = tween(animTime))

                ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_START)
                rightAction.action.invoke(index)
                ViewCompat.performHapticFeedback(view, HapticFeedbackConstantsCompat.GESTURE_END)
            }
        }
    }

    // Update the index of the last swiped action
    LaunchedEffect(state) {
        snapshotFlow { state.settledValue }.collect { value ->
            if (value != SwipeToRevealValue.Center) {
                curIndex?.intValue = index
            }
        }
    }

    // Close any previous swipe actions
    if (curIndex != null) {
        LaunchedEffect(state) {
            snapshotFlow { curIndex.intValue }.collect { value ->
                if (value != index && state.settledValue != SwipeToRevealValue.Center) {
                    state.animateTo(SwipeToRevealValue.Center, animationSpec = tween(animTime))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(cornerRadius))
            .background(color)
    ) {
        // Main content that moves with the swipe
        Box(
            modifier = Modifier
                .zIndex(1f)
                .anchoredDraggable(
                    state,
                    Orientation.Horizontal,
                    overscrollEffect = overScrollEffect,
                    flingBehavior = flingBehavior
                )
                .overscroll(overScrollEffect)
                .offset {
                    IntOffset(
                        x = state.requireOffset().roundToInt(),
                        y = 0
                    )
                }
        ) {
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(cornerRadius))
            ) {
                content.invoke()
            }
        }

        Row(
            modifier = Modifier.matchParentSize()
                .zIndex(zOrder),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leftAction != null) {
                if (state.offset >= 0) {
                    IconButton(
                        modifier = Modifier.width(revealWidth),
                        onClick = {
                            coroutineScope.launch {
                                state.animateTo(SwipeToRevealValue.Center, TweenSpec(animTime))
                                leftAction.action.invoke(index)
                            }
                        }) {
                        Icon(
                            imageVector = leftAction.icon,
                            tint = leftActionColorFg.value,
                            contentDescription = leftAction.desc
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (rightAction != null) {
                if (state.offset <= 0) {
                    IconButton(
                        modifier = Modifier.width(revealWidth),
                        onClick = {
                            coroutineScope.launch {
                                state.animateTo(SwipeToRevealValue.Center, TweenSpec(animTime))
                                rightAction.action.invoke(index)
                            }
                        }) {
                        Icon(
                            imageVector = rightAction.icon,
                            tint = rightActionColorFg.value,
                            contentDescription = rightAction.desc
                        )
                    }
                }
            }

        }
    }
}