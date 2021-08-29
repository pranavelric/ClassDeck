package com.classroom.classdeck.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.databinding.QuizItemBinding


class QuizAdapter :
    ListAdapter<Quiz?, QuizAdapter.QuizViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class QuizViewHolder(private val binding: QuizItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Quiz?, position: Int) {


            binding.courseCodeLabel.text = cp?.courseCode
            binding.quizTitleText.text = cp?.quizTitle
            binding.textDate.text = cp?.startDate + " " + cp?.startTime
            binding.marksText.text = cp?.marks.toString()+ " marks"

            binding.entryBtn.setOnClickListener {
                onEntryBtnClickListener.let { click ->
                    if (click != null) {
                        click(cp, position)
                    }
                }
            }


            binding.rootLayoutQuiz.setOnClickListener {

                onItemClickListener.let { click ->
                    if (click != null) {
                        click(cp, position)
                    }
                }
            }
        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<Quiz?>() {
        override fun areItemsTheSame(
            oldItem: Quiz,
            newItem: Quiz
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Quiz,
            newItem: Quiz
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: QuizItemBinding =
            QuizItemBinding.inflate(layoutInflater, parent, false)
        return QuizViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((Quiz?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Quiz?, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onEntryBtnClickListener: ((Quiz?, Int) -> Unit)? = null
    fun setOnEntryBtnClickListener(listener: (Quiz?, Int) -> Unit) {
        onEntryBtnClickListener = listener
    }


}
