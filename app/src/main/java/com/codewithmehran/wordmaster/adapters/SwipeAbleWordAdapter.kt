package com.codewithmehran.wordmaster.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.codewithmehran.wordmaster.databinding.ItemWordCardBinding
import com.codewithmehran.wordmaster.model.Word
import com.codewithmehran.wordmaster.view.dialogs.QuizDialog

class SwipeAbleWordAdapter(
    private val context: Context,
    data: List<Word>?
) : BaseAdapter() {

    private var dataList = mutableListOf<Word>()
    private var onListenClick: ((String) -> Unit)? = null
    private var onCardAnimationListener: ((View, () -> Unit) -> Unit)? = null

    init {
        if (data != null) {
            dataList.addAll(data)
            Log.d("CardCountIssue", "Adapter initialized with ${dataList.size} words")
        } else {
            Log.d("CardCountIssue", "Adapter initialized with null data")
        }
    }

    fun setOnListenClickListener(listener: (String) -> Unit) {
        onListenClick = listener
    }

    fun setOnCardAnimationListener(listener: (View, () -> Unit) -> Unit) {
        onCardAnimationListener = listener
    }

    override fun getCount(): Int {
        Log.d("CardCountIssue", "getCount() called - returning ${dataList.size}")
        return dataList.size
    }

    override fun getItem(position: Int): Word {
        Log.d("CardCountIssue", "getItem($position) called")
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d("CardCountIssue", "getView($position) called - total items: ${dataList.size}")

        val holder: WordViewHolder
        val view: View

        if (convertView == null) {
            Log.d("CardCountIssue", "Creating new view for position $position")
            val binding = ItemWordCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            view = binding.root
            holder = WordViewHolder(binding, context, this, onCardAnimationListener)
            view.tag = holder
        } else {
            Log.d("CardCountIssue", "Reusing existing view for position $position")
            view = convertView
            holder = view.tag as WordViewHolder
        }

        holder.bindData(getItem(position), position, onListenClick)
        return view
    }

    fun getAllWords(): List<Word> {
        return dataList
    }

    fun updateData(newData: List<Word>) {
        this.dataList = newData as MutableList<Word>
        notifyDataSetChanged()
    }

    class WordViewHolder(
        private val binding: ItemWordCardBinding,
        private val context: Context,
        private val adapter: SwipeAbleWordAdapter,
        private val animationListener: ((View, () -> Unit) -> Unit)?
    ) {

        private var currentPosition = -1

        fun bindData(
            word: Word,
            position: Int,
            onListenClick: ((String) -> Unit)?
        ) {
            currentPosition = position
            Log.d("CardCountIssue", "Binding data for position $position - Word: ${word.word}")

            binding.txtWord.text = word.word
            binding.txtSentence.text = word.exampleSentence

            binding.btnListen.setOnClickListener {
                onListenClick?.invoke(word.word)
            }

            binding.btnMeaning.setOnClickListener {
                handleMeaningClick(word, binding.root)
            }

            binding.btnSynonym.setOnClickListener {
                handleSynonymClick(word, binding.root)
            }

            binding.btnAntonym.setOnClickListener {
                handleAntonymClick(word, binding.root)
            }
        }

        private fun showQuizAfterAnimation(
            cardView: View,
            question: String,
            option1: String,
            option2: String,
            option3: String,
            option4: String,
            correctAnswer: Int
        ) {
            animationListener?.invoke(cardView) {
                QuizDialog(
                    context = context,
                    question = question,
                    option1 = option1,
                    option2 = option2,
                    option3 = option3,
                    option4 = option4,
                    correctAnswer = correctAnswer
                ).show()
            }
        }

        private fun handleMeaningClick(currentWord: Word, cardView: View) {
            val allWords = adapter.getAllWords()

            if (allWords.size < 5) {
                Toast.makeText(
                    context,
                    "Add at least 5 words before you can unlock quizzes",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val wrongMeanings = allWords
                .filter { it.word != currentWord.word && it.meaning.isNotBlank() }
                .map { it.meaning }
                .shuffled()
                .take(3)

            if (wrongMeanings.size < 3) {
                Toast.makeText(
                    context,
                    "Not enough words with meanings for quiz",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val allOptions = mutableListOf(currentWord.meaning).apply {
                addAll(wrongMeanings)
            }.shuffled()

            val correctAnswer = allOptions.indexOf(currentWord.meaning) + 1

            showQuizAfterAnimation(
                cardView,
                "What is the meaning of '${currentWord.word}'?",
                allOptions[0],
                allOptions[1],
                allOptions[2],
                allOptions[3],
                correctAnswer
            )
        }

        private fun handleSynonymClick(currentWord: Word, cardView: View) {
            val allWords = adapter.getAllWords()

            if (allWords.size < 5) {
                Toast.makeText(
                    context,
                    "Add at least 5 words before you can unlock quizzes",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (currentWord.synonyms.isNullOrBlank()) {
                Toast.makeText(
                    context,
                    "This word has no synonyms",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val wrongSynonyms = allWords
                .filter { it.word != currentWord.word && !it.synonyms.isNullOrBlank() }
                .map { it.synonyms!! }
                .shuffled()
                .take(3)

            if (wrongSynonyms.size < 3) {
                Toast.makeText(
                    context,
                    "Not enough words with synonyms for quiz",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val allOptions = mutableListOf(currentWord.synonyms!!).apply {
                addAll(wrongSynonyms)
            }.shuffled()

            val correctAnswer = allOptions.indexOf(currentWord.synonyms) + 1

            showQuizAfterAnimation(
                cardView,
                "What are the synonyms of '${currentWord.word}'?",
                allOptions[0],
                allOptions[1],
                allOptions[2],
                allOptions[3],
                correctAnswer
            )
        }

        private fun handleAntonymClick(currentWord: Word, cardView: View) {
            val allWords = adapter.getAllWords()

            if (allWords.size < 5) {
                Toast.makeText(
                    context,
                    "Add at least 5 words before you can unlock quizzes",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (currentWord.antonyms.isNullOrBlank()) {
                Toast.makeText(
                    context,
                    "This word has no antonyms",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val wrongAntonyms = allWords
                .filter { it.word != currentWord.word && !it.antonyms.isNullOrBlank() }
                .map { it.antonyms!! }
                .shuffled()
                .take(3)

            if (wrongAntonyms.size < 3) {
                Toast.makeText(
                    context,
                    "Not enough words with antonyms for quiz",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val allOptions = mutableListOf(currentWord.antonyms!!).apply {
                addAll(wrongAntonyms)
            }.shuffled()

            val correctAnswer = allOptions.indexOf(currentWord.antonyms) + 1

            showQuizAfterAnimation(
                cardView,
                "What are the antonyms of '${currentWord.word}'?",
                allOptions[0],
                allOptions[1],
                allOptions[2],
                allOptions[3],
                correctAnswer
            )
        }
    }
}