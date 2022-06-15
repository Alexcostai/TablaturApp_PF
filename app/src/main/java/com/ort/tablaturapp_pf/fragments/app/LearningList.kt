package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.common.primitives.Shorts.asList
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.HomeViewModel
import io.grpc.internal.JsonUtil
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import java.util.Arrays
import java.util.Arrays.asList


class LearningList : Fragment() {
  private val auth = Firebase.auth
  lateinit var LearningListView: View
  lateinit var listView1: ListView
  lateinit var txt: TextView
  val songs = arrayListOf<Bundle>()






  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    LearningListView = inflater.inflate(R.layout.fragment_learning_list, container, false)
    listView1 = LearningListView.findViewById(R.id.lv1)
    txt = LearningListView.findViewById(R.id.textView2)


    return LearningListView
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    listView1.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
      goToFragment(SongFragment(), songs.get(position).getString("id").toString())
    }
  }


  companion object {
    fun newInstance() = LearningList()
  }

  override fun onStart(){
    super.onStart()
    //getData(listView1)
    songsInListView((listView1))
  }

  private fun getData(listView: ListView) {
    val queue = Volley.newRequestQueue(this.context)
    val junto = mutableListOf<String>()
    var artist: String
    var name: String
    val db = Firebase.firestore
    var nameSong: String
    val us = auth.currentUser
    var array = arrayOf<String>()


    db.collection("users")
      .get()
      .addOnSuccessListener { result ->
        for (user in result){
          if (user.id == us?.uid){
            val st = user.data.get("learningPath").toString()
            Log.w("hola", user.data.get("learningPath").toString())
            array = st.split("[","]",", ").toTypedArray()
            for (i in array.indices){
              Log.w("h", array[i].toString())
            }
          }
        }
      }
      .addOnFailureListener { exception ->
        Log.w("HOLA", "Error getting documents.", exception)
      }


   // Log.w("hhh", "https://www.songsterr.com/a/wa/song?id=${array[1]}")
    val url = "https://www.songsterr.com/a/ra/songs.json?pattern=Bad%Bunny"


    val jsonArrayRequest = JsonArrayRequest(
      Request.Method.GET, url, null, { response ->
        for (i in 0 until response.length()) {
          val obj = response.getJSONObject(i)
          name = obj.getString("title")
          artist = obj.getJSONObject("artist").getString("nameWithoutThePrefix")
          nameSong = "$name - $artist"
          junto.add(nameSong)
        }
        listView.adapter =
          ArrayAdapter(LearningListView.context, android.R.layout.simple_list_item_1, junto)
      },
      { txt.text = " ERROR" })
    queue.add(jsonArrayRequest)
  }

  fun songsInListView(listView: ListView){
    val song = Bundle()
    val names = arrayListOf<String>()
    var name: String
    song.putString("id", "16946")
    song.putString("artist", "The Rolling Stones")
    song.putString("name", "Tumbling Dice")
    for (i in 0..10){
      songs.add(song)
      name = "${song.get("name")} - ${song.get("artist")}"
      names.add(name)
    }
    listView.adapter = ArrayAdapter(LearningListView.context, android.R.layout.simple_list_item_1, names)
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