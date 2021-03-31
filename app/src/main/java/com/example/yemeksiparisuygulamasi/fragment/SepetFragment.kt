package com.example.yemeksiparisuygulamasi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.yemeksiparisuygulamasi.R

class SepetFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val design = inflater.inflate(R.layout.activity_sepet, container,false)

        return design
    }
}