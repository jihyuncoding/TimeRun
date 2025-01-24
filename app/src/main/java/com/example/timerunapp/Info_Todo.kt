package com.example.timerunapp

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.timerunapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentInfoTodo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_info_todo.xml 레이아웃을 연결
        val rootView = inflater.inflate(R.layout.fragment_info_todo, container, false)

        // ListView를 찾기
        val listView: ListView = rootView.findViewById(R.id.listView)

        // DB에서 데이터를 읽어오기
        val dbManager = DBManager(requireContext())
        val db = dbManager.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Challenges", null)

        val challenges = mutableListOf<Challenge>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    ?: "이름 없음" // 초기화 안 해주면 계속 에러남
                val goal = cursor.getString(cursor.getColumnIndexOrThrow("goal")) ?: "목표 없음"
                val category =
                    cursor.getString(cursor.getColumnIndexOrThrow("category")) ?: "카테고리 없음"
                val startDate =
                    cursor.getString(cursor.getColumnIndexOrThrow("start_date")) ?: "0000-00-00"
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed"))
                // 새로운 D-day 계산
                val dDay = calculateDDay(startDate, duration)

                // DB 업데이트
                val contentValues = ContentValues()
                contentValues.put("d_day", dDay.removePrefix("D-").toIntOrNull() ?: 0)
                db.update("Challenges", contentValues, "id=?", arrayOf(id.toString()))
                val endDate =
                    cursor.getString(cursor.getColumnIndexOrThrow("end_date")) ?: "0000-00-00"

                // Challenge 생성 후 리스트에 추가
                challenges.add(
                    Challenge(
                        id,
                        name,
                        goal,
                        category,
                        startDate,
                        duration,
                        isCompleted,
                        dDay,
                        endDate
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()

        // 어댑터 생성
        val adapter = ChallengeAdapter(requireContext(), challenges) { challenge ->
            showChallengeDetails(challenge) // 클릭된 챌린지 세부 정보 표시
        }
        listView.adapter = adapter

        return rootView
    }

    // 세부 정보를 표시하는 함수
    private fun showChallengeDetails(challenge: Challenge) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_challenge_details, null)

        val nameTextView = view.findViewById<TextView>(R.id.challengeName)
        val goalTextView = view.findViewById<TextView>(R.id.challengeGoal)
        val categoryTextView = view.findViewById<TextView>(R.id.categorySpinner)
        val dateTextView = view.findViewById<TextView>(R.id.challengeDate)

        nameTextView.text = challenge.name
        goalTextView.text = challenge.goal
        categoryTextView.text = challenge.category
        dateTextView.text = "${challenge.startDate} ~ ${challenge.endDate}"

        dialog.setContentView(view)
        dialog.show()
    }

    // D-day 계산 함수(앱 실행 때마다 날짜 계산해서 DB에 있는 디데이가 변경됨)
    private fun calculateDDay(startDate: String, duration: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val today = Date()

        // 시작일과 현재 날짜의 차이 계산
        val diffInMillis = start.time - today.time
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        // 목표 기간이 지나지 않았다면 D-day 값 반환
        val remainingDays = duration - diffInDays
        return if (remainingDays > 0) {
            "D-$remainingDays"
        } else {
            "D-day"
        }
    }
}
