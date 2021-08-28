package com.classroom.classdeck.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classroom.classdeck.data.model.Announcements
import com.classroom.classdeck.databinding.NotifItemBinding

import kotlin.random.Random


class AnnouncementAdapter :
    ListAdapter<Announcements?, AnnouncementAdapter.AnnouncementsViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class AnnouncementsViewHolder(private val binding: NotifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Announcements?, position: Int) {


            binding.notifTitle.text = cp?.title
            binding.notfiMsg.text = cp?.message
            binding.notifTime.text = cp?.time
            binding.notifDateText.text = cp?.date


            binding.notifCard.setOnClickListener {

                onItemClickListener.let { click ->
                    if (click != null) {
                        click(cp, position)
                    }
                }
            }

        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<Announcements?>() {
        override fun areItemsTheSame(
            oldItem: Announcements,
            newItem: Announcements
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Announcements,
            newItem: Announcements
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: NotifItemBinding =
            NotifItemBinding.inflate(layoutInflater, parent, false)
        return AnnouncementsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnouncementsViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((Announcements?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Announcements?, Int) -> Unit) {
        onItemClickListener = listener
    }


}
