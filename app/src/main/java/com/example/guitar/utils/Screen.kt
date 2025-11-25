package com.example.guitar.utils

import com.example.guitar.dtmodels.NoteEventVM

sealed interface Screen {
    data object Home : Screen
    data class Guitar(var listNote:List<NoteEventVM> = emptyList(), var isTutorial:Boolean = false) : Screen
    data object Setting : Screen
    data object Language : Screen
    //data object Tuner : Screen
    data class Playlist(var selectedTab:String = "", var isTabVisible:Boolean = true) : Screen
    data object Policy : Screen
}