package com.ort.tablaturapp_pf.fragments.app

import Cancion
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    lateinit var homeView: View
    lateinit var btn: Button
    lateinit var listView1: ListView
    lateinit var listView2: ListView
    lateinit var btn2: Button
    lateinit var txt: TextView
    val songs = mutableListOf<Cancion>()
    var listIds = mutableListOf<String>()


    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeView = inflater.inflate(R.layout.home_fragment, container, false)
        btn = homeView.findViewById(R.id.button)
        listView1 = homeView.findViewById(R.id.lv1)
        listView2 = homeView.findViewById(R.id.lv2)
        txt = homeView.findViewById(R.id.txt)
        btn2 = homeView.findViewById(R.id.button3)


        return homeView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel

        listView1.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            goToFragment(SongFragment(), listIds[position], parent?.getItemAtPosition(position).toString())

        }
    }

    override fun onStart(){
        super.onStart()
        getData("https://www.songsterr.com/a/ra/songs.json?pattern=Bad%Bunny", listView1)
        getData("https://www.songsterr.com/a/ra/songs.json?pattern=Daddy%Yankee", listView2)
    }


    fun getData(url: String, listView: ListView){
        val queue = Volley.newRequestQueue(this.context)
        val junto = mutableListOf<String>()
        var song: Cancion
        var artist: String
        var name: String
        var id: String
        var max: Int = 4


        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url,null, { response ->
                for (i in 0 until response.length()){
                    val obj = response.getJSONObject(i)
                    name = obj.getString("title")
                    artist = obj.getJSONObject("artist").getString("nameWithoutThePrefix")
                    id = obj.getString("id")
                    song = Cancion(id, name, artist)
                    listIds.add(id)
                    songs.add(song)
                }
                for (i in 0 until max){
                   val conc = songs[i].artist + " - " + songs[i].title
                    junto.add(conc)
                }
                listView.adapter = ArrayAdapter(homeView.context, android.R.layout.simple_list_item_1,junto)
            },
            { txt.text =" ERROR" })
        queue.add(jsonArrayRequest)
    }

    private fun goToFragment(fragment: Fragment, song_id: String, songName: String){
        parentFragmentManager.beginTransaction().apply {
            val clpFragment : Fragment = fragment
            val arguments = Bundle()
            arguments.putString("song_id", song_id)
            arguments.putString("songName", songName)
            clpFragment.arguments = arguments
            replace(R.id.navAppController,clpFragment)
            commit()
        }
    }

}