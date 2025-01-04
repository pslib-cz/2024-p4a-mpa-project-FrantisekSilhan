package com.example.mpaproject
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskClicked: (Task) -> Unit,
    private val onTaskCheckChanged: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.taskTitle)
        val checkBox: CheckBox = view.findViewById(R.id.taskCheckBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        Log.d("TaskAdapter", "Binding task: ${task.title}, isCompleted: ${task.isCompleted}")
        holder.title.text = task.title
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.isCompleted

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            Log.d("TaskAdapter", "Checkbox for task '${task.title}' changed to $isChecked")
            onTaskCheckChanged(task.copy(isCompleted = isChecked))
        }

        holder.itemView.setOnClickListener {
            if (!holder.checkBox.isPressed) {
                Log.d("TaskAdapter", "Task clicked: ${task.title}")
                onTaskClicked(task)
            }
        }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        val diffCallback = TaskDiffCallback(tasks, newTasks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tasks = newTasks
        diffResult.dispatchUpdatesTo(this)
    }
}