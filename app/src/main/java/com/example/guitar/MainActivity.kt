package com.example.guitar

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.preferencesDataStore
import com.example.guitar.ui.home.updateLocale
import com.example.guitar.ui.onboarding.OnboardingScreen
import com.example.guitar.ui.onboarding.UserPreferences
import com.example.guitar.ui.policy.PolicyScreen
import com.example.guitar.ui.recordplaylist.TutorialDialog
import com.example.guitar.ui.theme.GuitarTheme
import kotlinx.coroutines.launch
import java.security.Policy
import java.util.Locale

class MainActivity : ComponentActivity() {
    val Context.dataStore by preferencesDataStore(name = "user_prefs")
    private lateinit var userPrefs: UserPreferences
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        installSplashScreen()
        userPrefs = UserPreferences(dataStore)
        setContent {
            val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
            val navigationBarHeight = systemBarsPadding.calculateBottomPadding()
            val systemBarHeight = systemBarsPadding.calculateTopPadding()
            GuitarTheme {
                val context = LocalContext.current
                var language by remember { mutableStateOf(SharedPreference.getLanguage(context)?: "en") }
                var currentLocale by remember {
                    mutableStateOf(
                        Locale.Builder().setLanguage(language).build()
                    )
                }

                val localizedContext =
                    remember(currentLocale) { context.updateLocale(currentLocale) }
                Surface {
                    val onboardingDone by userPrefs.onboardingDone.collectAsState(initial = null)
                    val coroutine = rememberCoroutineScope()
                    when (onboardingDone) {
                        false -> {
                            OnboardingScreen(
                                onFinished = {
                                    coroutine.launch { userPrefs.setOnboardingDone() }
                                },
                                modifier = Modifier.padding(bottom = navigationBarHeight)
                            )
                        }
                        null -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                Column(){
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 4.dp,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Text(text = localizedContext.getString(R.string.loading), modifier = Modifier.align(Alignment.CenterHorizontally))
                                }
                            }
                        }
                        else -> {
                            Navigation(
                                modifier = Modifier,
                                paddingTop = systemBarHeight,
                                paddingBottom = navigationBarHeight,
                                localizedContext = localizedContext,
                                getLocale = {
                                    currentLocale = Locale.Builder().setLanguage(SharedPreference.getLanguage(context)?: "en").build()
                                    language = SharedPreference.getLanguage(context)?: "en"
                                },
                                language = language,
                                window = window
                            )
                            //TutorialDialog()
                          //  PolicyScreen(modifier = Modifier.padding(bottom =navigationBarHeight), localizedContext = localizedContext)
                        }
                    }
                    //PlayingGuitarScreen()
                    //GuitarTunerScreen()
                }
                //     AdaptiveTextField()
            }
        }
    }
}