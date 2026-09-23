package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.CustomTypographyClass
import com.example.ui.theme.SignalGold

@Composable
fun CustomClassManagerDialog(
    customClasses: List<CustomTypographyClass>,
    onDismiss: () -> Unit,
    onAddClass: (CustomTypographyClass) -> Unit,
    onDeleteClass: (String) -> Unit,
    onInsertClass: (String) -> Unit,
    onAutoTypeClass: (String) -> Unit,
    onCopyCombinedCss: () -> Unit
) {
    var isAddingNew by remember { mutableStateOf(false) }
    var newClassName by remember { mutableStateOf("") }
    var newDisplayName by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var newCssRules by remember { mutableStateOf("") }
    var newTemplate by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .testTag("dialog_custom_classes"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SignalGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FormatPaint, contentDescription = null, tint = SignalGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "Custom Typography Classes",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "Define your own Blogspot post formatting classes",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar: Add new & Copy All CSS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ElevatedButton(
                        onClick = { isAddingNew = !isAddingNew },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = if (isAddingNew) MaterialTheme.colorScheme.secondaryContainer else SignalGold,
                            contentColor = if (isAddingNew) MaterialTheme.colorScheme.onSecondaryContainer else Color(0xFF1C2B2A)
                        ),
                        modifier = Modifier.weight(1f).height(36.dp).testTag("btn_toggle_add_class")
                    ) {
                        Icon(if (isAddingNew) Icons.Default.Close else Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isAddingNew) "Cancel Add" else "Add Custom Class", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onCopyCombinedCss,
                        modifier = Modifier.weight(1.2f).height(36.dp).testTag("btn_copy_combined_css")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Full Blogger CSS", fontSize = 11.sp, maxLines = 1)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Add New Class Form
                AnimatedVisibility(visible = isAddingNew) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Create New Typography Class", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newDisplayName,
                                    onValueChange = { newDisplayName = it },
                                    label = { Text("Display Name", fontSize = 11.sp) },
                                    placeholder = { Text("e.g. Glowing Quote Box", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = newClassName,
                                    onValueChange = {
                                        newClassName = it.trim().removePrefix(".").replace(" ", "-").lowercase()
                                    },
                                    label = { Text("CSS Class (.class)", fontSize = 11.sp) },
                                    placeholder = { Text("glow-box", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            OutlinedTextField(
                                value = newDescription,
                                onValueChange = { newDescription = it },
                                label = { Text("Description", fontSize = 11.sp) },
                                placeholder = { Text("Highlight callout for important takeaways", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = newCssRules,
                                onValueChange = { newCssRules = it },
                                label = { Text("CSS Rules", fontSize = 11.sp) },
                                placeholder = {
                                    Text("background: #fdf2e9;\nborder-left: 4px solid #e67e22;\npadding: 12px;\nborder-radius: 6px;", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )

                            OutlinedTextField(
                                value = newTemplate,
                                onValueChange = { newTemplate = it },
                                label = { Text("HTML Code Template (use {{text}})", fontSize = 11.sp) },
                                placeholder = {
                                    Text("<div class=\"custom-class\"><p>{{text}}</p></div>", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        if (newClassName.isNotBlank() && newDisplayName.isNotBlank()) {
                                            val template = newTemplate.ifBlank {
                                                "<div class=\"$newClassName\"><p>{{text}}</p></div>"
                                            }
                                            val css = newCssRules.ifBlank {
                                                ".$newClassName { padding: 12px; margin: 16px 0; border-radius: 6px; background: #f8fafc; }"
                                            }
                                            onAddClass(
                                                CustomTypographyClass(
                                                    id = "user_cls_${System.currentTimeMillis()}",
                                                    name = newDisplayName,
                                                    className = newClassName,
                                                    description = newDescription.ifBlank { "User custom typography class" },
                                                    cssRules = css,
                                                    htmlTemplate = template
                                                )
                                            )
                                            isAddingNew = false
                                            newDisplayName = ""
                                            newClassName = ""
                                            newDescription = ""
                                            newCssRules = ""
                                            newTemplate = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Color(0xFF1C2B2A)),
                                    modifier = Modifier.testTag("btn_save_custom_class")
                                ) {
                                    Text("Save Class", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 0.8.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                // List of Custom Classes
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customClasses, key = { it.id }) { item ->
                        CustomClassCard(
                            item = item,
                            onInsert = { onInsertClass(item.htmlTemplate.replace("{{text}}", "Your text goes here").replace("{{title}}", item.name)) },
                            onAutoType = { onAutoTypeClass(item.htmlTemplate.replace("{{text}}", "Your text goes here").replace("{{title}}", item.name)) },
                            onDelete = { onDeleteClass(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomClassCard(
    item: CustomTypographyClass,
    onInsert: () -> Unit,
    onAutoType: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(SignalGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = ".${item.className}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = SignalGold
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                }
            }

            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // HTML Template preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = item.htmlTemplate,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFCBD5E1),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilledTonalButton(
                    onClick = onInsert,
                    modifier = Modifier.weight(1f).height(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Insert Code", fontSize = 11.sp)
                }

                ElevatedButton(
                    onClick = onAutoType,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = SignalGold,
                        contentColor = Color(0xFF1C2B2A)
                    ),
                    modifier = Modifier.weight(1f).height(32.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto-Type", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
