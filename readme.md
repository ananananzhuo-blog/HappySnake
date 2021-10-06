> 关注公众号学习更多知识
>
>![](https://img-blog.csdnimg.cn/img_convert/6dd2df09156ca4cbfc44ad68c9baa2e4.png)

## 灵感来源
前端时间看到了大佬fundroid使用compose编写俄罗斯方块的文章，深受启发，当时便决定也要把诺基亚的贪吃蛇搬到android上来，因此才有了这篇文章

本篇文章界面和思路参考于 fundroid的文章

[fundroid俄罗斯方块传送:](https://blog.csdn.net/vitaviva/article/details/115878190) https://blog.csdn.net/vitaviva/article/details/115878190
## 最终效果
提前发一下效果，让读者有个心理预期，逻辑真不复杂，务必耐心看完

![俄罗斯方块_1.gif](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/bf82c02211664c5899ace92ebd4eade9~tplv-k3u1fbpfcp-watermark.image?)
## 思路
我们的界面分为两部分，上半部分是游戏的动态显示区域，下半部分是操作区域
### 显示区域
显示区域也可以细分为两部分，
- 边框+黑线边框
- 屏幕动态显示区域

所以我们把这两部分分成两个组合`@Composable BaseScreen`、`@Composable GameScreen`

`BaseScreen`用于绘制边框和黑线边框，这部分只需要绘制一次即可，所以我们需要抽离出来

`GameScreen`用于绘制动态区域，主要是绘制基础的浅色砖块矩阵，和蛇身深色砖块、游离蛇身深色砖块

### 操作区域
操作区域也有两部分，

- 开始重置按钮、恢复暂停按钮，这两个按钮用于操作游戏状态
- 方向键按钮，用于操纵蛇头方向

操作区域组合名称为`@Composable OperateBody`。

其中操作状态按钮使用Row包裹两个`@Composable GameButton`来实现

方向键按钮使用一个`Box`来包裹四个`@Composable DirectionButtom`来实现

### 逻辑
#### 如何数据通信
使用StateFlow.collectAsState方法来获取Compose状态，从而在状态更改的时候刷新界面
#### 如何刷新游戏状态
使用`LauncherEffect`内部定义一个死循环来维护时钟，保证我们可以在自定义的时间间隔内更新游戏状态。
#### 蛇身状态
我们使用一个数组来维护所有蛇身的砖块，然后就是蛇移动、蛇吃游离蛇身、蛇撞墙几个逻辑

##### 蛇移动
蛇单纯移动的话，我们可以通过构建一个新数据的方式来实现，新数组的第一个元素就是蛇身下一个要移动到的砖块，然后将老的蛇身数组addAll到新的蛇身数组中，最后移除新蛇身数组的最后一个蛇身。刷新StateFlow状态
##### 蛇吃游离蛇身
如果蛇下次要移动到的砖块位置刚好是游离蛇身的位置的话，那么我们直接将游离蛇身插入到数组的第一个元素位置，并且不移除蛇身数组最后位置的转块
##### 蛇撞墙
整个屏幕被我们栅格化为`Width_Matrix * Height_Matrix`数组，如果蛇头的位置超出栅格数组那么蛇撞墙死，游戏结束。
## 代码分析
### StateFlow声明和使用
声明StateFlow，这部分不需要引入任何依赖
```kt
private val _flow = MutableStateFlow(SnakeState(action = Action.GameTick))
val stateFlow = _flow.asStateFlow()
```

使用StateFlow获取状态


```kt
 val model = viewModel<GameViewModel>()
 val state = model.stateFlow.collectAsState().value
```

这里的`SnakeState`就是我们定义的状态实体，代码如下：


```kt
data class SnakeState(
    var action: Action,
    val direction: Direction = Direction.Bottom,
    val snakeBody: MutableList<SnakePart> = mutableListOf(SnakePart(10, 10)),
    val freeSnakePart: SnakePart = SnakePart(15, 15),
    var Width_Matrix: Int = 0,
    var Height_Matrix: Int = 0,
    var isRunning: Boolean = true
)
```


### 游戏状态时钟代码
使用`LauncherEffect`维持死循环，300ms分发一次时钟
```kt
val model = viewModel<GameViewModel>()
    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            delay(300)
            model.dispatch()
        }
    }
```

### 蛇移动、吃游离蛇身、撞墙代码


```kt
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
```

## 未实现部分
- 蛇撞自身蛇身逻辑
- 蛇撞墙后游戏结束的动画

这部分有兴趣的可以fork代码后自己实现以下，相信会有所收获。

