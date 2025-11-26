package com.guitarsimulator.guitar.utils

import com.guitarsimulator.guitar.dtmodels.NoteEventVM

sealed interface Screen {
    data object Home : Screen
    data class Guitar(var listNote:List<NoteEventVM> = emptyList(), var isTutorial:Boolean = false) : Screen
    data object Setting : Screen
    data object Language : Screen
    //data object Tuner : Screen
    data class Playlist(var selectedTab:String = "", var isTabVisible:Boolean = true) : Screen
    data object Policy : Screen
}