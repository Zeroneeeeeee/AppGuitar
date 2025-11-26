package com.guitarsimulator.guitar

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.preferencesDataStore
import com.guitarsimulator.guitar.utils.Navigation
import com.guitarsimulator.guitar.utils.SharedPreference
import com.guitarsimulator.guitar.utils.UserPreferences
import com.guitarsimulator.guitar.view.home.updateLocale
import com.guitarsimulator.guitar.view.onboarding.OnboardingScreen
import com.guitarsimulator.guitar.view.theme.GuitarTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private lateinit var userPrefs: UserPreferences

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        userPrefs = UserPreferences(dataStore)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContent {
            val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
            val navigationBarHeight = systemBarsPadding.calculateBottomPadding()
            val systemBarHeight = systemBarsPadding.calculateTopPadding()
            GuitarTheme {
                val context = LocalContext.current
                var language by remember {
                    mutableStateOf(
                        SharedPreference.getLanguage(context) ?: "en"
                    )
                }
                var currentLocale by remember {
                    mutableStateOf(
                        Locale.Builder().setLanguage(language).build()
                    )
                }

                val localizedContext =
                    remember(currentLocale) { context.updateLocale(currentLocale) }
                Surface {
                    val coroutine = rememberCoroutineScope()
                    val onboardingDone by userPrefs.onboardingDone.collectAsState(initial = null)
                    var isLoading by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(5000)
                        isLoading = true
                    }

                    when{
                        onboardingDone == false && isLoading -> {
                            OnboardingScreen(
                                onFinished = {
                                    coroutine.launch { userPrefs.setOnboardingDone() }
                                },
                                modifier = Modifier.padding(bottom = navigationBarHeight)
                            )
                        }

                        !isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(R.drawable.img_logo),
                                        contentDescription = "Logo",
                                        modifier = Modifier.size(136.dp).clip(RoundedCornerShape(20.dp))
                                    )
                                    Spacer(modifier = Modifier.height(32.dp))
                                    TimedLinearProgress(5000)
                                }
                            }
                        }

                        onboardingDone == true && isLoading-> {
                            Navigation(
                                modifier = Modifier,
                                paddingTop = systemBarHeight,
                                paddingBottom = navigationBarHeight,
                                localizedContext = localizedContext,
                                getLocale = {
                                    currentLocale = Locale.Builder()
                                        .setLanguage(SharedPreference.getLanguage(context) ?: "en")
                                        .build()
                                    language = SharedPreference.getLanguage(context) ?: "en"
                                },
                                language = language,
                                window = window
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimedLinearProgress(durationMillis: Int = 2000) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis)
        ) { value, _ ->
            progress = value
        }
    }

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
    )
}

