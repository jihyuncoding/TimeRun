package com.example.timerunapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ChallengeAdapter(private val context: Context, private val challenges: List<Challenge>) : BaseAdapter() {

    override fun getCount(): Int = challenges.size // 항목 수 반환

    override fun getItem(position: Int): Any = challenges[position] // 특정 위치의 항목 반환

    override fun getItemId(position: Int): Long = position.toLong() // 항목 ID 반환

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false)

        // 현재 위치의 Challenge 객체 가져오기
        val challenge = challenges[position]

        // 레이아웃의 뷰에 데이터 설정
        val nameTextView = view.findViewById<TextView>(R.id.challengeName)
        val goalTextView = view.findViewById<TextView>(R.id.challengeGoal)
        nameTextView.text = challenge.name
        goalTextView.text = challenge.goal

        return view
    }
}
