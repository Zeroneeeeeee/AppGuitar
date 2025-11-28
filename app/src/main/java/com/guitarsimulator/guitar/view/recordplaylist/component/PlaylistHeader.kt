package com.guitarsimulator.guitar.view.recordplaylist.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guitarsimulator.guitar.R

@Composable
fun PlaylistHeader(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    title: String,
    isGuideline: Boolean = false,
    onExposeTutorial: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 24.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_back_navigate),
            contentDescription = "Back",
            modifier = Modifier
                .size(32.dp)
                .clickable(onClick = {
                    onBackClick()
                })
        )
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
        if (isGuideline) {
            Icon(
                painter = painterResource(R.drawable.ic_guideline),
                contentDescription = "Guideline",
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .clickable(onClick = onExposeTutorial)
            )
        }
    }
}