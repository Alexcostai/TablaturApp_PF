package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.CreateLearningPathViewModel
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class CreateLearningPathFragment : Fragment() {

  lateinit var createLearningPathView: View
  lateinit var createLearningPathBtn: Button
  lateinit var pbCreateLp: ProgressBar
  private val SPOTIFY_BEARER =
    "BQDrUZXsqhFgdNcnrVSN-ZMRceWjLCGPMbYhFiImfwBzV2y3L66YD4tG00mWdOJjGWfgZKcKX9jBFbz48omvaha0nix83C8wHN4ryaZEyzNcR1x2cMTzhz2zumtQdzA8xfuIy0Kqa_LyX8iaRfpEyL78J8bV65LiuIagRilQbU8vdyWtU29n3M5d1OGL2YF_alub"
  private val db = Firebase.firestore
  private val auth = Firebase.auth

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
    pbCreateLp = createLearningPathView.findViewById(R.id.pbCreateLp);
    return createLearningPathView;
  }

  override fun onStart() {
    super.onStart()
    val args = CreateLearningPathFragmentArgs.fromBundle(requireArguments())
    val selectedGenres = mutableListOf<String>()
    val checkBoxGenres = getCheckBoxGenres()
    checkBoxGenres.forEach {
      it.setOnCheckedChangeListener { buttonView, isChecked ->
        val key = getKeyFromGenre(it.text.toString())
        if (isChecked) {
          if (selectedGenres.size < 3) {
            selectedGenres.add(key)
          } else {
            Toast.makeText(
              requireContext(),
              "Máximo 3 géneros!",
              Toast.LENGTH_SHORT
            ).show()
            it.isChecked = false;
          }
        } else {
          selectedGenres.remove(key)
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
        handleLoadingBtn();
        val quantitySongsByGenre = getQuantitySongsByGenre(selectedGenres);
        val songsterrSongs = arrayListOf<HashMap<String, Any?>>();
        GlobalScope.launch(Dispatchers.Main) {
          for (idx in 0 until selectedGenres.size) {
            var i = 0;
            var addedSongs = 0;
            val genre = selectedGenres[idx];
            val songsByGenre = quantitySongsByGenre[idx];
            val playlistId =
              async { getPlaylistIdByGenre(genre.lowercase()) }
            val songsJob = async { getSongsByPlaylist(playlistId.await()) }
            val songs = songsJob.await();
            while (addedSongs !== songsByGenre && i < songs.size) {
              val songsterrSongJob = async { getSongsterrSong(songs[i]) }
              val songsterrSong = songsterrSongJob.await();
              if (songsterrSong.size() != 0) {
                val hashMapSong = hashMapOf(
                  "id" to songsterrSong["id"],
                  "name" to songsterrSong["name"],
                  "artist" to songsterrSong["artist"]
                )
                songsterrSongs.add(hashMapSong);
                addedSongs += 1;
              }
              i += 1;
            }
          }
          if (args.isPremium) {
            setSongsInFirestore(songsterrSongs, "learningPathPremium")
          } else {
            setSongsInFirestore(songsterrSongs, "learningPath")
          }
        }
      }
    }
  }

  private fun setSongsInFirestore(songs: ArrayList<HashMap<String, Any?>>, fieldId: String) {
    auth.currentUser?.let { it1 ->
      db.collection("users").document(it1.uid)
        .update(
          fieldId, songs
        )
        .addOnSuccessListener {
          Toast.makeText(
            context, "Ruta generada con exito.",
            Toast.LENGTH_SHORT
          ).show()
          goToFragment(LearningList(), songs)
        }
        .addOnFailureListener {
          Toast.makeText(
            context, "Error al crear la ruta de aprendizaje.",
            Toast.LENGTH_SHORT
          ).show()
          handleLoadingBtn();
        }
    }
  }

  private fun handleLoadingBtn() {
    createLearningPathBtn.isVisible = false;
    pbCreateLp.isVisible = true;
  }

  private fun getKeyFromGenre(genre: String): String {
    return genre.lowercase()
  }

  private suspend fun getSongsterrSong(song: Bundle) =
    suspendCoroutine<Bundle> { cont ->
      val queue = Volley.newRequestQueue(context)
      val artist = song.getString("artist")?.replace("\\s+".toRegex(), "%");
      val songName = song.getString("name")?.replace("\\s+".toRegex(), "%")
      val url =
        "https://www.songsterr.com/a/ra/songs.json?pattern=$artist%$songName"
      val stringRequest = object : StringRequest(Request.Method.GET, url,
        Response.Listener<String> { response ->
          if (JSONArray(response).length() >= 1) {
            val apiSong = JSONArray(response).getJSONObject(0)
            val artistName = apiSong.getJSONObject("artist").getString("name")
            if (artistName == song.getString("artist")) {
              val songId = JSONArray(response).getJSONObject(0).getInt("id").toString()
              song.putString("id", songId);
              cont.resume(song);
            }
          } else {
            cont.resume(Bundle());
          }
        },
        Response.ErrorListener { error -> Log.e("SongsterrError", error.toString()) }) {}
      queue.add(stringRequest)
    }

  private suspend fun getPlaylistIdByGenre(genre: String) =
    suspendCoroutine<String> { cont ->
      val queue = Volley.newRequestQueue(context)
      val url =
        "https://api.spotify.com/v1/browse/categories/$genre/playlists?country=US&limit=1&offset=4"

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
          headers["Authorization"] = "Bearer $SPOTIFY_BEARER"
          return headers
        }
      }
      queue.add(stringRequest)
    }

  private suspend fun getSongsByPlaylist(playlistId: String) =
    suspendCoroutine<ArrayList<Bundle>> { cont ->
      val queue = Volley.newRequestQueue(context)
      val url =
        "https://api.spotify.com/v1/playlists/$playlistId/tracks?limit=30"
      val stringRequest = object : StringRequest(Request.Method.GET, url,
        Response.Listener<String> { response ->
          val jsonSongs = JSONObject(response).getJSONArray("items")
          val songs = arrayListOf<Bundle>()
          for (idx in 0 until jsonSongs.length()) {
            val bundle = Bundle()
            try {
              val jsonTrack = jsonSongs.getJSONObject(idx).getJSONObject("track");
              val artist =
                jsonTrack.getJSONArray("artists").getJSONObject(0).getString("name");
              val name = jsonTrack.getString("name");
              bundle.putString("artist", artist);
              bundle.putString("name", name);
              songs.add(bundle);
            } catch (e: Exception) {
              Log.w("TrackError", "Track is null")
            }
          }
          cont.resume(songs);
        },
        Response.ErrorListener { error -> Log.e("SpotifyError", error.toString()) }) {
        override fun getHeaders(): MutableMap<String, String> {
          val headers = HashMap<String, String>()
          headers["Content-Type"] = "application/json"
          headers["Authorization"] = "Bearer $SPOTIFY_BEARER"
          return headers
        }
      }
      queue.add(stringRequest)
    }

  private fun getQuantitySongsByGenre(selectedGenres: MutableList<String>): IntArray {
    var quantitySongsByGenre = intArrayOf(10);
    when (selectedGenres.size) {
      2 -> quantitySongsByGenre = intArrayOf(5, 5)
      3 -> quantitySongsByGenre = intArrayOf(4, 3, 3)
    }
    return quantitySongsByGenre
  }

  private fun getCheckBoxGenres(): ArrayList<CheckBox> {
    val checkBoxGenres = arrayListOf<CheckBox>()
    val v = createLearningPathView;
    checkBoxGenres.add(v.findViewById(R.id.popCbx))
    checkBoxGenres.add(v.findViewById(R.id.rockCbx))
    checkBoxGenres.add(v.findViewById(R.id.jazzCbx))
    checkBoxGenres.add(v.findViewById(R.id.bluesCbx))
    checkBoxGenres.add(v.findViewById(R.id.punkCbx))
    return checkBoxGenres;
  }

  private fun goToFragment(fragment: Fragment, songs : ArrayList<HashMap<String, Any?>>) {
    parentFragmentManager.beginTransaction().apply {
      val clpFragment: Fragment = fragment
      clpFragment.arguments = songsToArguments(songs);
      replace(R.id.navAppController, clpFragment)
      commit()
    }
  }

  private fun songsToArguments(songs: ArrayList<HashMap<String, Any?>>): Bundle {
    val songsTitles = Array<String>(songs.size) { "" }
    val ids = Array<String>(songs.size) { "" }
    val arguments = Bundle()
    for (idx in 0 until songs.size) {
      val song = songs[idx];
      songsTitles[idx] = ("${song["name"]} - ${song["artist"]}");
      song["id"]?.let { it1 -> ids[idx] = (it1 as String) }
    }
    arguments.putStringArray("songs", songsTitles);
    arguments.putStringArray("ids", ids);
    return arguments
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(CreateLearningPathViewModel::class.java)
    // TODO: Use the ViewModel
  }

}

