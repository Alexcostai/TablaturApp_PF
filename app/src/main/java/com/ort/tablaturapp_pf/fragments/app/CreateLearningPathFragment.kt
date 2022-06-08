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
  private val SPOTIFY_BEARER =
    "BQBfm5CNe2ZBfftUIlxA7L04uzDUdx6O3ofPzvgm-RfeRgZHLgmOLRyLC_LAKlfuXPI1VOsuopJoL3KwKs4ZPrxL9xNxnKwv8J-YExxFSGsaUUXAQ48C45i6XDJXc3bL7IqCq9D4d70J4R9Ja261yYdW75_MHwnscXA"
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
        val quantitySongsByGenre = getQuantitySongsByGenre(selectedGenres);
        val songsterrSongsIds = arrayListOf<String>();
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
              val songsterrSongIDJob = async { getSongsterrSongId(songs[i]) }
              val songsterrSongID = songsterrSongIDJob.await()
              if (songsterrSongID != "") {
                songsterrSongsIds.add(songsterrSongID);
                addedSongs += 1;
              }
              i += 1;
            }
          }
          if (args.isPremium) {
            setSongsInFirestore(songsterrSongsIds, "learningPathPremium")
          } else {
            setSongsInFirestore(songsterrSongsIds, "learningPath")
          }
        }
      }
    }
  }

  private fun setSongsInFirestore(songs: ArrayList<String>, fieldId: String) {
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
        }
        .addOnFailureListener {
          Toast.makeText(
            context, "Error al crear la ruta de aprendizaje.",
            Toast.LENGTH_SHORT
          ).show()
        }
    }
  }

  private fun getKeyFromGenre(genre: String): String {
    return genre.lowercase()
  }

  private suspend fun getSongsterrSongId(song: Bundle) =
    suspendCoroutine<String> { cont ->
      val queue = Volley.newRequestQueue(context)
      val artist = song.getString("artist")?.replace("\\s+".toRegex(), "%");
      val songName = song.getString("name")?.replace("\\s+".toRegex(), "%")
      val url =
        "https://www.songsterr.com/a/ra/songs.json?pattern=$artist%$songName"
      val stringRequest = object : StringRequest(Request.Method.GET, url,
        Response.Listener<String> { response ->
          var songId = ""
          if (JSONArray(response).length() >= 1) {
            val apiSong = JSONArray(response).getJSONObject(0)
            val artistName = apiSong.getJSONObject("artist").getString("name")
            if (artistName == song.getString("artist")) {
              songId = JSONArray(response).getJSONObject(0).getInt("id").toString()
            }
            cont.resume(songId);
          } else {
            cont.resume(songId);
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
      3 -> quantitySongsByGenre = intArrayOf(3, 3, 4)
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

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(CreateLearningPathViewModel::class.java)
    // TODO: Use the ViewModel
  }

}

