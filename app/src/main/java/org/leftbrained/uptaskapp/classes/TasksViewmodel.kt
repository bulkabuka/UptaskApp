package org.leftbrained.uptaskapp.classes

import org.jetbrains.exposed.sql.transactions.transaction

class TasksViewmodel {
    fun getTasks(taskListId: Int): List<UserTask> {
        transaction {
            return@transaction UserTask.find {
                UptaskDb.UserTasks.taskListId eq taskListId
            }.toList<UserTask>()
        }
        return emptyList()
    }

    fun addTask(task: UserTask) {
        transaction {
            UserTask.new {
                taskListId = task.taskListId
                userId = task.userId
                this.task = task.task
                description = task.description
                dueDate = task.dueDate
                isDone = task.isDone
                taskListId = task.taskListId
            }
        }
    }

    fun getTask(taskId: Int): UserTask? {
        transaction {
            return@transaction UserTask.find { UptaskDb.UserTasks.id eq taskId }.firstOrNull()
        }
        return null
    }

    fun deleteTask(taskId: Int) {
        transaction {
            UserTask.find { UptaskDb.UserTasks.id eq taskId }.firstOrNull()?.delete()
        }
    }
}