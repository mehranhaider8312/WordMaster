package com.codewithmehran.wordmaster.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.codewithmehran.wordmaster.R

class IntroFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESC = "arg_desc"
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(title: String, description: String, imageResId: Int): IntroFragment {
            val fragment = IntroFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESC, description)
            args.putInt(ARG_IMAGE, imageResId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        val title = arguments?.getString(ARG_TITLE)
        val desc = arguments?.getString(ARG_DESC)
        val imageResId = arguments?.getInt(ARG_IMAGE) ?: 0

        view.findViewById<TextView>(R.id.txtTitle).text = title
        view.findViewById<TextView>(R.id.txtDescription).text = desc
        if (imageResId != 0) {
            view.findViewById<ImageView>(R.id.imgIntro).setImageResource(imageResId)
        }
        return view
    }
}
