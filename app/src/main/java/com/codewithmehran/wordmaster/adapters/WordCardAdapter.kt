package com.codewithmehran.wordmaster.adapters

import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codewithmehran.wordmaster.databinding.ItemWordCardBinding
import com.codewithmehran.wordmaster.model.Word
import java.util.Locale

class WordCardAdapter :
    ListAdapter<Word, WordCardAdapter.WordViewHolder>(DiffCallback) {

    private var textToSpeech: TextToSpeech? = null

    object DiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean =
            oldItem == newItem
    }

    inner class WordViewHolder(private val binding: ItemWordCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.txtWord.text = word.word
            binding.txtMeaning.text = word.meaning

            if (textToSpeech == null) {
                textToSpeech = TextToSpeech(binding.root.context) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        textToSpeech?.language = Locale.US
                    }
                }
            }

            binding.btnListen.setOnClickListener {
                val textToSpeak = word.word
                textToSpeech?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWordCardBinding.inflate(inflater, parent, false)
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}