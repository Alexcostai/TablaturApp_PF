package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ort.tablaturapp_pf.R


class LearningList : Fragment() {
  lateinit var LearningListView: View
  lateinit var listView1: ListView
  lateinit var songs : Array<String>
  lateinit var ids : Array<String>

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    LearningListView = inflater.inflate(R.layout.fragment_learning_list, container, false)
    listView1 = LearningListView.findViewById(R.id.lv1)

    return LearningListView
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    listView1.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
      goToFragment(SongFragment(), ids[position])
    }
  }

  companion object {
    fun newInstance() = LearningList()
  }

  override fun onStart(){
    super.onStart()
    val args = LearningListArgs.fromBundle(requireArguments())
    songs = args.songs;
    ids = args.ids;
    listView1.adapter = ArrayAdapter(LearningListView.context, android.R.layout.simple_list_item_1, songs)
  }

  private fun goToFragment(fragment: Fragment, song_id: String) {
    parentFragmentManager.beginTransaction().apply {
      val clpFragment: Fragment = fragment
      val arguments = Bundle()
      arguments.putString("song_id", song_id)
      clpFragment.arguments = arguments
      replace(R.id.navAppController, clpFragment)
      commit()
    }
  }
}