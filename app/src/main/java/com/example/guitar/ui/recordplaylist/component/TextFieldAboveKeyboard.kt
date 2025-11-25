package com.example.guitar.ui.recordplaylist.component

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun TextFieldAboveKeyboard(name: String, onValueChange: (String) -> Unit) {
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
    val navigationBarHeight = systemBarsPadding.calculateBottomPadding()

    var text by remember {
        mutableStateOf(TextFieldValue(name))
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        Log.d("TextFieldAboveKeyboard", "LaunchedEffect")
        text = text.copy(selection = TextRange(text.text.length))
        focusRequester.requestFocus()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = navigationBarHeight)
            .windowInsetsPadding(WindowInsets.ime)
    ) {
        TextField(
            value = text,
            onValueChange = {
                text = it
                onValueChange(it.text)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
        )
    }
}