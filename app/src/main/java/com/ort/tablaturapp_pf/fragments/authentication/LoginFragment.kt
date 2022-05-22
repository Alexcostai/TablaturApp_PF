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
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.LoginViewModel
import org.w3c.dom.Text

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    val INVALID_EMAIL = "El email es invalido."
    val IS_EMPTY_ERROR = "Este campo no puede estar vacio."

    val auth = Firebase.auth
    lateinit var loginView: View
    lateinit var loginButton: Button
    lateinit var emailEdt: EditText
    lateinit var passwordEdt: EditText
    lateinit var registerButton: TextView

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginView = inflater.inflate(R.layout.login_fragment, container, false)
        loginButton = loginView.findViewById(R.id.loginButton)
        registerButton = loginView.findViewById(R.id.registerButton);
        emailEdt = loginView.findViewById(R.id.loginEmailEdt)
        passwordEdt = loginView.findViewById(R.id.loginPasswordEdt)
        return loginView
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            val action = LoginFragmentDirections.actionLoginFragmentToAppActivity()
            loginView.findNavController().navigate(action);
        }
        loginButton.setOnClickListener {
            if (!validateEmail() && !validatePassword()) {
                auth.signInWithEmailAndPassword(
                    emailEdt.text.toString(),
                    passwordEdt.text.toString()
                )
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            val action = LoginFragmentDirections.actionLoginFragmentToAppActivity()
                            loginView.findNavController().navigate(action)
                        } else {
                            Toast.makeText(
                                context, "Error al iniciar sesión.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        registerButton.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            loginView.findNavController().navigate(action)
        }
    }

    private fun validateEmail(): Boolean {
        var error = false;
        if (TextUtils.isEmpty(emailEdt.text.toString())) {
            emailEdt.error = IS_EMPTY_ERROR
            error = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailEdt.text.toString()).matches()) {
            emailEdt.error = INVALID_EMAIL
            error = true;
        }
        return error;
    }

    private fun validatePassword(): Boolean {
        var error = false;
        if (TextUtils.isEmpty(passwordEdt.text.toString())) {
            passwordEdt.error = IS_EMPTY_ERROR
            error = true;
        }
        return error;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}