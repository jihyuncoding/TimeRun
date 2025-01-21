package com.example.timerunapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timerunapp.R

class FragmentMain : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_main.xml 레이아웃을 연결
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
}
