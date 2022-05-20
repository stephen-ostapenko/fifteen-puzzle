package com.github.stephenostapenko.fifteenpuzzle.backend

import androidx.compose.runtime.mutableStateOf
import kotlin.math.abs
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

    fun checkButtonFitsInitPosition(): Boolean {
        return row == initRow && col == initCol
    }

    private val xPos = mutableStateOf(0)
    private val yPos = mutableStateOf(0)

    fun getXPos(): Int {
        return xPos.value
    }

    fun getYPos(): Int {
        return yPos.value
    }

    fun setXPos(pos: Int) {
        xPos.value = pos
    }

    fun setYPos(pos: Int) {
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
}