package com.guitarsimulator.guitar.view.policy

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guitarsimulator.guitar.R
import com.guitarsimulator.guitar.view.setting.Header

@Composable
fun PolicyScreen(
    modifier: Modifier = Modifier,
    localizedContext: Context,
    onBack: () -> Unit
) {
    val LINK_PRIVACY = "https://gambi-publishing-app.web.app/privacy-policy.html"
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Header(
            title = localizedContext.getString(R.string.policy),
            onBack = onBack,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(modifier = Modifier) {
            item {
                Text(
                    text = localizedContext.getString(R.string.terms_of_use),
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}