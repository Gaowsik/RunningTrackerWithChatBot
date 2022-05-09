package com.androiddevs.runningexpert.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androiddevs.runningexpert.R
import kotlinx.android.synthetic.main.fragment_finish.*

class FinishFragment : Fragment(R.layout.fragment_finish) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnClose.setOnClickListener{
            findNavController().navigate(R.id.action_finishFragment_to_sevenMinFragment)

        }
    }
}