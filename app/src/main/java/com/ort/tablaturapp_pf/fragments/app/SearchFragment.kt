package com.ort.tablaturapp_pf.fragments.app

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.SearchViewModel

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var contentView: View
    private lateinit var searchListView: ListView
    private lateinit var searchTextView: TextView
    private lateinit var artistIdList: List<Int>
    private lateinit var songIdList: List<Int>
    private var artistsLength: Int = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        contentView = inflater.inflate(R.layout.search_fragment, container, false)
        searchListView = contentView.findViewById(R.id.lv_searchResultSongs)
        searchTextView = contentView.findViewById(R.id.tv_searchTextView)
        return contentView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        searchListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (position < artistsLength){
                goToFragment(ArtistFragment(), artistIdList[position], parent?.getItemAtPosition(position).toString())
            }else{
                goToFragment(SongFragment(), songIdList[position])
            }
        }
        searchListView.emptyView = searchTextView
        // TODO: Use the ViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_search_menu, menu)
        val search = menu.findItem(R.id.nav_search_menu)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Busca tu canción..."

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(!query.isNullOrEmpty() && query.length > 1) {
                    apiSearchRequest(query)
                }else{
                    Toast.makeText(contentView.context, "Busqueda invalida", Toast.LENGTH_SHORT).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty() || newText.length <= 1) {
                    searchListView.adapter = ArrayAdapter(contentView.context, android.R.layout.simple_list_item_1, mutableListOf<String>())
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private fun apiSearchRequest(searchValue: String){
        val url = "https://www.songsterr.com/a/ra/songs.json?pattern=" + formatSearchValue(searchValue)
        val queue = Volley.newRequestQueue(contentView.context)
        val songs = mutableListOf<String>()
        val artists = mutableListOf<String>()
        val results = mutableListOf<String>()
        val artistsId = mutableListOf<Int>()
        val songsId = mutableListOf<Int>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url,null, { response ->
                if(response.length() >= 1){
                     for (i in 0 until getValidLength(response.length(), 15)){
                         val resultObject = response.getJSONObject(i)
                         songs.add(resultObject.getString("title") +" - " + resultObject.getJSONObject("artist").getString("nameWithoutThePrefix"))
                         artists.add(resultObject.getJSONObject("artist").getString("nameWithoutThePrefix"))
                         artistsId.add(resultObject.getJSONObject("artist").getInt("id"))
                         songsId.add(resultObject.getInt("id"))
                    }
                    songIdList = songsId
                    artistIdList = artistsId.distinct().subList(0,getValidLength(artistsId.distinct().size, 3))
                    artistsLength = artistIdList.size
                    results.addAll(artists.distinct().subList(0,getValidLength(artists.distinct().size, 3)))
                    results.addAll(songs)
                    searchListView.adapter = ArrayAdapter(contentView.context, android.R.layout.simple_list_item_1, results)
                }else{
                    Toast.makeText(contentView.context, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(contentView.context, "Error...", Toast.LENGTH_SHORT).show()
            })

        queue.add(jsonArrayRequest)
    }

    private fun formatSearchValue(searchValue: String): String{
        val formattedText: String
        formattedText = searchValue.replace(" ", "%")
        return formattedText
    }

    private fun getValidLength (listLength: Int, desiredLength: Int): Int{
        if(listLength < desiredLength){
            return listLength
        }else{
            return desiredLength
        }
    }

    private fun goToFragment(fragment: Fragment, id: Int, name: String){
        parentFragmentManager.beginTransaction().apply {
            val clpFragment : Fragment = fragment
            val arguments = Bundle()
            arguments.putInt("artistId", id)
            arguments.putString("artistName", name)
            clpFragment.arguments = arguments
            replace(R.id.navAppController,clpFragment)
            commit()
        }
    }

    private fun goToFragment(fragment: Fragment, id: Int){
        parentFragmentManager.beginTransaction().apply {
            val clpFragment : Fragment = fragment
            val arguments = Bundle()
            arguments.putInt("song_id", id)
            clpFragment.arguments = arguments
            replace(R.id.navAppController,clpFragment)
            commit()
        }
    }


}