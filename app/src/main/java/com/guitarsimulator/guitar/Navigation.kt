package com.guitarsimulator.guitar

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.guitarsimulator.guitar.utils.Screen
import com.guitarsimulator.guitar.utils.UserPreferences
import com.guitarsimulator.guitar.view.home.HomeScreen
import com.guitarsimulator.guitar.view.home.updateLocale
import com.guitarsimulator.guitar.view.playingguitar.PlayingGuitarScreen
import com.guitarsimulator.guitar.view.policy.PolicyScreen
import com.guitarsimulator.guitar.view.recordplaylist.RecordPlaylistScreen
import com.guitarsimulator.guitar.view.setting.LanguageScreen
import com.guitarsimulator.guitar.view.setting.SettingScreen
import com.guitarsimulator.guitar.view.setting.languages
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    paddingTop: Dp,
    paddingBottom: Dp,
    window: Window,
    localizedContext: Context,
    getLocale: (String) -> Unit,
    language: String
) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
    val activity = LocalActivity.current
    if (backStack.isEmpty()) {
        DisposableEffect(Unit) {
            activity?.finish()
            onDispose {
            }
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Screen.Home> {
                HomeScreen(
                    toGuitarScreen = {
                        windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
                        windowInsertController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        backStack.add(Screen.Guitar())
                    },
                    toSettingScreen = {
                        backStack.add(Screen.Setting)
                    },
                    toListRecordScreen = {
                        windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
                        windowInsertController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        backStack.add(
                            Screen.Playlist(
                                selectedTab = localizedContext.resources.getString(
                                    R.string.guitar_record
                                ), isTabVisible = false
                            )
                        )
                    },
                    toLearnToPlayScreen = {
                        windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
                        windowInsertController.systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                        backStack.add(
                            Screen.Playlist(
                                selectedTab = localizedContext.resources.getString(
                                    R.string.tutorial
                                ), isTabVisible = false
                            )
                        )
                    },
                    localizedContext = localizedContext,
                    window = window,
                    modifier = Modifier.padding(top = paddingTop+16.dp, bottom = paddingBottom)
                )
            }
            entry<Screen.Guitar> { (listNotes, tutorial) ->
                PlayingGuitarScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                        windowInsertController.show(WindowInsetsCompat.Type.systemBars())
                    },
//                    toTunerClick = {
//                        backStack.add(Screen.Tuner)
//                    },
                    toPlaylist = {
                        backStack.add(
                            Screen.Playlist(
                                selectedTab = localizedContext.resources.getString(
                                    R.string.guitar_record
                                )
                            )
                        )
                    },
                    window = window,
                    listNotes = listNotes,
                    isTutorial = tutorial,
                    localizedContext = localizedContext
                )
            }
            entry<Screen.Setting> {
                SettingScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    toLanguageScreen = {
                        backStack.add(Screen.Language)
                    },
                    toPolicyScreen = {
                        backStack.add(Screen.Policy)
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = paddingTop+16.dp, bottom = paddingBottom),
                    localizedContext = localizedContext,
                    language = languages.find { it.id == language }!!.language
                )
            }
            entry<Screen.Language> {
                LanguageScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(top = paddingTop +16.dp, bottom = paddingBottom),
                    localizedContext = localizedContext,
                    getLocale = getLocale,
                    language = language
                )
            }
//            entry<Screen.Tuner>{
//                GuitarTunerScreen()
//            }
            entry<Screen.Playlist> { (selectedTab, isTabVisible) ->
                RecordPlaylistScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    toTutorial = {
                        if (isTabVisible) {
                            backStack.removeLastOrNull()
                        }
                        backStack[backStack.lastIndex] =
                            Screen.Guitar(listNote = it, isTutorial = true)
                        Toast.makeText(
                            localizedContext,
                            localizedContext.getString(R.string.press_on_green_area_to_play),
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    selectedTab = selectedTab,
                    isTabVisible = isTabVisible,
                    localizedContext = localizedContext,
                    modifier = Modifier,
                    window = window,
                    toGuitarScreen = {
                        if (isTabVisible) backStack.removeLastOrNull()
                        else backStack[backStack.lastIndex] = Screen.Guitar()
                    }
                )
            }
            entry<Screen.Policy> {
                PolicyScreen(
                    localizedContext = localizedContext,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.padding(top = paddingTop, bottom = paddingBottom)
                )
            }
        }
    )
}