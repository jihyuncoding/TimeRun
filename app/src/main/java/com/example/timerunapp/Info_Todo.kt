package com.example.timerunapp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                try {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name")) ?: "이름 없음"
                    val goal = cursor.getString(cursor.getColumnIndexOrThrow("goal")) ?: "목표 없음"
                    val category = cursor.getString(cursor.getColumnIndexOrThrow("category")) ?: "카테고리 없음"
                    val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date")) ?: "0000-00-00"
                    val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date")) ?: "0000-00-00"

                    // duration 열 처리 (존재 여부 확인 후 기본값 설정)
                    val duration = cursor.getColumnIndex("duration")?.let {
                        cursor.getInt(it)
                    } ?: 0

                    // is_completed 처리 (존재하지 않는 경우 기본값 설정)
                    val isCompleted = cursor.getColumnIndex("is_completed")?.let {
                        cursor.getInt(it)
                    } ?: 0

                    // D-day 계산
                    val dDay = calculateDDay(startDate, duration)

                    // DB 업데이트
                    val dDayValue = dDay.removePrefix("D-").toIntOrNull() ?: 0
                    val contentValues = ContentValues().apply {
                        put("d_day", dDayValue)
                    }
                    db.update("Challenges", contentValues, "id=?", arrayOf(id.toString()))

                    // Challenge 생성 후 리스트에 추가
                    challenges.add(
                        Challenge(
                            id, name, goal, category, startDate, duration, isCompleted, dDay, endDate
                        )
                    )
                } catch (e: Exception) {
                    Log.e("FragmentInfoTodo", "Error processing challenge: ${e.message}")
                }
            } while (cursor.moveToNext())
        } else {
            Log.d("FragmentInfoTodo", "No data found in Challenges table")
        }

        cursor.close()

        // 어댑터 생성하고 ListView에 설정
        if (challenges.isEmpty()) {
            Log.d("FragmentInfoTodo", "No challenges to display")
            // TODO: 빈 화면 처리를 위한 UI 작업 추가
        } else {
            val adapter = ChallengeAdapter(requireContext(), challenges)
            listView.adapter = adapter
        }

        return rootView
    }

    // D-day 계산 함수(앱 실행 때마다 날짜 계산해서 DB에 있는 디데이가 변경됨)
    private fun calculateDDay(startDate: String, duration: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val start = dateFormat.parse(startDate)
            val today = Date()

            // 시작일과 현재 날짜의 차이 계산
            val diffInMillis = start.time - today.time
            val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

            // 목표 기간이 지나지 않았다면 D-day 값 반환
            val remainingDays = duration - diffInDays
            if (remainingDays > 0) {
                "D-$remainingDays"
            } else {
                "D-day"
            }
        } catch (e: Exception) {
            Log.e("FragmentInfoTodo", "Invalid date format: $startDate")
            "D-day"
        }
    }
}
