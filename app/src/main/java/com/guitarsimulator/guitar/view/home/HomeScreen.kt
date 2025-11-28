package com.guitarsimulator.guitar.view.home

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.Window
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.guitarsimulator.guitar.view.home.component.ChanceManager
import com.guitarsimulator.guitar.view.home.component.Header
import com.guitarsimulator.guitar.view.home.component.MenuGrid
import com.guitarsimulator.guitar.view.home.component.QuickRateDialog
import java.util.Locale

data class MenuItem(
    val title: String,
    val icon: Int
)

fun Context.updateLocale(locale: Locale): Context {
    val config = Configuration(resources.configuration)
    Locale.setDefault(locale)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    toGuitarScreen: () -> Unit = {},
    toSettingScreen: () -> Unit = {},
    toListRecordScreen: () ->Unit = {},
    toLearnToPlayScreen: () -> Unit = {},
    window: Window,
    localizedContext: Context
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
    var showRateDialog by remember{ mutableStateOf(false) }
    LaunchedEffect(Unit) {
        windowInsertController.show(WindowInsetsCompat.Type.systemBars())
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ChanceManager.init(context)
        showRateDialog = ChanceManager.handleClick()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Header(
            modifier = Modifier.padding(bottom = 16.dp),
            onSettingClick = toSettingScreen,
            localizedContext = localizedContext
        )
        MenuGrid(
            modifier = Modifier.padding(vertical = 16.dp),
            onGuitarClick = toGuitarScreen,
            localizedContext = localizedContext,
            onListRecordClick = toListRecordScreen,
            onLearnToPlayClick = toLearnToPlayScreen
        )
    }
    if(showRateDialog){
        QuickRateDialog(onDispose = { showRateDialog = false })
    }
}

