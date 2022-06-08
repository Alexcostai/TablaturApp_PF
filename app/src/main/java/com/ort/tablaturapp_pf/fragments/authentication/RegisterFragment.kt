package com.ort.tablaturapp_pf.fragments.authentication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ort.tablaturapp_pf.R
import com.ort.tablaturapp_pf.viewmodels.RegisterViewModel

class RegisterFragment : Fragment() {

  companion object {
    fun newInstance() = RegisterFragment()
  }

  val INVALID_EMAIL = "El email es invalido."
  val IS_EMPTY_ERROR = "Este campo no puede estar vacio."
  val PASSWORDS_BE_SAME = "Las contraseñas tienen que ser iguales."

  private val auth = Firebase.auth
  private val db = Firebase.firestore
  lateinit var registerView: View
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
    registerButton = registerView.findViewById(R.id.registerBtn)
    password = registerView.findViewById(R.id.passwordInput)
    confirmPassword = registerView.findViewById(R.id.passwordConfirmInput)
    email = registerView.findViewById(R.id.mailInput)
    return registerView
  }

  override fun onStart() {
    super.onStart()
    registerButton.setOnClickListener {
      val isValidEmail = !validateEmail()
      val isValidPasswords = !validatePasswords()
      if (isValidEmail && isValidPasswords) {
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
          .addOnCompleteListener() { task ->
            if (task.isSuccessful) {
              task.result.user?.let { it1 ->
                val newUser = hashMapOf(
                  "isPremium" to false,
                  "learningPath" to null,
                  "learningPathPremium" to null
                )
                db.collection("users").document(it1.uid).set(newUser).addOnSuccessListener {
                  val action =
                    RegisterFragmentDirections.actionRegisterFragmentToAppActivity()
                  registerView.findNavController().navigate(action)
                }.addOnFailureListener { error -> onRegisterFailure(); Log.e("FirestoreError", error.toString()) }
              }
            } else {
              Log.e("AuthError", task.exception.toString())
              onRegisterFailure()
            }
          }
      }
    }
  }

  private fun onRegisterFailure() {
    Toast.makeText(
      context, "Error al registrarse.",
      Toast.LENGTH_SHORT
    ).show()
  }

  private fun validateEmail(): Boolean {
    var error = false;
    if (TextUtils.isEmpty(email.text.toString())) {
      email.error = IS_EMPTY_ERROR
      error = true;
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
      email.error = INVALID_EMAIL
      error = true;
    }
    return error;
  }

  private fun validatePasswords(): Boolean {
    var error = false;
    if (TextUtils.isEmpty(password.text.toString())) {
      password.error = IS_EMPTY_ERROR
      error = true;
    }
    if (TextUtils.isEmpty(confirmPassword.text.toString())) {
      confirmPassword.error = IS_EMPTY_ERROR
      error = true;
    } else if (confirmPassword.text.toString() == password.text.toString()) {
      error = false;
    } else {
      confirmPassword.error = PASSWORDS_BE_SAME
      error = true;
    }
    return error;
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
    // TODO: Use the ViewModel
  }


}