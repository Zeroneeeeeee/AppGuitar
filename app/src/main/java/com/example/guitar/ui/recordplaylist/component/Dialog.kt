package com.example.guitar.ui.recordplaylist.component

import android.content.Context
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.guitar.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RenameDialog(
    modifier: Modifier = Modifier,
    name: String = "",
    onDismiss: () -> Unit = {},
    onSaveClick: (String) -> Unit = {},
    localizedContext: Context,
    window: Window
) {
    var newName by remember { mutableStateOf(name) }
    val isImeVisible by keyboardAsState()
    LaunchedEffect(isImeVisible) { Log.d("TextFieldAboveKeyboard", "isImeVisible: $isImeVisible") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onDismiss() }
                )
        ) {}
        Box(
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
                .padding(16.dp)

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = localizedContext.resources.getString(R.string.rename),
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
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
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            onDismiss()
                            Toast.makeText(
                                localizedContext,
                                localizedContext.getString(R.string.cancel_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFDB6E0E)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = localizedContext.resources.getString(R.string.cancel),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.2f))
                    Button(
                        onClick = { onSaveClick(newName) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFDB6E0E),
                            disabledContentColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f),
                        enabled = newName.isNotBlank()
                    ) {
                        Text(
                            text = localizedContext.resources.getString(R.string.save)
                        )
                    }
                }
            }
        }
        if (isImeVisible == Keyboard.Opened) {
            val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsertController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            TextFieldAboveKeyboard(
                name = newName,
                onValueChange = { newName = it }
            )
        }
    }
}

@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    localizedContext: Context
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { onDismiss() }
                )
        ) {}
        Box(
            modifier = modifier
                .width(300.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {}
                )
                .border(
                    4.dp,
                    Color.Black,
                    androidx.compose.foundation.shape.RoundedCornerShape(15.dp)
                )
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
                .padding(16.dp)

        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = localizedContext.resources.getString(R.string.delete),
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = localizedContext.resources.getString(R.string.are_you_sure_you_want_to_delete_this_item),
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            onDismiss()
                            Toast.makeText(
                                localizedContext,
                                localizedContext.getString(R.string.cancel_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFFDB6E0E)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = localizedContext.resources.getString(R.string.cancel),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.weight(0.2f))
                    Button(
                        onClick = { onDeleteClick() },
                        colors = ButtonDefaults.buttonColors(Color(0xFFDB6E0E)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = localizedContext.resources.getString(R.string.delete),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}