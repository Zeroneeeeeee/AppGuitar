package com.guitarsimulator.guitar.view.playingguitar.component

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guitarsimulator.guitar.R

@Composable
fun SettingDialog(
    modifier: Modifier = Modifier,
    hand: Int = 0,
    localizedContext: Context,
    onMetronome: Boolean = false,
    onShowIndex: Boolean = false,
    onDismiss: () -> Unit = {},
    onHandClick: (Int) -> Unit = {},
    onMetronomeClick: (Boolean) -> Unit = {},
    onShowIndexClick: (Boolean) -> Unit = {}
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
                    onClick = onDismiss
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
                .background(Color(0xFFFFBF51), RoundedCornerShape(15.dp))
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
                Text(text = localizedContext.getString(R.string.settings), fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = localizedContext.getString(R.string.choose_hand), fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.weight(1f))
                    HandToggle(onClick = onHandClick, hand = hand)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = localizedContext.getString(R.string.metronome), fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.weight(1f))
                    Toggle(
                        onClick = onMetronomeClick,
                        isChecked = onMetronome
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = localizedContext.getString(R.string.show_index), fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Spacer(modifier = Modifier.weight(1f))
                    Toggle(
                        onClick = onShowIndexClick,
                        isChecked = onShowIndex
                    )
                }
            }
        }
    }

}

@Composable
fun HandToggle(modifier: Modifier = Modifier, onClick: (Int) -> Unit = {}, hand: Int = 0) {
    var chosenHand by remember { mutableStateOf(hand) }
    Row(modifier = modifier.background(Color.White, RoundedCornerShape(5.dp))) {
        Icon(
            painter = painterResource(id = R.drawable.ic_left_hand),
            contentDescription = "Left Hand",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (chosenHand == 0) Color(0xFFDB6E0E) else Color.Transparent,
                    RoundedCornerShape(5.dp)
                )
                .clickable(onClick = {
                    chosenHand = 0
                    onClick(chosenHand)
                })
                .padding(4.dp)

        )
        Icon(
            painter = painterResource(id = R.drawable.ic_right_hand),
            contentDescription = "Right Hand",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (chosenHand == 1) Color(0xFFDB6E0E) else Color.Transparent,
                    RoundedCornerShape(5.dp)
                )
                .clickable(onClick = {
                    chosenHand = 1
                    onClick(chosenHand)
                })
                .padding(4.dp)
        )
    }
}

@Composable
fun Toggle(
    modifier: Modifier = Modifier,
    onClick: (Boolean) -> Unit,
    isChecked: Boolean = false
) {
    var checked by remember { mutableStateOf(isChecked) }

    Switch(
        checked = checked,
        onCheckedChange = {
            checked = it
            onClick(checked)
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color(0xFFFFBF51),
            checkedTrackColor = Color.White,
            disabledUncheckedThumbColor = Color.Gray,
            disabledUncheckedTrackColor = Color(0xFFF8D69B),
            checkedBorderColor = Color(0xFFFFBF51),
            uncheckedBorderColor = Color(0xFFF8D69B)
        ),
        modifier = modifier.scale(0.8f)
    )
}