package com.ort.tablaturapp_pf.fragments.app

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
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
    private lateinit var listView: ListView
    private lateinit var searchTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        contentView = inflater.inflate(R.layout.search_fragment, container, false)
        listView = contentView.findViewById(R.id.lv_searchResultSongs)
        searchTextView = contentView.findViewById(R.id.tv_searchTextView)
        return contentView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(contentView.context, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
        }
        listView.emptyView = searchTextView
        // TODO: Use the ViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_search_menu, menu)
        val search = menu?.findItem(R.id.nav_search_menu)
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
                    listView.adapter = ArrayAdapter(contentView.context, android.R.layout.simple_list_item_1, mutableListOf<String>())
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

    fun apiSearchRequest(searchValue: String){
        val url = "https://www.songsterr.com/a/ra/songs.json?pattern=" + formatSearchValue(searchValue)
        val queue = Volley.newRequestQueue(contentView.context)
        val songs = mutableListOf<String>()
        val artists = mutableListOf<String>()
        val results = mutableListOf<String>()

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url,null, { response ->
                if(response.length() >= 1){
                     for (i in 0 until getValidLength(response.length(), 15)){
                         val resultObject = response.getJSONObject(i)
                         songs.add(resultObject.getString("title") +" - " + resultObject.getJSONObject("artist").getString("nameWithoutThePrefix"))
                         artists.add(resultObject.getJSONObject("artist").getString("nameWithoutThePrefix"))
                    }
                    results.addAll(artists.distinct().subList(0,getValidLength(artists.distinct().size, 3)))
                    results.addAll(songs)
                    listView.adapter = ArrayAdapter(contentView.context, android.R.layout.simple_list_item_1, results)
                }else{
                    Toast.makeText(contentView.context, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(contentView.context, "Error...", Toast.LENGTH_SHORT).show()
            })

        queue.add(jsonArrayRequest)
    }

    fun formatSearchValue(searchValue: String): String{
        val formattedText: String
        formattedText = searchValue.replace(" ", "%")
        return formattedText
    }

    fun getValidLength (listLength: Int, desiredLength: Int): Int{
        if(listLength < desiredLength){
            return listLength
        }else{
            return desiredLength
        }
    }

}