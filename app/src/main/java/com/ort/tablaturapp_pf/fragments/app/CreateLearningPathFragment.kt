package com.ort.tablaturapp_pf.fragments.app

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.CreateLearningPathViewModel

class CreateLearningPathFragment : Fragment() {

    lateinit var createLearningPathView: View
    lateinit var instrumentSpn: Spinner

    companion object {
        fun newInstance() = CreateLearningPathFragment()
    }

    private lateinit var viewModel: CreateLearningPathViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createLearningPathView =
            inflater.inflate(R.layout.create_learning_path_fragment, container, false)
        instrumentSpn = createLearningPathView.findViewById(R.id.instrumentSpn)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.instruments_list,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            instrumentSpn.adapter = adapter
        }
        return createLearningPathView;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateLearningPathViewModel::class.java)
        // TODO: Use the ViewModel
    }

}