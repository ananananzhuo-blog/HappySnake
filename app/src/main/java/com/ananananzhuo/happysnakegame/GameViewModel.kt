package com.ananananzhuo.happysnakegame

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * author  :mayong
 * function:数据处理
 * date    :2021/10/3
 **/
class GameViewModel : ViewModel() {
    private val _flow = MutableStateFlow(SnakeState(action = Action.GameTick))
    val stateFlow = _flow.asStateFlow()

    /**
     * 刷新状态事件
     */
    fun dispatch() {
        val state = stateFlow.value
        if (state.action!=Action.GameTick){
            return
        }
        if (!state.isRunning) {//暂停状态不更新数据
            return
        }
        val firstPart = state.snakeBody.first()
        val newList = mutableListOf<SnakePart>()
        if (state.direction == Direction.Left || state.direction == Direction.Right) {
            val x = firstPart.x + state.direction.increase
            if (x == state.freeSnakePart.x && firstPart.y == state.freeSnakePart.y) {//吃游离蛇身
                newList.add(state.freeSnakePart)
                newList.addAll(state.snakeBody)
                emit(state.copy(snakeBody = newList, freeSnakePart = newFreeSnakePart()))
                return
            }
            if (x > state.Width_Matrix || x < 0) {//撞墙
                emit(
                    state.copy(
                        action = Action.GameOver,
                    )
                )
                return
            } else {
                newList.add(SnakePart(x, firstPart.y))
                newList.addAll(state.snakeBody)
                newList.removeLast()//删除蛇身的最后一节
            }
        } else {
            val y = firstPart.y + state.direction.increase
            if (y == state.freeSnakePart.y && firstPart.x == state.freeSnakePart.x) {//吃游离蛇身
                newList.add(state.freeSnakePart)
                newList.addAll(state.snakeBody)
                emit(state.copy(snakeBody = newList, freeSnakePart = newFreeSnakePart()))
                return
            }
            if (y > state.Height_Matrix || y < 0) {//撞墙
                emit(
                    state.copy(
                        action = Action.GameOver,
                    )
                )
                return
            } else {
                newList.add(SnakePart(firstPart.x, y))
                newList.addAll(state.snakeBody)
                newList.removeLast()//删除蛇身的最后一节
            }
        }
        emit(state.copy(snakeBody = newList))
    }

    fun emit(state: SnakeState) {
        _flow.value = state
    }

    fun changeDirection(direction: Direction) {
        when (_flow.value.direction) {
            Direction.Bottom, Direction.Top -> {
                if (direction == Direction.Left || direction == Direction.Right) {
                    emit(state = stateFlow.value.copy(direction = direction))
                }
            }
            Direction.Right, Direction.Left -> {
                if (direction == Direction.Top || direction == Direction.Bottom) {
                    emit(state = stateFlow.value.copy(direction = direction))
                }
            }
        }
    }

    private fun newFreeSnakePart(): SnakePart {
        val state = _flow.value
        val x = (0..state.Width_Matrix).random()
        val y = (0..state.Height_Matrix).random()
        val snakePart = SnakePart(x, y)
        if (snakePart in state.snakeBody) {
            newFreeSnakePart()
        }
        return snakePart
    }

    /**
     * 开始或重置
     */
    fun startOrReset() {
        val state = _flow.value
        state.snakeBody.clear()
        state.isRunning = true
        emit(
            state.copy(
                isRunning = true,
                snakeBody = mutableListOf(SnakePart(10, 10)),
                freeSnakePart = newFreeSnakePart(),
                action = Action.GameTick
            )
        )
    }

    /**
     * 恢复或暂停
     */
    fun resumeOrPause() {
        val state = _flow.value
        emit(state = state.copy(isRunning = !state.isRunning))
    }
}

data class SnakeState(
    var action: Action,
    val direction: Direction = Direction.Bottom,
    val snakeBody: MutableList<SnakePart> = mutableListOf(SnakePart(10, 10)),
    val freeSnakePart: SnakePart = SnakePart(15, 15),
    var Width_Matrix: Int = 0,
    var Height_Matrix: Int = 0,
    var isRunning: Boolean = true
)

/**
 * 蛇身实体
 */
data class SnakePart(var x: Int, var y: Int) {
    override operator fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is SnakePart) {
            other.x == this.x && other.y == this.y
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}