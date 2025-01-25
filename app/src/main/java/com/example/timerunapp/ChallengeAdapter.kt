package com.example.timerunapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView

class ChallengeAdapter(
    private val context: Context,
    private val challenges: MutableList<Challenge>,  // MutableList로 변경
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
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox) // 체크박스 추가

        // 데이터 바인딩
        nameTextView.text = challenge.name
        goalTextView.text = challenge.goal
        dateTextView.text = challenge.dDay // 기간 표시
        categoryTextView.text = challenge.category // 카테고리 표시

        // 체크박스 상태 설정
        checkBox.setOnCheckedChangeListener(null) // 리스너 초기화 (중복 방지)
        checkBox.isChecked = challenge.isChecked // 체크박스 상태 설정

        // 체크박스 상태 변경 리스너
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            challenge.isChecked = isChecked // 체크박스 상태 업데이트

            if (isChecked) {
                // 체크된 항목을 아래로 이동
                moveItemDown(position)
            } else {
                // 체크 해제된 항목을 위로 이동
                moveItemUp(position)
            }

            // 어댑터 갱신하여 상태 반영
            notifyDataSetChanged()
        }

        // 각 항목에 클릭 이벤트 설정
        view.setOnClickListener {
            onChallengeClick(challenge) // 클릭된 챌린지 객체를 전달
        }

        return view
    }

    // 체크된 항목을 아래로 이동
    private fun moveItemDown(position: Int) {
        val item = challenges[position]
        challenges.removeAt(position)
        challenges.add(item) // 마지막에 항목을 추가하여 아래로 이동
    }

    // 체크 해제된 항목을 위로 이동
    private fun moveItemUp(position: Int) {
        val item = challenges[position]
        challenges.removeAt(position)

        // 체크되지 않은 항목들보다 위로 이동
        val insertPosition = challenges.indexOfFirst { it.isChecked }
        if (insertPosition == -1) {
            // 모든 항목이 체크 해제된 상태라면 맨 아래로 이동
            challenges.add(item)
        } else {
            // 체크된 항목들 바로 위에 삽입
            challenges.add(insertPosition, item)
        }
    }
}
