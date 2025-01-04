package com.example.mpaproject

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE isCompleted = :status")
    fun getTasksByStatus(status: Boolean): LiveData<List<Task>>

    @Query("UPDATE tasks SET title = :title, description = :description, isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTask(id: Int, title: String, description: String, isCompleted: Boolean)

    @Query("SELECT * FROM tasks WHERE isCompleted = :status")
    fun getTasksByCompletionStatus(status: Boolean): LiveData<List<Task>>
}