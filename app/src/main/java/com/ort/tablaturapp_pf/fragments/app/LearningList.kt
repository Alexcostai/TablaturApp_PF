package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R


class LearningList : Fragment() {
  lateinit var LearningListView: View
  private lateinit var listView1: ListView
  private lateinit var songs : Array<String>
  private lateinit var ids : Array<String>
  private lateinit var learningListBtn: Button
  private var isPremium: Boolean = false

  private val auth = Firebase.auth;
  private val db = Firebase.firestore

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    LearningListView = inflater.inflate(R.layout.fragment_learning_list, container, false)
    listView1 = LearningListView.findViewById(R.id.lv1)
    learningListBtn = LearningListView.findViewById(R.id.learningList_button)

    return LearningListView
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener {
      user ->
      if (user != null) {
        isPremium = user["isPremium"] as Boolean
      }
    }

    listView1.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
      goToFragment(SongFragment(), ids[position])
    }
    learningListBtn.setOnClickListener {
      goToFragment(CreateLearningPathFragment(), isPremium)
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

  private fun goToFragment(fragment: Fragment, isPremium: Boolean){
    parentFragmentManager.beginTransaction().apply {
      val clpFragment: Fragment = fragment
      val arguments = Bundle()
      arguments.putBoolean("isPremium", isPremium)
      clpFragment.arguments = arguments
      replace(R.id.navAppController, clpFragment)
      commit()
    }
  }
}