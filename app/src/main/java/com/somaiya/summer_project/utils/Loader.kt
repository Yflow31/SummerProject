package com.somaiya.summer_project.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.somaiya.summer_project.databinding.LoadingLayoutBinding

class Loader : DialogFragment() {

    companion object {
        private const val ARG_LOADING_TEXT = "LOADING_TEXT"

        @JvmStatic
        fun newInstance(loadingText: String): Loader {
            val fragment = Loader()
            val args = Bundle()
            args.putString(ARG_LOADING_TEXT, loadingText)
            fragment.arguments = args
            return fragment
        }
    }

    private var _binding: LoadingLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        _binding = LoadingLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        dialog.setContentView(binding.root)

        val loadingText = arguments?.getString(ARG_LOADING_TEXT) ?: "Loading, Please wait..."
        binding.loadingText.text = loadingText

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}