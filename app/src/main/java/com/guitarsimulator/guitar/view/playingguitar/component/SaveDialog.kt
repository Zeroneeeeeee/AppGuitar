package com.guitarsimulator.guitar.view.playingguitar.component

import android.content.Context
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.guitarsimulator.guitar.R
import com.guitarsimulator.guitar.view.recordplaylist.component.Keyboard
import com.guitarsimulator.guitar.view.recordplaylist.component.TextFieldAboveKeyboard
import com.guitarsimulator.guitar.view.recordplaylist.component.keyboardAsState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SaveDialog(
    modifier: Modifier = Modifier,
    window: Window,
    localizedContext: Context,
    onCancel: () -> Unit = {},
    onSave: (String) -> Unit = {}
) {
    val isImeVisible by keyboardAsState()
    var name by remember { mutableStateOf("") }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { }
    ) {
        Column(
            modifier = modifier
                .width(300.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                )
                .border(4.dp, Color.Black, RoundedCornerShape(15.dp))
                .padding(8.dp)
                .background(
                    Color(0xFFFFBF51),
                    androidx.compose.foundation.shape.RoundedCornerShape(15.dp)
                )
                .drawBehind {
                    val strokeWidth = 8.dp.toPx()

                    drawLine(
                        color = Color(0xFFF8D69B),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = Color(0xFFF8D69B),
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = Color(0xFFDB6E0E),
                        start = Offset(size.width, -8f),
                        end = Offset(size.width, size.height + 8f),
                        strokeWidth = strokeWidth
                    )

                    drawLine(
                        color = Color(0xFFDB6E0E),
                        start = Offset(-8f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = localizedContext.getString(R.string.save_record),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = localizedContext.getString(R.string.do_you_want_to_save_this_record),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xffF8D69B),
                    unfocusedContainerColor = Color(0xffF8D69B),
                    focusedIndicatorColor = Color(0xFFDB6E0E),
                    unfocusedIndicatorColor = Color(0xFFDB6E0E),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),

                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onCancel() },
                    colors = ButtonDefaults.buttonColors(Color(0xFFDB6E0E)),
                ) {
                    Text(
                        text = localizedContext.getString(R.string.cancel),
                        color = Color.White
                    )
                }

                Button(
                    onClick = { onSave(name) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFDB6E0E),
                        disabledContentColor = Color.Gray,
                        contentColor = Color.White
                    ),
                    enabled = name.isNotEmpty()
                ) {
                    Text(text = localizedContext.getString(R.string.save))
                }
            }
        }
    }
    if (isImeVisible == Keyboard.Opened) {
        val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
        windowInsertController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        TextFieldAboveKeyboard(name = name, onValueChange = { name = it })
    }
}