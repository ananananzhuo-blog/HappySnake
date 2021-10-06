package com.ananananzhuo.happysnakegame

import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RoundRectShape
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ananananzhuo.happysnakegame.ui.theme.Purple500

/**
 * author  :mayong
 * function:
 * date    :2021/10/3
 **/


@Composable
fun OperateBody() {
    Column(
        Modifier
            .fillMaxHeight()
            .fillMaxWidth(1f)
            .padding(start = 40.dp, end = 40.dp)
    ) {
        val model = viewModel<GameViewModel>()
        Row(
            Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GameButton(Modifier.weight(1f), "开始/重置") {
                model.startOrReset()
            }
            GameButton(Modifier.weight(1f), "恢复/暂停") {
                model.resumeOrPause()
            }
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(Modifier.size(200.dp)) {
                DirectionButtom(m = Modifier.align(Alignment.CenterStart), "◀") {
                    model.changeDirection(Direction.Left)
                }
                DirectionButtom(m = Modifier.align(Alignment.CenterEnd), "▶") {
                    model.changeDirection(Direction.Right)
                }
                DirectionButtom(m = Modifier.align(Alignment.TopCenter), "▲") {
                    model.changeDirection(Direction.Top)
                }
                DirectionButtom(m = Modifier.align(Alignment.BottomCenter), "▼") {
                    model.changeDirection(Direction.Bottom)
                }
            }
        }
    }
}

@Composable
fun GameButton(m: Modifier, text: String, click: () -> Unit) {
    Column(Modifier.height(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = text)
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = m
                .padding(start = 10.dp, end = 10.dp)
                .width(60.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Purple500)
                .clickable {
                    click.invoke()
                }
        ) {

        }
    }

}

@Composable
fun DirectionButtom(m: Modifier, text: String, click: () -> Unit) {
    Box(
        m
            .size(80.dp)
            .padding(15.dp)
            .background(color = Purple500, shape = RoundedCornerShape(25.dp))
            .clickable {
                click.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White)
    }
}