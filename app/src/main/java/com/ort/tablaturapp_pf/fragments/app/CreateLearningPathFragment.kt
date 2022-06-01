package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.CreateLearningPathViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class CreateLearningPathFragment : Fragment() {

    lateinit var createLearningPathView: View
    lateinit var createLearningPathBtn: Button

    companion object {
        fun newInstance() = CreateLearningPathFragment()
    }

    private lateinit var viewModel: CreateLearningPathViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        createLearningPathView =
            inflater.inflate(R.layout.create_learning_path_fragment, container, false)
        createLearningPathBtn = createLearningPathView.findViewById(R.id.createLearningPathBtn)
        return createLearningPathView;
    }

    override fun onStart() {
        super.onStart()
        val selectedGenres = mutableListOf<String>()
        val checkBoxGenres = getCheckBoxGenres()
        checkBoxGenres.forEach {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (selectedGenres.size < 3) {
                        selectedGenres.add(it.text.toString().lowercase())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Máximo 3 géneros!",
                            Toast.LENGTH_SHORT
                        ).show()
                        it.isChecked = false;
                    }
                } else {
                    selectedGenres.remove(it.text.toString().lowercase())
                }
            }
        }

        createLearningPathBtn.setOnClickListener {
            if (selectedGenres.size < 1) {
                Toast.makeText(
                    requireContext(),
                    "Seleccioná al menos 1 género",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                var playlistIds = arrayListOf<String>()
                val quantitySongsByGenre = getQuantitySongsByGenre(selectedGenres);
                GlobalScope.launch(Dispatchers.Main) {
                    playlistIds.add(getPlaylistIdByGenre(selectedGenres[0].lowercase()))
                    Log.d("TestPlaylistIds", playlistIds.toString())
                }
            }
        }
    }


    suspend fun getPlaylistIdByGenre(genre: String) = suspendCoroutine<String> { cont ->
        val queue = Volley.newRequestQueue(context)
        val url =
            "https://api.spotify.com/v1/browse/categories/$genre/playlists?country=AR&limit=1"
        val stringRequest = object : StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                val playlistId =
                    JSONObject(response).getJSONObject("playlists").getJSONArray("items")
                        .getJSONObject(0).getString("id")
                cont.resume(playlistId)
            },
            Response.ErrorListener { error -> Log.e("SpotifyError", error.toString()) }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] =
                    "Bearer BQA6hEDG2f45CRDFH0vgjDEnDKRvhg78VwC2sx-8nBkEevqjtzdiBakGfBB_5P2UqiDhgCgJ7Dg6PmZA2ZGTRROEjoKFiQ7jKZANgSsRTiUUdV5ng_OA8faxHXoj8qIcbCBrLbFq8_MkGYFhR93pgvnqAE1BEgH-TE4"
                return headers
            }
        }
        queue.add(stringRequest)
    }


    private fun getQuantitySongsByGenre(selectedGenres: MutableList<String>): IntArray {
        var quantitySongsByGenre = intArrayOf(10);
        when (selectedGenres.size) {
            2 -> quantitySongsByGenre = intArrayOf(5, 5)
            3 -> quantitySongsByGenre = intArrayOf(3, 3, 4)
        }
        return quantitySongsByGenre
    }


    private fun getCheckBoxGenres(): MutableList<CheckBox> {
        val checkBoxGenres = mutableListOf<CheckBox>()
        val v = createLearningPathView;
        checkBoxGenres.add(v.findViewById(R.id.bluesCbx))
        checkBoxGenres.add(v.findViewById(R.id.clasicaCbx))
        checkBoxGenres.add(v.findViewById(R.id.cumbiaCbx))
        checkBoxGenres.add(v.findViewById(R.id.funkCbx))
        checkBoxGenres.add(v.findViewById(R.id.jazzCbx))
        checkBoxGenres.add(v.findViewById(R.id.metalCbx))
        checkBoxGenres.add(v.findViewById(R.id.popCbx))
        checkBoxGenres.add(v.findViewById(R.id.punkCbx))
        checkBoxGenres.add(v.findViewById(R.id.rapCbx))
        checkBoxGenres.add(v.findViewById(R.id.reggaetonCbx))
        checkBoxGenres.add(v.findViewById(R.id.rockCbx))
        checkBoxGenres.add(v.findViewById(R.id.reggaeCbx))
        return checkBoxGenres;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateLearningPathViewModel::class.java)
        // TODO: Use the ViewModel
    }

}