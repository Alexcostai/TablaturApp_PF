package com.ort.tablaturapp_pf.fragments.app

import Cancion
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.fragments.authentication.LoginFragmentDirections
import com.ort.tablaturapp_pf.viewmodels.HomeViewModel


class HomeFragment : Fragment() {

    lateinit var homeView: View
    lateinit var listView1: ListView
    lateinit var listView2: ListView
    lateinit var txt: TextView
    lateinit var subscriptionCard: CardView
    val songs = mutableListOf<Cancion>()
    var listIds = mutableListOf<String>()

    private val auth = Firebase.auth;

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeView = inflater.inflate(R.layout.home_fragment, container, false)
        listView1 = homeView.findViewById(R.id.lv1)
        listView2 = homeView.findViewById(R.id.lv2)
        subscriptionCard = homeView.findViewById(R.id.cv_subscription)

        setHasOptionsMenu(true);

        return homeView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel

        listView1.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            goToFragment(SongFragment(), listIds[position])

        }
    }

    override fun onStart(){
        super.onStart()
        getData("https://www.songsterr.com/a/ra/songs.json?pattern=Bad%Bunny", listView1)
        getData("https://www.songsterr.com/a/ra/songs.json?pattern=Daddy%Yankee", listView2)
        subscriptionCard.setOnClickListener{
            goToFragment(SubscriptionFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.log_out_menu -> {
                auth.signOut()
                val action = HomeFragmentDirections.actionHomeFragmentToMainActivity()
                homeView.findNavController().navigate(action)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun getData(url: String, listView: ListView){
        val queue = Volley.newRequestQueue(this.context)
        val junto = mutableListOf<String>()
        var song: Cancion
        var artist: String
        var name: String
        var id: String

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
                for (i in 0 until 3){
                   val conc = songs[i].artist + " - " + songs[i].title
                    junto.add(conc)
                }
                listView.adapter = ArrayAdapter(homeView.context, android.R.layout.simple_list_item_1,junto)
            },
            { txt.text =" ERROR" })
        queue.add(jsonArrayRequest)
    }

    private fun goToFragment(fragment: Fragment, song_id: String){
        parentFragmentManager.beginTransaction().apply {
            val clpFragment : Fragment = fragment
            val arguments = Bundle()
            arguments.putString("song_id", song_id)
            clpFragment.arguments = arguments
            replace(R.id.navAppController,clpFragment)
            commit()
        }
    }

    private fun goToFragment(fragment: Fragment){
        parentFragmentManager.beginTransaction().apply {
            val clpFragment : Fragment = fragment
            replace(R.id.navAppController,clpFragment)
            commit()
        }
    }

}