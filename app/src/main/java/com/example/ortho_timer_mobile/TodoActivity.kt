package com.example.ortho_timer_mobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TodoActivity : AppCompatActivity() {

    private lateinit var taskInput: EditText
    private lateinit var addTaskBtn: Button
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        taskInput = findViewById(R.id.taskInput)
        addTaskBtn = findViewById(R.id.addTaskBtn)
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)

        adapter = TodoAdapter { position ->
            adapter.removeItem(position)
        }

        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksRecyclerView.adapter = adapter

        addTaskBtn.setOnClickListener {
            val task = taskInput.text.toString().trim()
            if (task.isNotEmpty()) {
                adapter.addItem(task)
                taskInput.text.clear()
            }
        }
    }
}