package com.ort.tablaturapp_pf.fragments.app

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.SongViewModel

class SongFragment : Fragment() {

    companion object {
        fun newInstance() = SongFragment()
    }

    private lateinit var viewModel: SongViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.song_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SongViewModel::class.java)
        // TODO: Use the ViewModel
    }

}