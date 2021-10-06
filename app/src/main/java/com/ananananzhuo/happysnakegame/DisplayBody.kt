package com.ananananzhuo.happysnakegame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ananananzhuo.happysnakegame.ui.theme.BrickMatrix
import com.ananananzhuo.happysnakegame.ui.theme.BrickSpirit
import com.ananananzhuo.happysnakegame.ui.theme.ScreenBackground

/**
 * author  :mayong
 * function:
 * date    :2021/10/3
 **/
private val borderSize = 12.dp
private val brickOuterSize = 10.dp//砖块的宽高
private val brickInnerSize = 4.dp//砖块内部宽高
private val brickMargin = 3//两个砖块间的宽度

/**
 * 绘制屏幕，包括边框和游戏界面
 */
@Composable
fun DisplayBody() {
    Box(
        Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth(1f)
            .padding(all = 40.dp)
    ) {
        BaseScreen()
        Box(
            modifier = Modifier
                .padding(borderSize)
        ) {
            GameScreen()
        }
    }
}

/**
 * 动态游戏画面
 */
@Composable
fun GameScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        val model = viewModel<GameViewModel>()
        val state = model.stateFlow.collectAsState().value
        Canvas(
            Modifier.fillMaxSize()
        ) {
            val widthNum = (size.width / brickOuterSize.toPx()).toInt() - 2
            val heightNum = (size.height / brickOuterSize.toPx()).toInt() - 2
            if (state.Width_Matrix == 0 || state.Height_Matrix == 0) {
                state.Width_Matrix = widthNum
                state.Height_Matrix = heightNum
            }
            val xOffset = (size.width - (brickOuterSize * (widthNum + 1)).toPx()) / 2
            val yOffset = (size.height - (brickOuterSize * (heightNum + 1)).toPx()) / 2
            drawMatrix(widthNum, heightNum, xOffset, yOffset)
            drawSnakePart(state.snakeBody, xOffset, yOffset, BrickSpirit)
            drawFreePart(state.freeSnakePart, xOffset, yOffset, BrickSpirit)
        }
    }
}

/**
 * 画游离的蛇身部分
 */
fun DrawScope.drawFreePart(
    freeSnakePart: SnakePart,
    xOffset: Float,
    yOffset: Float,
    brickSpirit: Color
) {
    drawBrick(freeSnakePart.x, freeSnakePart.y, xOffset, yOffset, brickSpirit)
}

/**
 * 画蛇身
 */
fun DrawScope.drawSnakePart(
    snakePart: List<SnakePart>,
    xOffset: Float,
    yOffset: Float,
    brickSpirit: Color
) {
    if (snakePart.isNotEmpty()) {
        snakePart.forEach {
            drawBrick(it.x, it.y, xOffset, yOffset, brickSpirit)
        }
    }
}

/**
 * 绘制基础背景浅色砖块
 */
fun DrawScope.drawMatrix(widthNum: Int, heightNum: Int, xOffset: Float, yOffset: Float) {
    for (xNum in 0..widthNum) {
        for (yNum in 0..heightNum) {
            drawBrick(xNum, yNum, xOffset, yOffset)
        }
    }
}

/**
 * 画砖头
 */
fun DrawScope.drawBrick(
    xNum: Int,
    yNum: Int,
    xOffset: Float,
    yOffset: Float,
    color: Color = BrickMatrix
) {
    drawSingleInnerMatrix(xNum, yNum, xOffset, yOffset, color)
    drawSingleOuterMatrix(xNum, yNum, xOffset, yOffset, color)
}

/**
 * 绘制单个浅色砖块内部实心部分
 */
fun DrawScope.drawSingleInnerMatrix(
    xNum: Int,
    yNum: Int,
    xOffset: Float,
    yOffset: Float,
    color: Color
) {
    val x = xNum * brickOuterSize.toPx() + xOffset
    val y = yNum * brickOuterSize.toPx() + yOffset
    val brickOffset = (brickOuterSize - brickInnerSize).toPx() / 2
    drawRect(
        color,
        topLeft = Offset(x + brickOffset, y + brickOffset),
        size = Size(brickInnerSize.toPx(), brickInnerSize.toPx()),
        style = Fill
    )
}

/**
 * 绘制单个浅色砖块外部边框部分
 */
fun DrawScope.drawSingleOuterMatrix(
    xNum: Int,
    yNum: Int,
    xOffset: Float,
    yOffset: Float,
    color: Color
) {
    val x = xNum * brickOuterSize.toPx() + xOffset
    val y = yNum * brickOuterSize.toPx() + yOffset
    drawRect(
        color,
        topLeft = Offset(x + brickMargin, y + brickMargin),
        size = Size(
            brickOuterSize.toPx() - brickMargin * 2,
            brickOuterSize.toPx() - brickMargin * 2
        ),
        style = Stroke(2f)
    )
}

/**
 * 基础屏幕，包括border和屏幕背景
 */
@Composable
fun BaseScreen() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        drawBorder()
        drawGameScreen()
        drawInnerBorder()
    }

}

/**
 * 绘制屏幕边上的黑线
 */
private fun DrawScope.drawInnerBorder() {
    drawRect(
        Color.Black,
        topLeft = Offset(borderSize.toPx(), borderSize.toPx()),
        size = Size(size.width - borderSize.toPx() * 2, size.height - borderSize.toPx() * 2),
        style = Stroke(3f)
    )
}

/**
 * 绘制屏幕
 */
private fun DrawScope.drawGameScreen() {
    drawRect(
        ScreenBackground,
        topLeft = Offset(borderSize.toPx(), borderSize.toPx()),
        size = Size(size.width - borderSize.toPx() * 2, size.height - borderSize.toPx() * 2)
    )
}

/**
 * 绘制border
 */
private fun DrawScope.drawBorder() {
    val path = Path().apply {
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(0f, size.height)
        lineTo(0f, 0f)
        close()
    }
    drawPath(path, Color.Black.copy(0.5f))
    val path1 = Path().apply {
        moveTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        lineTo(size.width, 0f)
        close()
    }
    drawPath(path1, Color.White.copy(0.5f))
}