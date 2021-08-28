package com.classroom.classdeck.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classroom.classdeck.data.model.Course
import com.classroom.classdeck.databinding.CourseCardBinding
import kotlin.random.Random


class CourseAdapter :
    ListAdapter<Course?, CourseAdapter.CourseViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class CourseViewHolder(private val binding: CourseCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Course?, position: Int) {


            binding.courseCodeLabel.setText(cp?.courseCode)
            binding.courseNameText.setText(cp?.courseName)
            binding.joinedText.setText(cp?.studentCount.toString())
            binding.createdByText.setText("By " + cp?.createdBy)

            binding.rootLayoutCourse.setBackgroundColor(getRandomColorCode())

            binding.rootLayoutCourse.setOnClickListener {

                onItemClickListener.let { click ->
                    if (click != null) {
                        click(cp, position)
                    }
                }
            }

        }

    }

    private fun getRandomColorCode(): Int {
        val random = Random(System.currentTimeMillis());

        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    open class MyDiffutilCallback : DiffUtil.ItemCallback<Course?>() {
        override fun areItemsTheSame(
            oldItem: Course,
            newItem: Course
        ): Boolean {
            return oldItem.courseCode == newItem.courseCode
        }

        override fun areContentsTheSame(
            oldItem: Course,
            newItem: Course
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: CourseCardBinding =
            CourseCardBinding.inflate(layoutInflater, parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((Course?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Course?, Int) -> Unit) {
        onItemClickListener = listener
    }


}
