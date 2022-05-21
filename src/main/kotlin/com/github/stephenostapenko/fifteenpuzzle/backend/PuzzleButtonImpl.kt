package com.github.stephenostapenko.fifteenpuzzle.backend

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Suppress("MemberVisibilityCanBePrivate", "unused")
class PuzzleButtonImpl(val initRow: Int, val initCol: Int,
                       private val rowsNumber: Int, private val columnsNumber: Int)
{
    private var row = initRow
    private var col = initCol
    private var label = "${row * columnsNumber + col + 1}"

    fun getRow(): Int {
        return row
    }

    fun getCol(): Int {
        return col
    }

    fun getLabel(): String {
        return label
    }

    fun moveButtonOnGrid(newRow: Int, newCol: Int) {
        row = newRow
        col = newCol
        updatePositionOnBoard()
    }

    @Composable
    fun moveButtonOnGridAnimated(newRow: Int, newCol: Int) {
        row = newRow
        col = newCol
        updatePositionOnBoardAnimated()
    }

    fun checkButtonFitsInitPosition(): Boolean {
        return row == initRow && col == initCol
    }

    private val xPos = mutableStateOf(0)
    private val yPos = mutableStateOf(0)
    private val xPosForAnimation = mutableStateOf(0)
    private val yPosForAnimation = mutableStateOf(0)

    fun getXPos(): Int {
        return xPos.value
    }

    fun getYPos(): Int {
        return yPos.value
    }

    fun getXPosForAnimation(): Int {
        return xPosForAnimation.value
    }

    fun getYPosForAnimation(): Int {
        return yPosForAnimation.value
    }

    fun setXPos(pos: Int) {
        xPosForAnimation.value = pos
        xPos.value = pos
    }

    fun setYPos(pos: Int) {
        yPosForAnimation.value = pos
        yPos.value = pos
    }

    var boardHeight = 0
    var boardWidth = 0

    fun initButtonPositionOnBoard(boardHeight: Int, boardWidth: Int) {
        setBoardSize(boardHeight, boardWidth)
        updatePositionOnBoard()
    }

    fun setBoardSize(height: Int, width: Int) {
        boardHeight = height
        boardWidth = width
    }

    fun updatePositionOnBoard() {
        setXPos((getScaledXPos() * boardWidth).roundToInt())
        setYPos((getScaledYPos() * boardHeight).roundToInt())
    }

    @Composable
    fun updatePositionOnBoardAnimated() {
        val xPos by animateIntAsState(xPosForAnimation.value)
        setXPos(xPos)
        setYPos((getScaledYPos() * boardHeight).roundToInt())
    }

    fun getHeight(): Int {
        return (getScaledHeight() * boardHeight).roundToInt()
    }

    fun getWidth(): Int {
        return (getScaledWidth() * boardWidth).roundToInt()
    }

    val active = !(initRow + 1 == rowsNumber && initCol + 1 == columnsNumber)

    private val selected = mutableStateOf(false)

    fun checkSelected(): Boolean {
        return selected.value
    }

    fun select() {
        selected.value = true
    }

    fun deselect() {
        selected.value = false
    }

    fun getOrderIndex(): Float {
        return when {
            !active -> -1f
            checkSelected() -> 1f
            else -> 0f
        }
    }

    fun getScaledHeight(): Double {
        return 1.0 / rowsNumber
    }

    fun getScaledWidth(): Double {
        return 1.0 / columnsNumber
    }

    fun getScaledXPos(): Double {
        return getScaledWidth() * col
    }

    fun getScaledYPos(): Double {
        return getScaledHeight() * row
    }

    fun swapPositions(button: PuzzleButtonImpl) {
        row = button.row.also { button.row = row }
        col = button.col.also { button.col = col }
    }

    fun getManhattanDistOnGrid(button: PuzzleButtonImpl): Int {
        return abs(row - button.row) + abs(col - button.col)
    }

    fun getOnDragAction(): (PointerInputChange, Offset) -> Unit {
        return action@{ change: PointerInputChange, dragAmount: Offset ->
            if (!active) {
                return@action
            }

            var nextButtonXPos = getXPos() + dragAmount.x.roundToInt()
            var nextButtonYPos = getYPos() + dragAmount.y.roundToInt()
            nextButtonXPos = max(0, nextButtonXPos)
            nextButtonXPos = min(boardWidth - getWidth(), nextButtonXPos)
            nextButtonYPos = max(0, nextButtonYPos)
            nextButtonYPos = min(boardHeight - getHeight(), nextButtonYPos)

            setXPos(nextButtonXPos)
            setYPos(nextButtonYPos)
            change.consumeAllChanges()
        }
    }

    fun getOnDragStartAction(state: MainPanel.GameState): (Offset) -> Unit {
        return action@{
            if (!active) {
                return@action
            }

            state.setInProgress()
            select()
        }
    }

    fun getOnDragEndAction(state: MainPanel.GameState, grid: PuzzleGrid): () -> Unit {
        return action@{
            deselect()

            val swapButton = grid.findNearestButtonOnGrid(this)
            if (swapButton.active) {
                updatePositionOnBoard()
                return@action
            }
            if (getManhattanDistOnGrid(swapButton) != 1) {
                updatePositionOnBoard()
                return@action
            }

            val halfButtonHeight = swapButton.getHeight() / 2
            val halfButtonWidth = swapButton.getWidth() / 2
            if (getXPos() !in (swapButton.getXPos() - halfButtonWidth)..(swapButton.getXPos() + halfButtonWidth)) {
                updatePositionOnBoard()
                return@action
            }
            if (getYPos() !in (swapButton.getYPos() - halfButtonHeight)..(swapButton.getYPos() + halfButtonHeight)) {
                updatePositionOnBoard()
                return@action
            }

            swapPositions(swapButton)
            updatePositionOnBoard()
            swapButton.updatePositionOnBoard()

            state.incTurnsCount()
            if (grid.checkIfGridIsFinished()) {
                state.setFinished()
            }
        }
    }
}