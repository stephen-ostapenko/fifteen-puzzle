package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.awt.ComposePanel
import com.github.stephenostapenko.fifteenpuzzle.UI.UIElements.Companion.composePanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButton
import javax.swing.JComponent

class MainPanel(private val rowsNumber: Int, private val columnsNumber: Int) {
    private var buttonList: List<List<PuzzleButton>> = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            PuzzleButton(row, col, rowsNumber, columnsNumber)
        }
    }

    fun getJComponentPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                composePanel(rowsNumber, columnsNumber, GameState, buttonList, checkForSuccess)
            }
        }
    }

    @Composable
    fun getComposePanel() {
        composePanel(rowsNumber, columnsNumber, GameState, buttonList, checkForSuccess)
    }

    private val checkForSuccess = check@{ buttonList: List<List<PuzzleButton>> ->
        return@check buttonList.all { rowList -> rowList.all { it.checkButtonPosition() } }
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
    object GameState {
        enum class ProcessState {
            Ready, InProgress, Finished
        }

        val notProgressStates = listOf(ProcessState.Ready, ProcessState.Finished)

        private val state = mutableStateOf(ProcessState.Ready)

        fun getState(): ProcessState {
            return state.value
        }

        fun setReady() {
            resetTurnsCount()
            state.value = ProcessState.Ready
        }

        fun setInProgress() {
            if (state.value != ProcessState.InProgress) {
                resetTurnsCount()
            }
            state.value = ProcessState.InProgress
        }

        fun setFinished() {
            state.value = ProcessState.Finished
        }

        private val turnsCount = mutableStateOf(0)

        fun getTurnsCount(): Int {
            return turnsCount.value
        }

        fun incTurnsCount() {
            turnsCount.value++
        }

        fun resetTurnsCount() {
            turnsCount.value = 0
        }
    }
}