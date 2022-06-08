package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.SongViewModel


class SongFragment : Fragment() {

    lateinit var homeView: View
    lateinit var txt_song: TextView
    lateinit var web: WebView
    lateinit var id: String


    companion object {
        fun newInstance(song_id: String, songName: String): SongFragment{
            val fragment = SongFragment()
            val args = Bundle()
            args.putString("song_id", song_id)
            args.putString("songName", songName)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: SongViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeView = inflater.inflate(R.layout.song_fragment, container, false)
        txt_song = homeView.findViewById(R.id.name_song)
        web = homeView.findViewById(R.id.webView)


        return homeView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SongViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart(){
        super.onStart()
        val args = SongFragmentArgs.fromBundle(requireArguments())
        txt_song.text = args.songName
        val id = args.songId
        val settings: WebSettings = web.settings
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        web.webViewClient = WebViewClient()
        web.loadUrl("http://www.songsterr.com/a/wa/song?id=$id")

    }

}