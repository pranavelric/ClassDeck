package com.classroom.classdeck.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.classroom.classdeck.data.model.Quiz
import com.classroom.classdeck.data.model.RankingModel
import com.classroom.classdeck.databinding.LeaderboardItemBinding
import com.classroom.classdeck.databinding.QuizItemBinding


class LeaderBoardAdapter :
    ListAdapter<RankingModel?, LeaderBoardAdapter.QuizViewHolder>(MyDiffutilCallback()) {

    lateinit var context: Context

    inner class QuizViewHolder(private val binding: LeaderboardItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cp: RankingModel?, position: Int) {


            binding.rankText.text = (cp?.rank?.plus(1)).toString()
            binding.userEmail.text = cp?.userEmail
            binding.userName.text = cp?.userName
            binding.userScore.text = cp?.score.toString()

        }

    }


    open class MyDiffutilCallback : DiffUtil.ItemCallback<RankingModel?>() {
        override fun areItemsTheSame(
            oldItem: RankingModel,
            newItem: RankingModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RankingModel,
            newItem:RankingModel
        ): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        context = parent.context
        val binding: LeaderboardItemBinding =
            LeaderboardItemBinding.inflate(layoutInflater, parent, false)
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


    private var onItemClickListener: ((RankingModel?, Int) -> Unit)? = null
    fun setOnItemClickListener(listener: (RankingModel?, Int) -> Unit) {
        onItemClickListener = listener
    }


    private var onEntryBtnClickListener: ((RankingModel?, Int) -> Unit)? = null
    fun setOnEntryBtnClickListener(listener: (RankingModel?, Int) -> Unit) {
        onEntryBtnClickListener = listener
    }


}
