package com.example.mpaproject

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private var currentFilter: Filter = Filter.ALL

    enum class Filter {
        ALL,
        COMPLETED,
        INCOMPLETE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        applyFilter(currentFilter)

        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showAddTaskDialog()
        }

        val completedButton: Button = findViewById(R.id.completedButton)
        completedButton.setOnClickListener {
            applyFilter(Filter.COMPLETED)
        }

        val incompleteButton: Button = findViewById(R.id.incompleteButton)
        incompleteButton.setOnClickListener {
            applyFilter(Filter.INCOMPLETE)
        }

        val allButton: Button = findViewById(R.id.allButton)
        allButton.setOnClickListener {
            applyFilter(Filter.ALL)
        }
    }

    private fun showAddTaskDialog() {
        val editText = EditText(this)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Přidat nový úkol")
            .setView(editText)
            .setPositiveButton("Přidat") { _: DialogInterface, _: Int ->
                val taskTitle = editText.text.toString()
                if (taskTitle.isNotEmpty()) {
                    val task = Task(title = taskTitle)
                    taskViewModel.addTask(task)
                }
            }
            .setNegativeButton("Zrušit", null)
            .create()
        dialog.show()
    }

    private fun updateProgressDisplay(progress: Float) {
        val progressBar = findViewById<ProgressBar>(R.id.determinateBar)
        progressBar.progress = progress.toInt()
    }

    private fun updateRecyclerView(tasks: List<Task>) {
        adapter = TaskAdapter(
            tasks,
            onTaskClicked = { task ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("task", task)
                startActivity(intent)
            },
            onTaskCheckChanged = { task ->
                taskViewModel.updateTask(task.id, task.title, task.description, task.isCompleted)
                Log.d("MainActivity", "${currentFilter}")
                applyFilter(currentFilter)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun applyFilter(filter: Filter) {
        currentFilter = filter
        taskViewModel.setFilter(filter)

        taskViewModel.filteredTasks.observe(this) { tasks ->
            val progress = taskViewModel.getCompletionProgress()
            updateProgressDisplay(progress)
            updateRecyclerView(tasks)
        }
    }
}