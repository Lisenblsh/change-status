package com.example.changesatus.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment.STYLE_NO_TITLE
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.changesatus.R
import com.example.changesatus.databinding.FragmentChangeStatusBinding
import com.example.changesatus.tools.ChangeStatusSender
import com.example.changesatus.tools.VkStatus
import kotlinx.coroutines.launch


class ChangeStatusFragment : Fragment() {

    private lateinit var binding: FragmentChangeStatusBinding

    private var pref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeStatusBinding.inflate(inflater, container, false)
        binding.bindElements()
        initSharedPreference()
        showFragment()

        return binding.root
    }

    val token = ""

    private fun initSharedPreference() {
        pref = activity?.getSharedPreferences("appSettings", Context.MODE_PRIVATE)
        binding.tokenText.text = pref?.getString("TOKEN", "токена нет переустанови приложение")


    }

    private fun showFragment() {
        if (checkToken()) {
            val fragmentManager = activity?.supportFragmentManager
            val authorizationFragment = AuthorizationFragment()
            if (fragmentManager != null) {
                authorizationFragment.setStyle(
                    STYLE_NO_TITLE,
                    androidx.constraintlayout.widget.R.style.AlertDialog_AppCompat
                )
                authorizationFragment.show(fragmentManager, "")
            }
        }
    }

    private fun checkToken(): Boolean {
        val token = pref?.getString("TOKEN", null)
        return token.isNullOrEmpty()
    }

    private fun FragmentChangeStatusBinding.bindElements() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.privacy_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            binding.spinner.adapter = adapter
        }

        setStatus.setOnClickListener {
            val id = binding.spinner.selectedItemPosition
            setStatus(id)
        }
        onlyMe.setOnClickListener {
            setStatus(3)
            binding.spinner.setSelection(3)
        }
        all.setOnClickListener {
            setStatus(0)
            binding.spinner.setSelection(0)

        }
    }

    private fun setStatus(id: Int) {
        val pref = activity?.getSharedPreferences("appSettings", Context.MODE_PRIVATE)

        val privacyType = VkStatus.values()[id]

        val token = pref?.getString("TOKEN", null)
        if (!token.isNullOrEmpty()) {
            lifecycleScope.launch {
                ChangeStatusSender(token = token).setStatus(privacyType)
            }
        }
    }
}
