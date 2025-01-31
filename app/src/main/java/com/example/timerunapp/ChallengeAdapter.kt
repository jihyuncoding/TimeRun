package com.example.timerunapp

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.timerunapp.Challenge
import com.example.timerunapp.R

class ChallengeAdapter(
    private val context: Context,
    private val challenges: MutableList<Challenge>,
    private val onChallengeClick: (Challenge) -> Unit
) : BaseAdapter() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ChallengePrefs", Context.MODE_PRIVATE)

    override fun getCount(): Int = challenges.size

    override fun getItem(position: Int): Any = challenges[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false)
        val challenge = challenges[position]

        val nameTextView = view.findViewById<TextView>(R.id.challengeName)
        val goalTextView = view.findViewById<TextView>(R.id.challengeGoal)
        val dateTextView = view.findViewById<TextView>(R.id.challengeDate)
        val categoryTextView = view.findViewById<TextView>(R.id.categorySpinner)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBox)

        nameTextView.text = challenge.name
        goalTextView.text = challenge.goal
        dateTextView.text = challenge.dDay
        categoryTextView.text = challenge.category

        // SharedPreferences에서 체크 상태 불러오기
        val isChecked = getChallengeCheckedStatus(challenge.id)
        checkBox.isChecked = isChecked
        challenges[position].isChecked = isChecked  // 리스트에도 반영

        // 체크박스 상태 변경 리스너
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            challenges[position].isChecked = isChecked
            saveChallengeCheckedStatus(challenge.id, isChecked)

            // 체크된 비율 계산 및 저장
            saveCheckedPercentage()
            notifyDataSetChanged()
        }

        view.setOnClickListener {
            onChallengeClick(challenge)
        }

        return view
    }

    private fun getChallengeCheckedStatus(challengeId: Int): Boolean {
        return sharedPreferences.getBoolean("challenge_$challengeId", false)
    }

    private fun saveChallengeCheckedStatus(challengeId: Int, isChecked: Boolean) {
        sharedPreferences.edit().putBoolean("challenge_$challengeId", isChecked).apply()
    }

    // 체크된 항목의 비율을 계산하고 저장하는 함수
    fun saveCheckedPercentage() {
        val checkedCount = challenges.count { it.isChecked }
        val totalCount = challenges.size
        val percentage = if (totalCount > 0) (checkedCount * 100) / totalCount else 0

        sharedPreferences.edit().putInt("checked_percentage", percentage).apply()
    }

    // 다른 화면에서 값을 가져올 수 있도록 SharedPreferences에서 불러오는 함수
    fun getCheckedPercentage(): Int {
        return sharedPreferences.getInt("checked_percentage", 0)
    }
}
