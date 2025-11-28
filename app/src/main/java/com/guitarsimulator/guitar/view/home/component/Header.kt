package com.guitarsimulator.guitar.view.home.component

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guitarsimulator.guitar.R

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
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = onSettingClick)
        )
    }
}