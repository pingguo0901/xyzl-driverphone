package com.xyzl.driverphone.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.xyzl.driverphone.LocalAppColors
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private val SegTrack: Color @Composable get() = LocalAppColors.current.surfaceVariant
private val SegBorder: Color @Composable get() = LocalAppColors.current.border
private val SegInk: Color @Composable get() = LocalAppColors.current.ink
private val SegMuted: Color @Composable get() = LocalAppColors.current.muted

/** 胶囊式左右选项：整体一个胶囊（不是各自独立胶囊），选中项有白色滑块跟随 */
@Composable
fun SegmentedTabs(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val itemOffsets = remember { mutableStateMapOf<Int, IntOffset>() }
    val itemWidths = remember { mutableStateMapOf<Int, Int>() }

    val targetX = itemOffsets[selectedIndex]?.x ?: 0
    val targetW = itemWidths[selectedIndex] ?: 0

    val animatedX by animateDpAsState(
        targetValue = with(density) { targetX.toDp() },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "segX",
    )
    val animatedW by animateDpAsState(
        targetValue = with(density) { targetW.toDp() },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "segW",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(50))
            .background(SegTrack)
            .border(1.dp, SegBorder, RoundedCornerShape(50))
            .padding(4.dp),
    ) {
        // 白色滑块：跟随当前选项
        Box(
            modifier = Modifier
                .offset(x = animatedX)
                .width(animatedW)
                .fillMaxHeight()
                .shadow(2.dp, RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(LocalAppColors.current.background),
        )

        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            options.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .onGloballyPositioned { coords ->
                            itemOffsets[index] =
                                IntOffset(coords.positionInParent().x.roundToInt(), 0)
                            itemWidths[index] = coords.size.width
                        }
                        .clip(RoundedCornerShape(50))
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (selected) SegInk else SegMuted,
                    )
                }
            }
        }
    }
}
