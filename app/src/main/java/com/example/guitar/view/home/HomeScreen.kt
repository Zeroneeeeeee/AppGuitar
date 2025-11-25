package com.example.guitar.view.home

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guitar.R
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
    localizedContext: Context
) {
    val context = LocalContext.current
    val activity = context as? Activity
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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

}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    onSettingClick: () -> Unit = {},
    localizedContext: Context
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = localizedContext.resources.getString(R.string.menu_title), fontSize = 22.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.ic_setting),
            contentDescription = "Setting",
            modifier = Modifier.size(32.dp).clickable(onClick = onSettingClick)
        )
    }
}

@Composable
fun MenuGrid(
    modifier: Modifier = Modifier,
    onGuitarClick: () -> Unit = {},
    onLearnToPlayClick: () -> Unit = {},
    onListRecordClick: () -> Unit = {},
    localizedContext: Context
) {
    val menuItems = listOf(
        MenuItem(localizedContext.resources.getString(R.string.menu_item_1), R.drawable.ic_guitar),
        MenuItem(
            localizedContext.resources.getString(R.string.menu_item_2),
            R.drawable.ic_learn_to_play
        ),
        MenuItem(
            localizedContext.resources.getString(R.string.menu_item_3),
            R.drawable.ic_list_record
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(152.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),

        modifier = modifier.fillMaxWidth()
    ) {
        items(menuItems) { item ->
            MenuItemLayout(
                title = item.title,
                icon = item.icon,
                modifier = Modifier.requiredSize(152.dp),
                onClick = {
                    when (menuItems.indexOf(item)) {
                        0 -> onGuitarClick()
                        1 -> onLearnToPlayClick()
                        2 -> onListRecordClick()
                    }
                }
            )
        }
    }
}

@Composable
fun MenuItemLayout(
    modifier: Modifier = Modifier,
    title: String = "Guitar",
    icon: Int = R.drawable.ic_guitar,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(Color(0xFFFFDFA8), RoundedCornerShape(20.dp))
            .border(width = 2.dp, color = Color(0xFFFFBF51), shape = RoundedCornerShape(20.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "Guitar",
                tint = Color.Unspecified,
                modifier = Modifier.height(80.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(text = title, fontSize = 16.sp, textAlign = TextAlign.Center, color = Color.Black)
        }
    }
}