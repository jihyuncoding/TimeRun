package com.example.timerunapp

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import org.json.JSONArray

class ChallengeAdapter(
    private val context: Context,
    private val challenges: MutableList<Challenge>,
    private val onChallengeClick: (Challenge, View) -> Unit
) : BaseAdapter() {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ChallengePrefs", Context.MODE_PRIVATE)

    init {
        loadOrderFromPreferences() // 저장된 순서 로드
    }

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

        // 기존 리스너 제거 (중복 호출 방지)
        checkBox.setOnCheckedChangeListener(null)

        // SharedPreferences에서 체크 상태 불러오기
        val isChecked = getChallengeCheckedStatus(challenge.id)
        challenge.isChecked = isChecked
        checkBox.isChecked = isChecked // UI에 반영

        // 체크박스 상태 변경 리스너 설정
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            challenge.isChecked = isChecked
            saveChallengeCheckedStatus(challenge.id, isChecked)

            if (isChecked) {
                moveItemToCheckedGroup(position)
            } else {
                moveItemToUncheckedGroup(position)
            }

            saveOrderToPreferences() // 순서 저장
            saveCheckedPercentage()
            notifyDataSetChanged()
        }

        view.setOnClickListener {
            onChallengeClick(challenge, view)
        }

        return view
    }

    // 체크된 항목들 중에서 최상단으로 이동
    private fun moveItemToCheckedGroup(position: Int) {
        val item = challenges.removeAt(position)

        // 체크된 그룹의 첫 번째 위치 찾기
        val insertPosition = challenges.indexOfFirst { it.isChecked }.takeIf { it >= 0 } ?: challenges.size
        challenges.add(insertPosition, item)
    }

    // 체크되지 않은 항목들 중 가장 아래로 이동
    private fun moveItemToUncheckedGroup(position: Int) {
        val item = challenges.removeAt(position)

        // 체크되지 않은 그룹의 마지막 위치 찾기
        val insertPosition = challenges.indexOfFirst { it.isChecked }.takeIf { it >= 0 } ?: challenges.size
        challenges.add(insertPosition, item)
    }

    // 순서를 SharedPreferences에 저장
    private fun saveOrderToPreferences() {
        val orderArray = JSONArray()
        challenges.forEach { challenge -> orderArray.put(challenge.id) }

        sharedPreferences.edit().putString("challenge_order", orderArray.toString()).apply()
    }

    // 저장된 순서를 불러오기
    private fun loadOrderFromPreferences() {
        val orderString = sharedPreferences.getString("challenge_order", null)

        if (!orderString.isNullOrEmpty()) {
            val orderArray = JSONArray(orderString)
            val orderedChallenges = mutableListOf<Challenge>()

            for (i in 0 until orderArray.length()) {
                val challengeId = orderArray.getInt(i)
                challenges.find { it.id == challengeId }?.let { orderedChallenges.add(it) }
            }

            // 기존 리스트 업데이트
            challenges.clear()
            challenges.addAll(orderedChallenges)
        }
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

    fun getCheckedPercentage(): Int {
        return sharedPreferences.getInt("checked_percentage", 0)
    }
}
