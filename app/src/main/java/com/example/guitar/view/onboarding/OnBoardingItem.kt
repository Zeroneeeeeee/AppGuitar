package com.example.guitar.view.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guitar.R

@Composable
fun OnBoardingItem(modifier: Modifier = Modifier, onboardingPage: OnboardingPage) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(onboardingPage.image),
            contentDescription = "Onboarding Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFFFFBF51),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(onboardingPage.title)
                }
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append(onboardingPage.description)
                }
            },
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.baloo_bhai_font)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp, start = 48.dp, end = 48.dp)
        )
    }
}