package org.leftbrained.uptaskapp.classes

import kotlinx.serialization.Serializable

@Serializable
data class TaskListAll(
    var list: List<TaskList>
)