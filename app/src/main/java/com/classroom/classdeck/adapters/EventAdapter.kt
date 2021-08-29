package com.classroom.classdeck.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classroom.classdeck.data.model.Event
import com.classroom.classdeck.databinding.EventItemBinding


class EventAdapter :
    ListAdapter<Event?, EventAdapter.EventViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class EventViewHolder(private val binding: EventItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Event?, position: Int) {


            binding.eventName.text = cp?.title
            binding.eventDesc.text = cp?.message
            binding.eventDate.text = cp?.eventDate
            binding.eventTime.text = cp?.eventTime
            binding.courseCode.text = cp?.courseCode
            binding.teacherName.text = cp?.teacherName

            binding.notifCard.setOnClickListener {

                onItemClickListener.let { click ->
                    if (click != null) {
                        click(cp, position)
                    }
                }
            }

        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<Event?>() {
        override fun areItemsTheSame(
            oldItem: Event,
            newItem: Event
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Event,
            newItem: Event
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: EventItemBinding =
            EventItemBinding.inflate(layoutInflater, parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((Event?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Event?, Int) -> Unit) {
        onItemClickListener = listener
    }


}
