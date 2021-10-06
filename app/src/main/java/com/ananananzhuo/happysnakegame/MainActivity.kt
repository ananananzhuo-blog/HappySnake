package com.ananananzhuo.happysnakegame

import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ananananzhuo.happysnakegame.ui.theme.BodyColor
import com.ananananzhuo.happysnakegame.ui.theme.HappySnakeGameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HappySnakeGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Home()
                }
            }
        }
    }
}

@Composable
fun Home() {
    val model = viewModel<GameViewModel>()
    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            delay(300)
            model.dispatch()
        }
    }
    GameBody()
}

@Composable
fun GameBody() {
    Column(
        Modifier
            .fillMaxSize(1f)
            .background(BodyColor)) {
        DisplayBody()
        OperateBody()
    }
}

















