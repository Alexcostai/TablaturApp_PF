package com.ort.tablaturapp_pf.fragments.app

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

    lateinit var txt: TextView

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

        btn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(p0: View?) {
                getDataRecently()
                getData()
            }

        })
        return homeView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun getDataRecently(){
        val url = "https://www.songsterr.com/a/ra/songs.json?pattern=Bad%Bunny"
        val queue = Volley.newRequestQueue(this.context)
        val artists = mutableListOf<String>()
        val songs = mutableListOf<String>()
        val junto = mutableListOf<String>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url,null, { response ->
                for (i in 0 until response.length()){
                    val obj = response.getJSONObject(i)
                    songs.add(obj.getString("title"))
                    artists.add(obj.getJSONObject("artist").getString("nameWithoutThePrefix"))
                }
                for (i in 0 until songs.size){
                    val conc = artists.get(i) + " - " + songs.get(i)
                    junto.add(conc)
                }
                listView1.adapter = ArrayAdapter(homeView.context, android.R.layout.simple_list_item_1,junto)
            },
            { txt.text =" ERROR" })
        queue.add(jsonArrayRequest)
    }

    fun getData(){
        val url = "https://www.songsterr.com/a/ra/songs.json?pattern=Daddy%Yankee"
        val queue = Volley.newRequestQueue(this.context)
        val artists = mutableListOf<String>()
        val songs = mutableListOf<String>()
        val junto = mutableListOf<String>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url,null, { response ->
                for (i in 0 until 4){
                    val obj = response.getJSONObject(i)
                    songs.add(obj.getString("title"))
                    artists.add(obj.getJSONObject("artist").getString("nameWithoutThePrefix"))
                }
                for (i in 0 until songs.size){
                    val conc = artists.get(i) + " - " + songs.get(i)
                    junto.add(conc)
                }
                listView2.adapter = ArrayAdapter(homeView.context, android.R.layout.simple_list_item_1,junto)
            },
            { txt.text =" ERROR" })

        queue.add(jsonArrayRequest)
    }

}