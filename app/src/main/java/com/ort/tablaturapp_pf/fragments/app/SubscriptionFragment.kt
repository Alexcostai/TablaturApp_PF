package com.ort.tablaturapp_pf.fragments.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import org.json.JSONObject


class SubscriptionFragment : Fragment() {

  private lateinit var contentView: View
  private lateinit var tv_title: TextView
  private lateinit var tv_body: TextView
  private lateinit var subscribe_btn: Button

  private val auth = Firebase.auth

  private val MERCADO_PAGO_BEARER =
    "TEST-8907559517508214-061319-ca495a6e51e26e414c9058e9fe7a9823-1142307329";

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    contentView = inflater.inflate(R.layout.subscription_fragment, container, false)
    tv_title = contentView.findViewById(R.id.tv_title)
    tv_body = contentView.findViewById(R.id.tv_body)
    subscribe_btn = contentView.findViewById(R.id.subscribe_btn)
    return contentView
  }

  override fun onStart() {
    super.onStart()

    subscribe_btn.setOnClickListener {
      val jsonBody = JSONObject()
      val autoRecurring = JSONObject()
      autoRecurring.put("frequency", 1)
      autoRecurring.put("frequency_type", "months")
      autoRecurring.put("transaction_amount", 350)
      autoRecurring.put("currency_id", "ARS")
      jsonBody.put("reason", "Premium - TablaturApp")
      jsonBody.put("auto_recurring", autoRecurring)
      jsonBody.put("back_url", "https://www.mercadopago.com/success")
      jsonBody.put("payer_email", "test_user_78947070@testuser.com")
      Log.d("JSON_BODY", jsonBody.toString())

      val queue = Volley.newRequestQueue(context)
      val url = "https://api.mercadopago.com/preapproval"
      val jsonRequest = object : JsonObjectRequest(
        Request.Method.POST, url, jsonBody,
        Response.Listener<JSONObject> { response ->
          val mpLink = response.getString("init_point");
          val openURL = Intent(android.content.Intent.ACTION_VIEW)
          openURL.data = Uri.parse(mpLink)
          startActivity(openURL)
        },
        Response.ErrorListener { error: VolleyError ->
          Log.e(
            "MPError",
            error.message.toString()
          )
        }) {
        override fun getHeaders(): MutableMap<String, String> {
          val headers = HashMap<String, String>()
          headers["Content-Type"] = "application/json"
          headers["Authorization"] = "Bearer $MERCADO_PAGO_BEARER"
          return headers
        }
      }
      queue.add(jsonRequest)
    }
  }

}