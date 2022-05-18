package com.github.stephenostapenko.fifteenpuzzle.services

import com.github.stephenostapenko.fifteenpuzzle.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
