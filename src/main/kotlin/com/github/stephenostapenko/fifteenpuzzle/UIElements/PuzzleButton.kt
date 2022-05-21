package com.github.stephenostapenko.fifteenpuzzle.UIElements

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.stephenostapenko.fifteenpuzzle.backend.MainPanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButtonImpl
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleGrid
import com.github.stephenostapenko.fifteenpuzzle.backend.Utility
import kotlinx.coroutines.SupervisorJob

class PuzzleButton {
    companion object {
        @Composable
        fun drawPuzzleButton(state: MainPanel.GameState, button: PuzzleButtonImpl, grid: PuzzleGrid) {
            button.updatePositionOnBoard()
            //val animatedPos by animateIntOffsetAsState(IntOffset(button.getXPosForAnimation(), button.getYPosForAnimation()))
            //val animatedYPos by animateDpAsState(button.getYPos().dp)
            //val x = remember { button.getXPos() }
            //val y = button.getYPos()

            Button(
                enabled = button.active,
                onClick = Utility.getOnClickActionForPuzzleButton(state, button, grid),
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
                    .zIndex(button.getOrderIndex())
                    .offset {
                        IntOffset(button.getXPos(), button.getYPos())
                        /*if (button.checkSelected()) {
                            //println("OFFFFFFFFFSET")
                            //println("${button.getRow()} ${button.getCol()}")
                            IntOffset(button.getXPos(), button.getYPos())
                        } else {
                            println("!!!!!!!!!!!!!")
                            //animatedPos
                            IntOffset(button.getXPos(), button.getYPos())
                        }*/
                    }
                    .alpha(if (button.active) 1f else 0f)
                    .pointerInput(Unit) {
                        if (button.active) {
                            detectDragGestures(
                                onDrag = button.getOnDragAction(),
                                onDragStart = button.getOnDragStartAction(state),
                                onDragEnd = button.getOnDragEndAction(state, grid)
                            )
                        }
                    }
            ) {
                Text(
                    text = button.getLabel(),
                    fontSize = 24.sp
                )
            }
        }
    }
}