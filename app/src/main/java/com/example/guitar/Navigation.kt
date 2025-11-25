package com.example.guitar

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.guitar.ui.home.HomeScreen
import com.example.guitar.ui.playingguitar.PlayingGuitarScreen
import com.example.guitar.ui.policy.PolicyScreen
import com.example.guitar.ui.recordplaylist.RecordPlaylistScreen
import com.example.guitar.ui.setting.LanguageScreen
import com.example.guitar.ui.setting.SettingScreen
import com.example.guitar.ui.setting.languages

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
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
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Home> {
                HomeScreen(
                    toGuitarScreen = {
                        backStack.add(Screen.Guitar())
                    },
                    toSettingScreen = {
                        backStack.add(Screen.Setting)
                    },
                    toListRecordScreen = {
                        backStack.add(
                            Screen.Playlist(
                                selectedTab = localizedContext.resources.getString(
                                    R.string.guitar_record
                                ), isTabVisible = false
                            )
                        )
                    },
                    toLearnToPlayScreen = {
                        backStack.add(
                            Screen.Playlist(
                                selectedTab = localizedContext.resources.getString(
                                    R.string.tutorial
                                ), isTabVisible = false
                            )
                        )
                    },
                    localizedContext = localizedContext,
                    modifier = Modifier.padding(top = paddingTop, bottom = paddingBottom)
                )
            }
            entry<Screen.Guitar> { (listNotes, tutorial) ->
                PlayingGuitarScreen(
                    onBack = {
                        backStack.removeLastOrNull()
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
                        .background(Color(0xFFF9F9F9))
                        .padding(top = paddingTop, bottom = paddingBottom),
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
                        .background(Color(0xFFF9F9F9))
                        .padding(top = paddingTop, bottom = paddingBottom),
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
                        if(isTabVisible) {
                            backStack.removeLastOrNull()
                        }
                            Log.d("TAG", "${backStack[backStack.lastIndex]}")
                        backStack[backStack.lastIndex] = Screen.Guitar(listNote = it, isTutorial = true)
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
                        if(isTabVisible) backStack.removeLastOrNull()
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