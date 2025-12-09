package com.example.ortho_timer_mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<TodoItem>()

    data class TodoItem(
        val text: String,
        var isCompleted: Boolean = false
    )

    fun addItem(text: String) {
        tasks.add(TodoItem(text))
        notifyItemInserted(tasks.size - 1)
    }

    fun removeItem(position: Int) {
        if (position < tasks.size) {
            tasks.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], position)
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckBox)
        private val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)

        fun bind(item: TodoItem, position: Int) {
            checkBox.text = item.text
            checkBox.isChecked = item.isCompleted

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                tasks[position].isCompleted = isChecked
            }

            deleteBtn.setOnClickListener {
                onDelete(position)
            }
        }
    }
}