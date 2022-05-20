package com.github.stephenostapenko.fifteenpuzzle

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class WindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Creating content for window \"${toolWindow.id}\" in project \"${project.name}\"...")

        val content = ContentFactory.SERVICE.getInstance().createContent(
            MainPanel(4, 4).getSwingPanel(),
            "",
            false
        )
        toolWindow.contentManager.addContent(content, 0)

        println("Content for window \"${toolWindow.id}\" in project \"${project.name}\" has been created")
    }
}