package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ime.KeyboardSetupManager
import com.example.ui.theme.SignalGold
import kotlinx.coroutines.delay

@Composable
fun KeyboardSetupBanner(
    onOpenSetup: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(KeyboardSetupManager.isKeyboardEnabled(context)) }
    var isSelected by remember { mutableStateOf(KeyboardSetupManager.isKeyboardSelected(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            isEnabled = KeyboardSetupManager.isKeyboardEnabled(context)
            isSelected = KeyboardSetupManager.isKeyboardSelected(context)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenSetup() }
            .testTag("banner_keyboard_setup"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled && isSelected) {
                Color(0xFF142B22)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled && isSelected) Color(0xFF2E7D32) else SignalGold.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            if (isEnabled && isSelected) Color(0xFF2E7D32).copy(alpha = 0.3f)
                            else SignalGold.copy(alpha = 0.2f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEnabled && isSelected) Icons.Default.CheckCircle else Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = if (isEnabled && isSelected) Color(0xFF81C784) else SignalGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isEnabled && isSelected) "Default Keyboard Active" else "Enable as Phone Keyboard",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isEnabled && isSelected) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isEnabled && isSelected) Color(0xFF2E7D32) else SignalGold,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (isEnabled && isSelected) "ACTIVE" else "SETUP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEnabled && isSelected) Color.White else Color(0xFF1C2B2A)
                            )
                        }
                    }
                    Text(
                        text = if (isEnabled && isSelected)
                            "Ready to auto-type in Chrome, Blogger, WhatsApp & all apps"
                        else
                            "Tap to set up system typewriter keyboard for all apps",
                        fontSize = 10.sp,
                        color = if (isEnabled && isSelected) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Setup",
                tint = if (isEnabled && isSelected) Color(0xFF81C784) else SignalGold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
