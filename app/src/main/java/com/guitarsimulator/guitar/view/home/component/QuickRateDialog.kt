package com.guitarsimulator.guitar.view.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guitarsimulator.guitar.R

@Preview(showBackground = true)
@Composable
fun QuickRateDialog(modifier: Modifier = Modifier, onDispose: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.75f))
            .clickable(
                indication = null,
                interactionSource = null,
                onClick = {}
            )
    ) {
        Box(
            modifier = modifier
                .align(Alignment.Center),
        ) {
            Content(
                onDispose = onDispose,
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            )

            Icon(
                painter = painterResource(R.drawable.ic_quick_rate),
                contentDescription = "Quick Rate",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-24).dp)
            )
        }
    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    onDispose: () -> Unit = {},
    getStar: (Int) -> Unit = {},
    star: Int = 3
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var stars by remember { mutableIntStateOf(-1) }
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Close",
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.End)
                .clickable(
                    onClick = {
                        onDispose()
                    }
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Do you like our app?",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Give us a quick rating so we know if you like it!",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(5) { index ->
                Icon(
                    painter = painterResource(if (index <= stars) R.drawable.ic_chosen_star else R.drawable.ic_star),
                    contentDescription = "Star",
                    tint = Color.Unspecified,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null,
                        onClick = { stars = index }
                    )
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}