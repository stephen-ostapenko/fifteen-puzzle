package com.github.stephenostapenko.fifteenpuzzle.UI

/*import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButton
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun composePanel() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                turnsCounter()
                successLabel()
                shuffleButton()
                puzzleButtons()
            }
        }
    }
}

@Composable
private fun puzzleButtons() {
    BoxWithConstraints(modifier = Modifier
        .fillMaxSize()
        .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(5.dp))
    ) {
        buttonList.forEach {
            it.boardHeight = constraints.maxHeight
            it.boardWidth = constraints.maxWidth
        }

        buttonList.forEach {
            puzzleButton(it)
        }
    }
}

@Composable
private fun puzzleButton(button: PuzzleButton) {
    button.updatePos()

    val onDragAction = action@{ change: PointerInputChange, dragAmount: Offset ->
        if (!button.active) {
            return@action
        }

        gameFinishedState.value = false
        button.select()

        var nextButtonXPos = button.getXPos() + dragAmount.x.roundToInt()
        var nextButtonYPos = button.getYPos() + dragAmount.y.roundToInt()
        nextButtonXPos = max(0, nextButtonXPos)
        nextButtonXPos = min(button.boardWidth - button.getWidth(), nextButtonXPos)
        nextButtonYPos = max(0, nextButtonYPos)
        nextButtonYPos = min(button.boardHeight - button.getHeight(), nextButtonYPos)

        button.setXPos(nextButtonXPos)
        button.setYPos(nextButtonYPos)
        change.consumeAllChanges()
    }

    val onDragEndAction = action@{
        button.deselect()

        val swapButton = button.findNearestButtonToCurrent(buttonList)
        if (swapButton.active) {
            button.updatePos()
            return@action
        }
        if (button.getManhattanDistOnGrid(swapButton) != 1) {
            button.updatePos()
            return@action
        }

        val halfButtonHeight = swapButton.getHeight() / 2
        val halfButtonWidth = swapButton.getWidth() / 2
        if (button.getXPos() !in
            (swapButton.getXPos() - halfButtonWidth)..(swapButton.getXPos() + halfButtonWidth))
        {
            button.updatePos()
            return@action
        }
        if (button.getYPos() !in
            (swapButton.getYPos() - halfButtonHeight)..(swapButton.getYPos() + halfButtonHeight))
        {
            button.updatePos()
            return@action
        }

        button.swapPositions(swapButton)
        button.updatePos()
        swapButton.updatePos()

        turnsCount.value++
        if (checkForSuccess()) {
            gameFinishedState.value = true
        }
    }

    Button(
        enabled = button.active,
        onClick = {},
        colors = ButtonDefaults.buttonColors(
            if (button.checkSelected())
                MaterialTheme.colors.secondary
            else
                MaterialTheme.colors.primary
        ),
        modifier = Modifier
            .height(button.getHeight().dp)
            .width(button.getWidth().dp)
            .padding(5.dp)
            .zIndex(if (button.checkSelected()) 1f else 0f)
            .offset {
                IntOffset(button.getXPos(), button.getYPos())
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = onDragAction,
                    onDragEnd = onDragEndAction
                )
            }
    ) {
        Text(
            text = button.label,
            fontSize = 24.sp
        )
    }
}

@Composable
fun turnsCounter() {
    Text(
        text = "${turnsCount.value} turn" + if (turnsCount.value != 1) "s" else "",
        modifier = Modifier.scale(2f).padding(top = 20.dp, bottom = 15.dp)
    )
}

@Composable
fun successLabel() {
    Text(
        text = if (gameFinishedState.value) "Success!" else "",
        modifier = Modifier.scale(2f).padding(20.dp)
    )
}

@Composable
fun shuffleButton() {
    Button(
        enabled = gameFinishedState.value || gameInitState.value,
        onClick = {
            //shuffleCells()
        },
        modifier = Modifier.scale(1.25f).padding(15.dp)
    ) {
        Text(text = "Shuffle cells")
    }
}*/