package com.ort.tablaturapp_pf.fragments.authentication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.RegisterViewModel

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    lateinit var registerView : View
    lateinit var registerButton: Button
    lateinit var password: TextView
    lateinit var confirmPassword: TextView
    lateinit var email: TextView

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        registerView = inflater.inflate(R.layout.register_fragment, container, false)
        registerButton = registerView.findViewById(R.id.button2)
        password = registerView.findViewById(R.id.passwordInput)
        confirmPassword = registerView.findViewById(R.id.passwordConfirmInput)
        email = registerView.findViewById(R.id.mailInput)
        return registerView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun validarEmail(): Boolean {
        var valido = false
        if (email.text.toString().contains("@") && email.text.toString().contains(".")){
            valido = true
        }
        return valido
    }

    private fun validadContrasenias(): Boolean{
        var valido = false
        if (password.text.toString().isNotEmpty() && confirmPassword.text.toString().isNotEmpty()){
            if (password == confirmPassword){
                valido = true
            }
        }
        return valido
    }



}