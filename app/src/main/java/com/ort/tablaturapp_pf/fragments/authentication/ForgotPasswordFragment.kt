package com.ort.tablaturapp_pf.fragments.authentication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.ForgotPasswordViewModel

class ForgotPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }

    val INVALID_EMAIL = "El email es invalido."
    val IS_EMPTY_ERROR = "Este campo no puede estar vacio."

    private val auth = Firebase.auth
    private lateinit var contentView: View
    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var forgotButton: Button
    private lateinit var forgotEmail: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contentView = inflater.inflate(R.layout.forgot_password_fragment, container, false)
        forgotButton = contentView.findViewById(R.id.forgotPw_button)
        forgotEmail = contentView.findViewById(R.id.forgotPw_email)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        forgotButton.setOnClickListener {
            val mail = forgotEmail.text.toString()
            if(validateMail(mail)){
                sendEmail(mail)
            }
        }
    }

    private fun sendEmail(emailAddress: String){
        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(contentView.context, "¡Correo enviado con éxito!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(contentView.context, "Hubo un error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateMail(emailAddress: String): Boolean{
        var error = true;
        if (TextUtils.isEmpty(emailAddress)) {
            forgotEmail.error = IS_EMPTY_ERROR
            error = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            forgotEmail.error = INVALID_EMAIL
            error = false;
        }
        return error;
    }
}