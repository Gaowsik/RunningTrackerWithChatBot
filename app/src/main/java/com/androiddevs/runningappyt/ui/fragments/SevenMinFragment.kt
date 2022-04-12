package com.androiddevs.runningappyt.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.androiddevs.runningappyt.R
import kotlinx.android.synthetic.main.fragement_seven_min.*

class SevenMinFragment : Fragment(R.layout.fragement_seven_min)
{
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        li_start.setOnClickListener{

            findNavController().navigate(R.id.action_sevenMinFragment_to_exerciseFragment)
        }
    }

}