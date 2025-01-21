package com.example.timerunapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.timerunapp.R
import java.text.SimpleDateFormat
import java.util.*

class FragmentAddTodo : Fragment() {

    private lateinit var challengeNameEditText: EditText
    private lateinit var goalEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var startDateTextView: TextView
    private lateinit var endDateTextView: TextView
    private val calendar = Calendar.getInstance()

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
        startDateTextView = view.findViewById(R.id.startDate)
        endDateTextView = view.findViewById(R.id.endDate)

        // Spinner에 데이터 추가
        val categories = arrayOf("카테고리를 선택하세요.", "운동", "독서", "건강", "학습", "기타")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // 스피너 기본 선택 설정
        categorySpinner.setSelection(0)

        // 날짜 선택 초기화
        startDateTextView.text = "시작일을 선택하세요."
        endDateTextView.text = "종료일을 선택하세요."

        // 시작일 클릭 리스너
        startDateTextView.setOnClickListener {
            showDatePickerDialog { year, month, day ->
                calendar.set(year, month, day)
                startDateTextView.text = formatDate(calendar.time)
            }
        }

        // 종료일 클릭 리스너
        endDateTextView.setOnClickListener {
            showDatePickerDialog { year, month, day ->
                calendar.set(year, month, day)
                endDateTextView.text = formatDate(calendar.time)
            }
        }

        // 챌린지 추가 버튼 클릭 리스너
        val addChallengeButton: Button = view.findViewById(R.id.addChallengeButton)
        addChallengeButton.setOnClickListener {
            val challengeName = challengeNameEditText.text.toString()
            val goal = goalEditText.text.toString()
            val category = categorySpinner.selectedItem.toString()
            val startDate = startDateTextView.text.toString()
            val endDate = endDateTextView.text.toString()

            // 날짜가 선택되지 않았을 경우 챌린지 추가 불가
            if (challengeName.isNotEmpty() && goal.isNotEmpty() &&
                category != "카테고리를 선택하세요." &&
                startDate != "시작일을 선택하세요." &&
                endDate != "종료일을 선택하세요."
            ) {

                dbManager.insertChallenge(challengeName, goal, category, startDate, endDate)

                // 화면 초기화
                challengeNameEditText.text.clear()
                goalEditText.text.clear()

                // 날짜 초기화
                startDateTextView.text = "시작일을 선택하세요."
                endDateTextView.text = "종료일을 선택하세요."

                // 카테고리 초기화
                categorySpinner.setSelection(0)
            } else {
                // 모든 항목을 입력하라는 메시지 표시
                Toast.makeText(requireContext(), "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 날짜 포맷 함수
    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    // DatePickerDialog 표시 함수
    private fun showDatePickerDialog(onDateSet: (Int, Int, Int) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                onDateSet(selectedYear, selectedMonth, selectedDay)
            }, year, month, day)

        datePickerDialog.show()
    }
}