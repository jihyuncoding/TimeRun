package com.example.timerunapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentMain : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var textView3: TextView
    private lateinit var imageView2: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        // 레이아웃에서 ProgressBar, TextView3, imageView2를 찾기
        progressBar = view.findViewById(R.id.progressBar)
        textView3 = view.findViewById(R.id.textView3)
        imageView2 = view.findViewById(R.id.imageView2)

        // SharedPreferences에서 저장된 체크된 퍼센트 가져오기
        val checkedPercentage = getCheckedPercentage()

        // ProgressBar와 TextView 업데이트
        progressBar.progress = checkedPercentage  // 퍼센트 값으로 프로그래스바 설정
        textView3.text = "$checkedPercentage" // 퍼센트 값 표시

        // progressValue를 checkedPercentage로 변경하여 이미지 업데이트
        when (checkedPercentage) {
            in 0..10 -> imageView2.setImageResource(R.drawable.main1)
            in 11..20 -> imageView2.setImageResource(R.drawable.main2)
            in 21..30 -> imageView2.setImageResource(R.drawable.main3)
            in 31..40 -> imageView2.setImageResource(R.drawable.main4)
            in 41..50 -> imageView2.setImageResource(R.drawable.main5)
            in 51..60 -> imageView2.setImageResource(R.drawable.main6)
            in 61..70 -> imageView2.setImageResource(R.drawable.main7)
            in 71..80 -> imageView2.setImageResource(R.drawable.main8)
            in 81..90 -> imageView2.setImageResource(R.drawable.main9)
            in 91..100 -> imageView2.setImageResource(R.drawable.main10)
            else -> imageView2.setImageResource(R.drawable.main1) // 기본 이미지
        }

        return view
    }

    // SharedPreferences에서 체크된 비율 가져오기
    private fun getCheckedPercentage(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("ChallengePrefs", android.content.Context.MODE_PRIVATE)
        return sharedPreferences.getInt("checked_percentage", 0)
    }
}

