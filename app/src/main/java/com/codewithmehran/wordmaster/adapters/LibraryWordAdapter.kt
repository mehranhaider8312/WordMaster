package com.codewithmehran.wordmaster.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codewithmehran.wordmaster.databinding.ItemLibraryWordBinding
import com.codewithmehran.wordmaster.model.Word

class LibraryWordAdapter(
    private val onEditClick: (Word) -> Unit,
    private val onDeleteClick: (Word) -> Unit
) : ListAdapter<Word, LibraryWordAdapter.WordViewHolder>(WordDiffCallback()) {

    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemLibraryWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position), position == expandedPosition)
    }

    inner class WordViewHolder(
        private val binding: ItemLibraryWordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word, isExpanded: Boolean) {
            binding.txtWord.text = word.word
            binding.txtMeaning.text = word.meaning

            binding.expandedLayout.visibility = if (isExpanded) android.view.View.VISIBLE else android.view.View.GONE

            if (isExpanded) {
                binding.txtFullMeaning.text = word.meaning
                binding.txtSynonyms.text = word.synonyms.ifEmpty { "No synonyms" }
                binding.txtAntonyms.text = word.antonyms.ifEmpty { "No antonyms" }
                binding.txtExampleSentence.text = if (word.exampleSentence.isNotEmpty()) {
                    "\"${word.exampleSentence}\""
                } else {
                    "No example sentence"
                }
            }

            binding.btnEdit.setOnClickListener {
                onEditClick(word)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(word)
            }

//            binding.root.setOnLongClickListener {   }

            binding.root.setOnClickListener {
                val previousExpanded = expandedPosition
                expandedPosition = if (isExpanded) -1 else bindingAdapterPosition

                if (previousExpanded != -1) {
                    notifyItemChanged(previousExpanded)
                }
                notifyItemChanged(bindingAdapterPosition)

                true
            }
        }
    }

    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
}