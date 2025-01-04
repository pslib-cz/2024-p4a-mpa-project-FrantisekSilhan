package com.example.mpaproject

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class DetailActivity : AppCompatActivity() {

    private lateinit var task: Task
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("task", Task::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("task")!!
        }

        val titleEditText: EditText = findViewById(R.id.editTaskTitle)
        val descriptionEditText: EditText = findViewById(R.id.editTaskDescription)
        val updateButton: Button = findViewById(R.id.updateButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)
        val backButton: Button = findViewById(R.id.backButton)

        titleEditText.setText(task.title)
        descriptionEditText.setText(task.description)

        backButton.setOnClickListener {
            finish()
        }

        updateButton.setOnClickListener {
            val newTitle = titleEditText.text.toString()
            val newDescription = descriptionEditText.text.toString()
            if (newTitle.isNotEmpty()) {
                taskViewModel.updateTask(task.id, newTitle, newDescription, task.isCompleted)
                finish()
            }
        }

        deleteButton.setOnClickListener {
            taskViewModel.deleteTask(task)
            finish()
        }
    }
}
