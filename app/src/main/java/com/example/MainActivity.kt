package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.screens.DraftsScreen
import com.example.ui.screens.GitHubScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SnippetsScreen
import com.example.ui.theme.MyApplicationTheme

enum class NavigationDestination(val label: String, val icon: ImageVector) {
    COMPOSER("Composer", Icons.Default.Edit),
    GITHUB("GitHub", Icons.Default.Language),
    SNIPPETS("Snippets", Icons.Default.AutoAwesome),
    DRAFTS("Drafts", Icons.Default.Description),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Blogger Keyboard",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            windowInsets = WindowInsets.navigationBars,
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationDestination.values().forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(text = item.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTabIndex) {
                            0 -> HomeScreen(
                                viewModel = viewModel,
                                onNavigateToGitHub = { selectedTabIndex = 1 },
                                onNavigateToSnippets = { selectedTabIndex = 2 }
                            )
                            1 -> GitHubScreen(
                                viewModel = viewModel,
                                onOpenComposerWithText = { title, content ->
                                    viewModel.loadMarkdownIntoEditor(title, content)
                                    selectedTabIndex = 0
                                }
                            )
                            2 -> SnippetsScreen(
                                viewModel = viewModel,
                                onInsertIntoComposer = { text ->
                                    viewModel.editorContent.value += "\n" + text
                                    selectedTabIndex = 0
                                }
                            )
                            3 -> DraftsScreen(
                                viewModel = viewModel,
                                onOpenDraft = { draft ->
                                    viewModel.openDraftInEditor(draft)
                                    selectedTabIndex = 0
                                },
                                onNewPost = {
                                    viewModel.editorTitle.value = "New Post"
                                    viewModel.editorContent.value = "# "
                                    viewModel.currentEditingDraftId.value = 0
                                    selectedTabIndex = 0
                                }
                            )
                            4 -> SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
