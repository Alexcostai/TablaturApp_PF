package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.LearningPathViewModel
import kotlin.collections.ArrayList

class LearningPathFragment : Fragment() {

  lateinit var lpCardView: CardView
  lateinit var lpCreateCardView: CardView
  lateinit var lppCardView: CardView
  lateinit var lppCreateCardView: CardView
  lateinit var learningPathView: View
  lateinit var goToLearningPath: ImageView
  lateinit var goToLearningPathPremium: ImageView

  private val db = Firebase.firestore
  private val auth = Firebase.auth
  private var learningPathSongs = arrayListOf<HashMap<String, Any?>>()
  private var learningPathPremiumSongs = arrayListOf<HashMap<String, Any?>>()
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
    goToLearningPath = learningPathView.findViewById(R.id.imageView4)
    goToLearningPathPremium = learningPathView.findViewById(R.id.imageView3)
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
              val arguments = Bundle()
              arguments.putBoolean("isPremium", true)
              goToFragment(CreateLearningPathFragment(), arguments)
            } else {
              goToFragment(SubscriptionFragment(), null)
            }
          }
          lpCreateCardView.setOnClickListener {
            val arguments = Bundle()
            arguments.putBoolean("isPremium", false)
            goToFragment(CreateLearningPathFragment(), arguments)
          }
          learningPathSongs = user!!["learningPath"] as ArrayList<HashMap<String, Any?>>
          learningPathPremiumSongs =
            user!!["learningPathPremium"] as ArrayList<HashMap<String, Any?>>
          lpCreateCardView.isVisible = learningPathSongs.size == 0;
          lpCardView.isVisible = learningPathSongs.size != 0;
          lppCreateCardView.isVisible = learningPathPremiumSongs.size == 0;
          lppCardView.isVisible = learningPathPremiumSongs.size != 0;
          goToLearningPath.setOnClickListener {
            val arguments = songsToArguments(learningPathSongs)
            arguments.putBoolean("isEditable", false);
            goToFragment(LearningList(), arguments);
          }
          goToLearningPathPremium.setOnClickListener {
            val arguments = songsToArguments(learningPathPremiumSongs)
            arguments.putBoolean("isEditable", user!!["isPremium"] as Boolean);
            goToFragment(LearningList(), arguments);
          }
        }
      }
    }
  }

  private fun songsToArguments(songs: ArrayList<HashMap<String, Any?>>): Bundle {
    val songsTitles = Array<String>(songs.size) { "" }
    val ids = Array<String>(songs.size) { "" }
    val arguments = Bundle()
    for (idx in 0 until songs.size) {
      val song = songs[idx];
      songsTitles[idx] = ("${song["name"]} - ${song["artist"]}");
      song["id"]?.let { it1 -> ids[idx] = (it1 as String) }
    }
    arguments.putStringArray("songs", songsTitles);
    arguments.putStringArray("ids", ids);
    return arguments
  }

  private fun goToFragment(fragment: Fragment, arguments: Bundle?) {
    parentFragmentManager.beginTransaction().apply {
      val clpFragment: Fragment = fragment
      clpFragment.arguments = arguments
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