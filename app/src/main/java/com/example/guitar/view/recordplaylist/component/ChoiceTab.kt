package com.example.guitar.view.recordplaylist.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guitar.R

class TabItem(val title: String, val icon: Int)

@Composable
fun ChoiceTab(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    getSelectedTab: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(localizedContext.resources.getString(R.string.guitar_record)) }
    val tabs = listOf(
        TabItem(
            title = localizedContext.resources.getString(R.string.guitar_record),
            icon = R.drawable.ic_guitar_small
        ),
        TabItem(
            title = localizedContext.resources.getString(R.string.tutorial),
            icon = R.drawable.ic_tutorial
        )
    )
    Column(
        modifier = modifier.width(IntrinsicSize.Max)
    ) {
        tabs.forEach { tab ->
            TabItem(
                tab = tab,
                modifier = Modifier.fillMaxWidth(),
                onClick = {

                    selectedTab = tab.title
                    getSelectedTab(selectedTab)
                },
                isSelected = selectedTab == tab.title
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun TabItem(
    modifier: Modifier = Modifier,
    tab: TabItem,
    isSelected: Boolean = false,
    onClick: (String) -> Unit = {}
) {
    Column(
        modifier = modifier
            .background(
                if (isSelected) Color(0xFFFFBF51) else Color(0xFFFFDFA8),
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = { onClick(tab.title) })
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(tab.icon),
            contentDescription = tab.title,
            tint = if (isSelected) Color.Black else Color(0xFFFFBF51),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = tab.title,
            fontSize = 16.sp,
            color = if (isSelected) Color.Black else Color(0xFFFFBF51),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}