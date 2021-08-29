package com.classroom.classdeck.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classroom.classdeck.data.model.Question
import com.classroom.classdeck.databinding.QaItemBinding


class QuestionAdapter :
    ListAdapter<Question?, QuestionAdapter.QuestionViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class QuestionViewHolder(private val binding: QaItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: Question?, position: Int) {

            binding.correctAnswer.setText(cp?.answer)
            binding.qaIndex.setText("Q ${cp?.period}.")
            binding.questionText.setText(cp?.question)
            binding.incorrectAnswerA.setText(cp?.optionB)
            binding.incorrectAnswerB.setText(cp?.optionC)
            binding.incorrectAnswerC.setText(cp?.optionD)


        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<Question?>() {
        override fun areItemsTheSame(
            oldItem: Question,
            newItem: Question
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Question,
            newItem: Question
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: QaItemBinding =
            QaItemBinding.inflate(layoutInflater, parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    private var onItemClickListener: ((Question?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (Question?, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onEntryBtnClickListener: ((Question?, Int) -> Unit)? = null
    fun setOnEntryBtnClickListener(listener: (Question?, Int) -> Unit) {
        onEntryBtnClickListener = listener
    }


}
