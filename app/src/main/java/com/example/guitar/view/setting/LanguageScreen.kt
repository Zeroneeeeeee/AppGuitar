package com.example.guitar.view.setting

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guitar.R
import com.example.guitar.utils.SharedPreference

class Language(
    val icon: Int,
    val language: String,
    val id: String
)

val languages = listOf(
    Language(R.drawable.ic_us, "English", "en"),
    Language(R.drawable.ic_spain, "Español", "es"),
    Language(R.drawable.ic_korea, "한국어", "ko"),
    Language(R.drawable.ic_germany, "Deutsch", "de"),
    Language(R.drawable.ic_france, "Français", "fr"),
    Language(R.drawable.ic_canada, "Anglais", "ca"),
    Language(R.drawable.ic_portuguese, "Português", "pt"),
    Language(R.drawable.ic_finland, "Suomi", "fi"),
    Language(R.drawable.ic_japan, "日本語", "ja"),
    Language(R.drawable.ic_vietnam, "Tiếng Việt", "vi"),
)

@Composable
fun LanguageScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    localizedContext: Context,
    getLocale: (String) -> Unit,
    language: String
) {

    var selected by remember { mutableStateOf(language) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Header(
            modifier = Modifier.padding(bottom = 16.dp),
            title = localizedContext.resources.getString(R.string.language),
            onBack = onBack
        )
        Spacer(Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(languages) {
                LanguageItem(
                    language = it.language,
                    icon = it.icon,
                    isSelected = selected == it.id,
                    onSelected = {
                        selected = it.id
                        SharedPreference.saveLanguage(localizedContext,it.id)
                        getLocale(it.id)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageItem(
    language: String = "English",
    icon: Int = R.drawable.ic_launcher_background,
    isSelected: Boolean = false,
    onSelected: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onSelected)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = "",
            modifier = Modifier
                .size(36.dp)
                .clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = language,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        RadioButton(
            selected = isSelected,
            onClick = onSelected
        )
    }
}