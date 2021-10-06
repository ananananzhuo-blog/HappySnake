package com.ananananzhuo.happysnakegame

/**
 * author  :mayong
 * function:
 * date    :2021/10/3
 **/
open class Action {
    object GameTick : Action()
    object GameOver:Action()

}

enum class Direction(val increase: Int) {
    Left(-1), Right(1), Top(-1), Bottom(1)
}