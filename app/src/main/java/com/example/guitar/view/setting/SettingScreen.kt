package com.example.guitar.view.setting

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.guitar.R

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    toLanguageScreen: () -> Unit = {},
    toPolicyScreen:()-> Unit = {},
    localizedContext: Context,
    language: String
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Header(
            title = localizedContext.resources.getString(R.string.settings),
            onBack = onBack,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = localizedContext.resources.getString(R.string.general),
            fontSize = 18.sp,
            fontWeight = Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        GeneralChoices(
            onSelectLanguage = toLanguageScreen,
            onSelectPolicy = toPolicyScreen,
            localizedContext = localizedContext,
            language = language
        )
    }
}

@Composable
fun Header(
    modifier: Modifier = Modifier,
    title: String = "Settings",
    onBack: () -> Unit = {}
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(R.drawable.ic_back_navigate),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterStart)
                .clickable(onClick = onBack),
        )
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 22.sp,
            fontWeight = Bold
        )
    }
}

class General(
    val icon: Int = R.drawable.ic_launcher_background,
    val title: String = "Title",
    val trailing: String = "Trailing",
    val hasTrailing: Boolean = false
)

@Composable
fun GeneralChoices(
    modifier: Modifier = Modifier,
    onSelectLanguage: () -> Unit = {},
    onSelectPolicy: () -> Unit = {},
    localizedContext: Context,
    language: String
) {
    val context = LocalContext.current
    val generalList = listOf(
        General(
            R.drawable.ic_language,
            localizedContext.resources.getString(R.string.language),
            language,
            hasTrailing = true
        ),
        General(R.drawable.ic_mail, localizedContext.resources.getString(R.string.mail), "Light"),
        General(
            R.drawable.ic_share,
            localizedContext.resources.getString(R.string.share_this_app),
            "On"
        ),
        General(
            R.drawable.ic_feedback,
            localizedContext.resources.getString(R.string.feedback),
            ""
        ),
        General(
            R.drawable.ic_privacy,
            localizedContext.resources.getString(R.string.privacy_policy),
            ""
        ),
    )
    Column(modifier = modifier) {
        generalList.forEach { general ->
            GeneralItem(
                icon = general.icon,
                title = general.title,
                trailing = general.trailing,
                hasTrailing = general.hasTrailing,
                onItemClick = {
                    when (general.title) {
                        localizedContext.resources.getString(R.string.language) -> {
                            onSelectLanguage()
                        }

                        localizedContext.resources.getString(R.string.mail) -> {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:store@gambi.global".toUri()
                                setPackage("com.google.android.gm") // ép mở Gmail
                                putExtra(Intent.EXTRA_SUBJECT, "Guitar App Feedback")
                            }

                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        "https://play.google.com/store/apps/details?id=com.google.android.gm".toUri()
                                    )
                                )
                            }
                        }

                        localizedContext.resources.getString(R.string.share_this_app) -> {
                            // Handle share this app selection
                        }

                        localizedContext.resources.getString(R.string.feedback) -> {
                            // Handle feedback selection
                        }

                        localizedContext.resources.getString(R.string.privacy_policy) -> {
                            val url = "https://gambi-publishing-app.web.app/privacy-policy.html"
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = url.toUri()
                            }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context,
                                    localizedContext.getString(R.string.no_browser_found), Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun GeneralItem(
    modifier: Modifier = Modifier,
    hasTrailing: Boolean = true,
    icon: Int = R.drawable.ic_launcher_background,
    title: String = "Language",
    trailing: String = "English",
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onItemClick)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title
        )
        Spacer(modifier = Modifier.weight(1f))
        if (hasTrailing) {
            Text(
                text = trailing,
                color = Color(0xFFBDBBBE)
            )
            Icon(
                painter = painterResource(R.drawable.ic_next_navigate),
                contentDescription = null,
                tint = Color(0xFFBDBBBE),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

