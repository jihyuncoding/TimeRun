package com.example.timerunapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class FragmentAddTodo : Fragment() {

    private lateinit var challengeNameEditText: EditText
    private lateinit var goalEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var threeDaysCheckBox: CheckBox
    private lateinit var sevenDaysCheckBox: CheckBox
    private lateinit var addChallengeButton: Button

    private lateinit var dbManager: DBManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_add_todo.xml 레이아웃 연결
        return inflater.inflate(R.layout.fragment_add_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DBManager 초기화
        dbManager = DBManager(requireContext())

        // XML 요소 연결
        challengeNameEditText = view.findViewById(R.id.challengeName)
        goalEditText = view.findViewById(R.id.goalInput)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        threeDaysCheckBox = view.findViewById(R.id.threeDaysCheckBox)
        sevenDaysCheckBox = view.findViewById(R.id.sevenDaysCheckBox)
        addChallengeButton = view.findViewById(R.id.addChallengeButton)

        // Spinner 데이터 설정
        val categories = arrayOf("카테고리를 선택하세요.", "운동", "독서", "건강", "학습", "기타")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        categorySpinner.setSelection(0)

        // 체크박스 상호 배제 로직 설정
        threeDaysCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sevenDaysCheckBox.isChecked = false
        }

        sevenDaysCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) threeDaysCheckBox.isChecked = false
        }

        // 챌린지 추가 버튼 클릭 리스너
        addChallengeButton.setOnClickListener {
            val challengeName = challengeNameEditText.text.toString()
            val goal = goalEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()
            val duration = when {
                threeDaysCheckBox.isChecked -> 3
                sevenDaysCheckBox.isChecked -> 7
                else -> 0
            }

            if (challengeName.isNotEmpty() && goal.isNotEmpty() &&
                category != "카테고리를 선택하세요." && duration > 0
            ) {
                val startDate = getCurrentDate()
                val dDay = calculateDDayValue(startDate, duration)
                val isCompleted = 0

                // DB에 데이터 저장
                dbManager.insertChallenge(challengeName, goal, category, startDate, duration, isCompleted, dDay)

                // UI 초기화
                challengeNameEditText.text.clear()
                goalEditText.text.clear()
                categorySpinner.setSelection(0)
                threeDaysCheckBox.isChecked = false
                sevenDaysCheckBox.isChecked = false

                Toast.makeText(requireContext(), "챌린지가 추가되었습니다!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 현재 날짜 가져오기
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    // 디데이 계산 (목표 기간에 맞춰서, 문자열 반환)
    private fun calculateDDayValue(startDate: String, duration: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val today = Date()

        // 현재 날짜에서 시작 날짜를 뺀 값을 일수로 계산
        val diffInMillis = today.time - start.time
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        // 목표 기간에서 지난 날짜를 뺀 남은 일수 계산
        val remainingDays = duration - diffInDays

        // 남은 일수가 0일이면 "D-Day", 1일 이상이면 "D-남은일수" 반환
        return if (remainingDays > 0) {
            "D-$remainingDays"
        } else {
            "D-Day"
        }
    }

}
