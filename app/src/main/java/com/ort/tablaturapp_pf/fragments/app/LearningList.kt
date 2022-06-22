package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.view.*
import android.widget.*
import android.widget.AdapterView.AdapterContextMenuInfo
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import kotlin.properties.Delegates


class LearningList : Fragment() {
  lateinit var LearningListView: View
  private lateinit var listView1: ListView
  private lateinit var songs: Array<String>
  private lateinit var ids: Array<String>
  private lateinit var learningListBtn: Button
  private var isEditable by Delegates.notNull<Boolean>()
  private var isPremium: Boolean = false

  private val auth = Firebase.auth
  private val db = Firebase.firestore

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    LearningListView = inflater.inflate(R.layout.fragment_learning_list, container, false)
    listView1 = LearningListView.findViewById(R.id.lv1)
    learningListBtn = LearningListView.findViewById(R.id.learningList_button)

    registerForContextMenu(listView1)
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

  override fun onStart() {
    super.onStart()
    val args = LearningListArgs.fromBundle(requireArguments())
    songs = args.songs;
    ids = args.ids;
    isEditable = args.isEditable
    listView1.adapter =
      ArrayAdapter(LearningListView.context, android.R.layout.simple_list_item_1, songs)
  }

  override fun onCreateContextMenu(
    menu: ContextMenu,
    v: View,
    menuInfo: ContextMenu.ContextMenuInfo?
  ) {
    super.onCreateContextMenu(menu, v, menuInfo)
    if (isEditable) {
      activity?.menuInflater?.inflate(R.menu.options_song_menu, menu)
    }
  }


  override fun onContextItemSelected(item: MenuItem): Boolean {
    val info = item.menuInfo as AdapterContextMenuInfo

    return when (item.itemId) {
      R.id.song_option_delete -> {
        deleteSong(info.position)
        return true
      }
      else -> super.onContextItemSelected(item)
    }
  }

  private fun deleteSong(position: Int) {
    val newSongs = songs.toMutableList()
    newSongs.removeAt(position)
    val newIds = ids.toMutableList()
    newIds.removeAt(position)
    auth.currentUser?.let {
      db.collection("users").document(it.uid)
        .update(
          "learningPathPremium",
          songsToHashMap(newSongs.toTypedArray(), newIds.toTypedArray())
        )
        .addOnSuccessListener {
          songs = newSongs.toTypedArray()
          ids = newIds.toTypedArray()
          Toast.makeText(
            context, "Eliminada.",
            Toast.LENGTH_SHORT
          ).show()
          listView1.adapter =
            ArrayAdapter(LearningListView.context, android.R.layout.simple_list_item_1, songs)
        }
        .addOnFailureListener {
          Toast.makeText(
            context, "Error al eliminar.",
            Toast.LENGTH_SHORT
          ).show()
        }
    }
  }

  private fun songsToHashMap(
    songs: Array<String>,
    ids: Array<String>
  ): ArrayList<HashMap<String, String>> {
    val hashMapSongs = arrayListOf<HashMap<String, String>>()
    for (idx in songs.indices) {
      val id = ids[idx];
      val song = songs[idx];
      val songWithoutSpaces = song.replace("\\s".toRegex(), "");
      val songArray = songWithoutSpaces.split("-")
      val hashMapSong = hashMapOf(
        "id" to id,
        "name" to songArray[0],
        "artist" to songArray[1]
      )
      hashMapSongs.add(hashMapSong);
    }
    return hashMapSongs
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