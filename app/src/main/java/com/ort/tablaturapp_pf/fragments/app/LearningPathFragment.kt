package com.ort.tablaturapp_pf.fragments.app

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.LearningPathViewModel

class LearningPathFragment : Fragment() {

    companion object {
        fun newInstance() = LearningPathFragment()
    }

    private lateinit var viewModel: LearningPathViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.learning_path_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LearningPathViewModel::class.java)
        // TODO: Use the ViewModel
    }

}