package com.example.mpaproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    fun getCompletionProgress(): Float {
        val allTasks = allTasks.value ?: emptyList()
        val totalTasks = allTasks.size
        val completedTasks = allTasks.count { it.isCompleted }
        return if (totalTasks == 0) 0f else (completedTasks.toFloat() / totalTasks.toFloat()) * 100
    }

    fun getCompletedTasks(): LiveData<List<Task>> {
        return taskDao.getTasksByCompletionStatus(true)
    }

    fun getIncompleteTasks(): LiveData<List<Task>> {
        return taskDao.getTasksByCompletionStatus(false)
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }

    fun updateTask(id: Int, title: String, description: String, isCompleted: Boolean) {
        viewModelScope.launch {
            taskDao.updateTask(id, title, description, isCompleted)
        }
    }
}