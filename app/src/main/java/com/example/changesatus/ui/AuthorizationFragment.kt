package com.example.changesatus.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope

import com.example.changesatus.databinding.FragmentAuthorizationBinding
import com.example.changesatus.tools.TokenException
import com.example.changesatus.tools.TokenExceptionType
import com.example.changesatus.tools.TokenReceiver
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi


class AuthorizationFragment : DialogFragment() {

    private lateinit var binding: FragmentAuthorizationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizationBinding.inflate(inflater, container, false)
        binding.bindElement()
        return binding.root
    }

    private var captchaSid: String? = null
    @OptIn(ExperimentalSerializationApi::class)
    private fun FragmentAuthorizationBinding.bindElement() {
        getTokenButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val code = codeEditText.text.toString().ifEmpty { null }
            val captchaKey = captchaEditText.text.toString().ifEmpty { null }
            lifecycleScope.launch {
                val tokenReceiver = TokenReceiver()
                try {
                    val token = tokenReceiver.getToken(username, password,code, captchaSid, captchaKey)
                    if (token != null) {
                        writeToSharedPreference(token)
                        onDestroyView()
                    }
                } catch (e: TokenException) {
                    when (e.code) {
                        TokenExceptionType.TWO_FA_REQ -> {
                            errorMessage.text = "Было отправленно смс с кодом для подтверждения"
                            errorMessage.visibility = View.VISIBLE
                            codeEditText.visibility = View.VISIBLE
                        }
                        TokenExceptionType.REGISTRATION_ERROR -> {
                            errorMessage.text = "Неправильный логин или пароль"
                            errorMessage.visibility = View.VISIBLE
                        }
                        TokenExceptionType.NEED_CAPTCHA -> {
                            captchaSid = e.captchaSid
                            errorMessage.text  = "Нужна капча"
                            errorMessage.visibility = View.VISIBLE
                            Picasso.get().load(e.captchaImg).into(captchaImage)
                            captchaLayout.visibility = View.VISIBLE
                        }
                        else -> {
                            errorMessage.text = e.message
                            errorMessage.visibility = View.VISIBLE
                        }
                    }
                }
                catch (e: Exception) {
                    Log.e("catch","${e.message}")
                }
            }
        }
    }

    private fun writeToSharedPreference(token: String) {
        val pref = activity?.getSharedPreferences("appSettings", Context.MODE_PRIVATE) ?: return
        with(pref.edit()) {
            putString("TOKEN", token)
            apply()
        }
    }
}
