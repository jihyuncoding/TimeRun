package com.example.timerunapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ChallengeAdapter(
    private val context: Context,
    private val challenges: List<Challenge>,
    private val onChallengeClick: (Challenge) -> Unit // 클릭 이벤트 전달
) : BaseAdapter() {

    override fun getCount(): Int = challenges.size

    override fun getItem(position: Int): Any = challenges[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false)

        val challenge = challenges[position]

        val nameTextView = view.findViewById<TextView>(R.id.challengeName)
        val goalTextView = view.findViewById<TextView>(R.id.challengeGoal)
        val dateTextView = view.findViewById<TextView>(R.id.challengeDate) // 기간 텍스트 뷰
        val categoryTextView = view.findViewById<TextView>(R.id.categorySpinner) // 카테고리 텍스트 뷰

        // 데이터 바인딩
        nameTextView.text = challenge.name
        goalTextView.text = challenge.goal
        dateTextView.text = challenge.dDay // 기간 표시
        categoryTextView.text = challenge.category // 카테고리 표시

        // 각 항목에 클릭 이벤트 설정
        view.setOnClickListener {
            onChallengeClick(challenge) // 클릭된 챌린지 객체를 전달
        }

        return view
    }
}
