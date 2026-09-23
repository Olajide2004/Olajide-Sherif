package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KeycapDark
import com.example.ui.theme.KeycapLight
import com.example.ui.theme.KeyedgeDark
import com.example.ui.theme.KeyedgeLight
import com.example.ui.theme.SignalGold

@Composable
fun TypewriterKey(
    label: String,
    modifier: Modifier = Modifier,
    subLabel: String? = null,
    isActive: Boolean = false,
    isSpecial: Boolean = false,
    minWidth: Dp = 32.dp,
    keyHeight: Dp = 44.dp,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val isDark = MaterialTheme.colorScheme.background.value == 0xFF141E1D.toULong()

    val defaultCapColor = if (isDark) KeycapDark else KeycapLight
    val edgeColor = if (isDark) KeyedgeDark else KeyedgeLight
    val activeCapColor = SignalGold

    val animatedCapColor by animateColorAsState(
        targetValue = when {
            isActive -> activeCapColor
            isPressed -> edgeColor
            isSpecial -> if (isDark) Color(0xFF1E2B29) else Color(0xFFE5DFC9)
            else -> defaultCapColor
        },
        animationSpec = tween(durationMillis = if (isActive) 120 else 60),
        label = "keycapColor"
    )

    val keyPressOffset by animateDpAsState(
        targetValue = if (isPressed || isActive) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 50),
        label = "keyPressOffset"
    )

    val textColor = when {
        isActive -> Color(0xFF1C2B2A)
        isDark -> Color(0xFFECE7DC)
        else -> Color(0xFF1C2B2A)
    }

    // Physical key structure: base shadow + bevel edge + raised face
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth)
            .height(keyHeight)
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .testTag("key_$label"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom bevel shadow (simulates physical keycap depth)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight - 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(edgeColor)
        )

        // Raised key face that depresses when pressed or active
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyHeight - 6.dp)
                .offset(y = keyPressOffset - 2.dp)
                .shadow(
                    elevation = if (isPressed || isActive) 0.dp else 1.5.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .clip(RoundedCornerShape(5.dp))
                .background(animatedCapColor)
                .border(
                    width = if (isActive) 1.5.dp else 0.5.dp,
                    color = if (isActive) Color(0xFFB88200) else edgeColor,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = if (label.length > 3) 11.sp else 15.sp,
                    fontWeight = if (isActive || isSpecial) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                if (subLabel != null && label.length <= 2) {
                    Text(
                        text = subLabel,
                        color = textColor.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 1.dp, top = 1.dp)
                    )
                }
            }
        }
    }
}
