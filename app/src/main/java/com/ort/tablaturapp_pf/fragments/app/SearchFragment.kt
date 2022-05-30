package com.ort.tablaturapp_pf.fragments.app

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.SearchViewModel

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel
    private lateinit var contentView: View
    private lateinit var adapter: ArrayAdapter<*>
    private lateinit var activityContext: Context
    private lateinit var listView: ListView
    private lateinit var emptyTextView: TextView

    override fun onAttach( context: Context) {
        super.onAttach(context)
        activityContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        contentView = inflater.inflate(R.layout.search_fragment, container, false)
        listView = contentView.findViewById(R.id.lv_searchResultSongs)
        emptyTextView = contentView.findViewById(R.id.tv_emptyTextView)
        return contentView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        adapter = ArrayAdapter(activityContext, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.countries_array))
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            Toast.makeText(activityContext, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
        }
        listView.emptyView = emptyTextView
        // TODO: Use the ViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_search_menu, menu)
        val search = menu?.findItem(R.id.nav_search_menu)
        val searchView = search?.actionView as SearchView
        searchView.queryHint = "Buscar canción..."

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

}