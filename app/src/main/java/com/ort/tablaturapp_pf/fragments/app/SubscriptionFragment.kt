package com.ort.tablaturapp_pf.fragments.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import com.ort.tablaturapp_pf.R

class SubscriptionFragment : Fragment() {

    private lateinit var contentView: View
    private lateinit var tv_title: TextView
    private lateinit var tv_body: TextView
    private lateinit var subscribe_btn: Button
    private lateinit var wv_list: WebView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.subscription_fragment, container, false)
        tv_title = contentView.findViewById(R.id.tv_title)
        tv_body = contentView.findViewById(R.id.tv_body)
        subscribe_btn = contentView.findViewById(R.id.subscribe_btn)
        wv_list = contentView.findViewById(R.id.wv_list)
        return contentView
    }

    override fun onStart() {
        super.onStart()
        wv_list.settings.javaScriptEnabled
        wv_list.loadUrl("file:///android_asset/subscriptionList.html");
    }

}