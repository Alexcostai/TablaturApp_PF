package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.LearningPathViewModel
import java.util.*
import kotlin.collections.ArrayList

class LearningPathFragment : Fragment() {

  lateinit var lpCardView: CardView
  lateinit var lpCreateCardView: CardView
  lateinit var lppCardView: CardView
  lateinit var lppCreateCardView: CardView
  lateinit var learningPathView: View

  private val db = Firebase.firestore
  private val auth = Firebase.auth
  private var learningPathSongs = arrayListOf<String>()
  private var learningPathPremiumSongs = arrayListOf<String>()
  private var user: MutableMap<String, Object>? = null

  companion object {
    fun newInstance() = LearningPathFragment()
  }

  private lateinit var viewModel: LearningPathViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    learningPathView = inflater.inflate(R.layout.learning_path_fragment, container, false)
    lpCardView = learningPathView.findViewById(R.id.lpCardView)
    lpCreateCardView = learningPathView.findViewById(R.id.lpCreateCardView)
    lppCardView = learningPathView.findViewById(R.id.lppCardView)
    lppCreateCardView = learningPathView.findViewById(R.id.lppCreateCardView)
    return learningPathView
  }

  override fun onStart() {
    super.onStart()
    auth.currentUser?.let {
      db.collection("users").document(it.uid).get().addOnSuccessListener {
        user = it.data as MutableMap<String, Object>?
        if (user != null) {
          lppCreateCardView.setOnClickListener {
            if (user!!["isPremium"] as Boolean) {
              goToFragment(CreateLearningPathFragment(), true)
            } else {
              goToFragment(SubscriptionFragment())
            }
          }
          lpCreateCardView.setOnClickListener {
            goToFragment(CreateLearningPathFragment(), false)
          }
          learningPathSongs = user!!["learningPath"] as ArrayList<String>
          learningPathPremiumSongs = user!!["learningPathPremium"] as ArrayList<String>
          lpCreateCardView.isVisible = learningPathSongs.size == 0;
          lpCardView.isVisible = learningPathSongs.size != 0;
          lppCreateCardView.isVisible = learningPathPremiumSongs.size == 0;
          lppCardView.isVisible = learningPathPremiumSongs.size != 0;
        }
      }
    }
  }

  private fun goToFragment(fragment: Fragment, isPremium: Boolean) {
    parentFragmentManager.beginTransaction().apply {
      val clpFragment: Fragment = fragment
      val arguments = Bundle()
      arguments.putBoolean("isPremium", isPremium)
      clpFragment.arguments = arguments
      replace(R.id.navAppController, clpFragment)
      commit()
    }
  }

  private fun goToFragment(fragment: Fragment) {
    parentFragmentManager.beginTransaction().apply {
      val clpFragment: Fragment = fragment
      replace(R.id.navAppController, clpFragment)
      commit()
    }
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(LearningPathViewModel::class.java)
    // TODO: Use the ViewModel
  }

}