package com.codewithmehran.wordmaster.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.codewithmehran.wordmaster.R
import com.codewithmehran.wordmaster.databinding.ItemWordCardBinding
import com.codewithmehran.wordmaster.model.Word
import com.codewithmehran.wordmaster.view.dialogs.QuizDialog
import com.google.firebase.Timestamp

class SwipeAbleWordAdapter(
    private val context: Context,
    data: List<Word>?
) : BaseAdapter() {

    private var dataList = mutableListOf<Word>()
    private var onListenClick: ((String) -> Unit)? = null
    private var onCardAnimationListener: ((View, () -> Unit) -> Unit)? = null
    private var onDeckEmpty: (() -> Unit)? = null


    init {
        if (data != null) {
            updateData(data)
            Log.d("CardCountIssue", "Adapter initialized with ${dataList.size} words")
        } else {
            Log.d("CardCountIssue", "Adapter initialized with null data")
        }
    }

    fun setOnListenClickListener(listener: (String) -> Unit) {
        onListenClick = listener
    }

    fun setOnDeckEmptyListener(listener: () -> Unit) {
        onDeckEmpty = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d("CardCountIssue", "getView($position) called - total items: ${dataList.size}")

        // Handle dummy card at position 0 - create empty view
        if (position == 0) {
            // Return empty view that won't be visible
            return View(parent.context).apply {
                visibility = View.GONE
                layoutParams = ViewGroup.LayoutParams(0, 0)
            }
        }

        // For real cards (position >= 1)
        val holder: WordViewHolder
        val view: View

        if (convertView == null || convertView.tag == null) {
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

        // Bind real card data
        holder.bindData(getItem(position), position, onListenClick)
        return view
    }

    fun updateData(newData: List<Word>) {
        Log.d("CardCountIssue", "Adapter updateData called with ${newData.size} items")

        // Clear existing data
        dataList.clear()

        // Add dummy card at index 0 if we have any real cards
        if (newData.isNotEmpty()) {
            val dummy = Word(
                word = "",
                meaning = "",
                synonyms = "",
                antonyms = "",
                exampleSentence = "",
                id = 0,
                source = "other",
                dateAdded = Timestamp.now().toDate()
            )
            dataList.add(dummy)
        }

        // Add all real cards
        dataList.addAll(newData)

        notifyDataSetChanged()

        Log.d("CardCountIssue", "Adapter data updated, new count: ${dataList.size}")
        Log.d("CardCountIssue", "Real item count: ${getRealItemCount()}")
    }

    fun clearData() {
        Log.d("CardCountIssue", "clearData() called")
        dataList.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int { // For Koloda
        return dataList.size
    }

    fun getRealItemCount(): Int {
        // Return only real cards (excluding dummy at index 0)
        return maxOf(0, dataList.size - 1)
    }

    fun getRealItemCountBeforeSwipe(swipedPosition: Int): Int {
        // Calculate how many real cards were there BEFORE the swipe
        // swipedPosition is the position that was just swiped
        // If we have N total items (including dummy), and we swiped at position P,
        // then before swipe we had (N-1) real cards

        // IMPORTANT: Koloda's position parameter might be 1-based for the visible cards
        // Let's use the current dataList size to determine

        if (dataList.isEmpty()) return 0

        // Before any swipe, total items = dataList.size
        // Real cards = dataList.size - 1 (minus dummy)
        return maxOf(0, dataList.size - 1)
    }


    override fun getItem(position: Int): Word {
        Log.d("CardCountIssue", "getItem($position) called")
        if (position == 0) return Word(0.toLong(),"", "", "", "", "", "other",Timestamp.now().toDate())  // dummy
        return dataList[position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    fun getAllWords(): List<Word> {
        return dataList
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
                    context.getString(R.string.quiz_unlock_requirement),
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
                    context.getString(R.string.not_enough_meanings),
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
                context.getString(R.string.quiz_meaning_question, currentWord.word),
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
                    context.getString(R.string.quiz_unlock_requirement),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (currentWord.synonyms.isNullOrBlank()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.word_no_synonyms),
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
                    context.getString(R.string.not_enough_synonyms),
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
                context.getString(R.string.quiz_synonyms_question, currentWord.word),
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
                    context.getString(R.string.quiz_unlock_requirement),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (currentWord.antonyms.isNullOrBlank()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.word_no_antonyms),
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
                    context.getString(R.string.not_enough_antonyms),
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
                context.getString(R.string.quiz_antonyms_question, currentWord.word),
                allOptions[0],
                allOptions[1],
                allOptions[2],
                allOptions[3],
                correctAnswer
            )
        }
    }
}