package com.classroom.classdeck.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classroom.classdeck.data.model.Assignments
import com.classroom.classdeck.databinding.NotifItemBinding
import com.classroom.classdeck.util.visible


class AssignmentAdapter :
    ListAdapter<Assignments?, AssignmentAdapter.AssignmentsViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class AssignmentsViewHolder(private val binding: NotifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Assignments?, position: Int) {


            binding.notifTitle.text = cp?.title
            binding.notfiMsg.text = cp?.message
            binding.notifTime.text = cp?.time
            binding.notifDateText.text = cp?.date

            binding.submitTo.visible()
            binding.submitTo.text = "Send your assignment to: " + cp?.submitTo

            binding.notifCard.setOnClickListener {

                onItemClickListener.let { click ->
                    if (click != null) {
                        click(cp, position)
                    }
                }
            }

        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<Assignments?>() {
        override fun areItemsTheSame(
            oldItem: Assignments,
            newItem: Assignments
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Assignments,
            newItem: Assignments
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: NotifItemBinding =
            NotifItemBinding.inflate(layoutInflater, parent, false)
        return AssignmentsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentsViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((Assignments?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Assignments?, Int) -> Unit) {
        onItemClickListener = listener
    }


}
