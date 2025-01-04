package com.example.mpaproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = TaskDatabase.getDatabase(application).taskDao()
    private val allTasksLiveData = taskDao.getAllTasks()
    private val completedTasksLiveData = taskDao.getTasksByCompletionStatus(true)
    private val incompleteTasksLiveData = taskDao.getTasksByCompletionStatus(false)

    val filteredTasks = MediatorLiveData<List<Task>>()
    private val currentFilter = MutableLiveData<MainActivity.Filter>()

    init {
        filteredTasks.addSource(allTasksLiveData) { updateFilteredTasks() }
        filteredTasks.addSource(completedTasksLiveData) { updateFilteredTasks() }
        filteredTasks.addSource(incompleteTasksLiveData) { updateFilteredTasks() }
        filteredTasks.addSource(currentFilter) { updateFilteredTasks() }
    }

    fun getCompletionProgress(): Float {
        val allTasks = allTasksLiveData.value ?: emptyList()
        val totalTasks = allTasks.size
        val completedTasks = allTasks.count { it.isCompleted }
        return if (totalTasks == 0) 0f else (completedTasks.toFloat() / totalTasks.toFloat()) * 100
    }

    fun setFilter(filter: MainActivity.Filter) {
        currentFilter.value = filter
    }

    private fun updateFilteredTasks() {
        val filter = currentFilter.value ?: MainActivity.Filter.ALL
        val allTasks = allTasksLiveData.value.orEmpty()

        filteredTasks.value = when (filter) {
            MainActivity.Filter.ALL -> allTasks
            MainActivity.Filter.COMPLETED -> allTasks.filter { it.isCompleted }
            MainActivity.Filter.INCOMPLETE -> allTasks.filter { !it.isCompleted }
        }
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