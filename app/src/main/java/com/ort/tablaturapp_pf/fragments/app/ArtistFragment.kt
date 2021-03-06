package com.ort.tablaturapp_pf.fragments.app

import android.content.Context
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
import com.ort.tablaturapp_pf.viewmodels.ArtistViewModel

class ArtistFragment : Fragment() {

    companion object {
        fun newInstance(artistId: Int, artistName: String): ArtistFragment {
            val fragment = ArtistFragment()
            val args = Bundle()
            args.putInt("artistId", artistId)
            args.putString("artistName", artistName)
            fragment.setArguments(args)
            return fragment
        }
    }

    private lateinit var viewModel: ArtistViewModel
    private lateinit var contentView: View
    private lateinit var artistTextView: TextView
    private lateinit var artistListView: ListView
    private lateinit var songIdList: List<String>
    private lateinit var args: ArtistFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contentView = inflater.inflate(R.layout.artist_fragment, container, false)
        artistListView = contentView.findViewById(R.id.lv_artistSongs)
        artistTextView = contentView.findViewById(R.id.tv_artistName)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        args = ArtistFragmentArgs.fromBundle(requireArguments())
        viewModel = ViewModelProvider(this).get(ArtistViewModel::class.java)
        artistTextView.text = args.artistName
        apiRequest(args.artistName)
        artistListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            goToFragment(SongFragment(), songIdList[position])
        }
        // TODO: Use the ViewModel
    }

    private fun apiRequest(searchValue: String){
        val url = "https://www.songsterr.com/a/ra/songs.json?pattern=" + searchValue
        val queue = Volley.newRequestQueue(contentView.context)
        val songs = mutableListOf<String>()
        val results = mutableListOf<String>()
        val songsId = mutableListOf<String>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url,null, { response ->
                if(response.length() >= 1){
                    for (i in 0 until getValidLength(response.length(), 30)){
                        val resultObject = response.getJSONObject(i)
                        if(resultObject.getJSONObject("artist").getInt("id") == args.artistId) {
                            songs.add(
                                resultObject.getString("title") + " - " + resultObject.getJSONObject(
                                    "artist"
                                ).getString("nameWithoutThePrefix")
                            )
                        }
                        songsId.add(resultObject.getString("id"))
                    }
                    songIdList = songsId
                    results.addAll(songs)
                    artistListView.adapter = ArrayAdapter(contentView.context, android.R.layout.simple_list_item_1, results)
                }else{
                    Toast.makeText(contentView.context, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(contentView.context, "Error...", Toast.LENGTH_SHORT).show()
            })

        queue.add(jsonArrayRequest)
    }

    private fun getValidLength (listLength: Int, desiredLength: Int): Int{
        if(listLength < desiredLength){
            return listLength
        }else{
            return desiredLength
        }
    }

    private fun goToFragment(fragment: Fragment, songId: String){
        parentFragmentManager.beginTransaction().apply {
            val clpFragment : Fragment = fragment
            val arguments = Bundle()
            arguments.putString("song_id", songId)
            clpFragment.arguments = arguments
            replace(R.id.navAppController,clpFragment)
            commit()
        }
    }

}