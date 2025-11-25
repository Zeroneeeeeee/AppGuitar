package com.example.guitar.view.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.guitar.R
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val image: Int
)

val onboardingPages = listOf(
    OnboardingPage("LEARN ", "guitar the easy way", R.drawable.img_onboarding),
    OnboardingPage("TUNE", "your guitar to your liking", R.drawable.png_onboarding_2)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(
        Modifier.fillMaxSize()
    ) {
        HorizontalPager(state = pagerState) { page ->
            OnBoardingItem(onboardingPage = onboardingPages[page])
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { onFinished() }) {
                Text("Skip")
            }

            Button(onClick = {
                if (pagerState.currentPage == onboardingPages.lastIndex) {
                    onFinished()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            }) {
                Text(if (pagerState.currentPage == onboardingPages.lastIndex) "Start" else "Next")
            }
        }
    }
}
