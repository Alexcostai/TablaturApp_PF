package com.ort.tablaturapp_pf.fragments.authentication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.LoginViewModel
import org.w3c.dom.Text

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    lateinit var loginView: View
    lateinit var loginButton: Button
    lateinit var registerButton: TextView

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginView = inflater.inflate(R.layout.login_fragment, container, false)
        loginButton = loginView.findViewById(R.id.loginButton)
        registerButton = loginView.findViewById(R.id.registerButton);
        return loginView
    }

    override fun onStart() {
        super.onStart()
        loginButton.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToAppActivity()
            loginView.findNavController().navigate(action)
        }

        registerButton.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            loginView.findNavController().navigate(action)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}