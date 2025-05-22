package com.example.multiviewsapp

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(private val tasks: MutableList<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.checkBox.text = task.name
        holder.checkBox.isChecked = task.isDone

        holder.checkBox.setOnCheckedChangeListener(null) // Prevent flickering

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isDone = isChecked
            holder.checkBox.paintFlags = if (isChecked)
                holder.checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else
                holder.checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // Strike-through if already done
        holder.checkBox.paintFlags = if (task.isDone)
            holder.checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        else
            holder.checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    override fun getItemCount(): Int = tasks.size
}
