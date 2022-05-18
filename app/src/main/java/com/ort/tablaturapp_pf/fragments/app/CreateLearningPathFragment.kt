package com.ort.tablaturapp_pf.fragments.app

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
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

    override fun onStart() {
        super.onStart()
        val selectedGenres = mutableListOf<String>()
        val checkBoxGenres = getCheckBoxGenres()
        checkBoxGenres.forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (selectedGenres.size < 3) {
                        selectedGenres.add(it.text.toString().lowercase())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Máximo 3 géneros!",
                            Toast.LENGTH_SHORT
                        ).show()
                        it.isChecked = false;
                    }
                } else{
                    selectedGenres.remove(it.text.toString().lowercase())
                }
            }
        }
    }

    private fun getCheckBoxGenres(): MutableList<CheckBox> {
        val checkBoxGenres = mutableListOf<CheckBox>()
        val v = createLearningPathView;
        checkBoxGenres.add(v.findViewById(R.id.bluesCbx))
        checkBoxGenres.add(v.findViewById(R.id.clasicaCbx))
        checkBoxGenres.add(v.findViewById(R.id.cumbiaCbx))
        checkBoxGenres.add(v.findViewById(R.id.funkCbx))
        checkBoxGenres.add(v.findViewById(R.id.jazzCbx))
        checkBoxGenres.add(v.findViewById(R.id.metalCbx))
        checkBoxGenres.add(v.findViewById(R.id.popCbx))
        checkBoxGenres.add(v.findViewById(R.id.punkCbx))
        checkBoxGenres.add(v.findViewById(R.id.rapCbx))
        checkBoxGenres.add(v.findViewById(R.id.reggaetonCbx))
        checkBoxGenres.add(v.findViewById(R.id.rockCbx))
        checkBoxGenres.add(v.findViewById(R.id.reggaeCbx))
        return checkBoxGenres;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateLearningPathViewModel::class.java)
        // TODO: Use the ViewModel
    }

}