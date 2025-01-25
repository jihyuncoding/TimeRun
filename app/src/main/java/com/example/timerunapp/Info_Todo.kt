package com.example.timerunapp

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

class FragmentInfoTodo : Fragment() {

    private lateinit var dbManager: DBManager
    private lateinit var listView: ListView
    private lateinit var adapter: ChallengeAdapter
    private var challenges = mutableListOf<Challenge>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_info_todo.xml 레이아웃 연결
        val rootView = inflater.inflate(R.layout.fragment_info_todo, container, false)

        dbManager = DBManager(requireContext())

        // ListView 연결
        listView = rootView.findViewById(R.id.listView)

        // 데이터 로드 및 UI 초기화
        refreshData()

        return rootView
    }

    // 데이터 새로고침 함수
    private fun refreshData() {
        val db = dbManager.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Challenges", null)

        challenges.clear()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name")) ?: "이름 없음"
                val goal = cursor.getString(cursor.getColumnIndexOrThrow("goal")) ?: "목표 없음"
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category")) ?: "카테고리 없음"
                val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date")) ?: "0000-00-00"
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed"))
                val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date")) ?: "0000-00-00"

                // D-Day 계산
                val dDay = calculateDDay(startDate, duration)

                challenges.add(
                    Challenge(
                        id, name, goal, category, startDate, duration, isCompleted, dDay, endDate
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        // 어댑터 연결
        adapter = ChallengeAdapter(requireContext(), challenges) { challenge ->
            showChallengeDetails(challenge)
        }
        listView.adapter = adapter
    }

    // 세부 정보 표시
    private fun showChallengeDetails(challenge: Challenge) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_challenge_details, null)

        val nameTextView = view.findViewById<TextView>(R.id.challengeName)
        val goalTextView = view.findViewById<TextView>(R.id.challengeGoal)
        val categoryTextView = view.findViewById<TextView>(R.id.categorySpinner)
        val dateTextView = view.findViewById<TextView>(R.id.challengeDate)

        val editButton = view.findViewById<Button>(R.id.editButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)

        nameTextView.text = challenge.name
        goalTextView.text = challenge.goal
        categoryTextView.text = challenge.category
        dateTextView.text = "${challenge.startDate} ~ ${challenge.endDate}"

        // 수정 버튼 클릭
        editButton.setOnClickListener {
            if (challenge.isCompleted == 1) {
                AlertDialog.Builder(requireContext())
                    .setTitle("수정 불가")
                    .setMessage("이미 완료된 목표는 수정할 수 없습니다.")
                    .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
                    .show()
            } else {
                val intent = Intent(requireContext(), UpdateTodo::class.java).apply {
                    putExtra("id", challenge.id)
                    putExtra("name", challenge.name)
                    putExtra("goal", challenge.goal)
                    putExtra("category", challenge.category)
                }
                updateLauncher.launch(intent)
                dialog.dismiss()
            }
        }

        // 삭제 버튼 클릭
        deleteButton.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("삭제 확인")
                .setMessage("이 목표를 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    val db = dbManager.writableDatabase
                    db.delete("Challenges", "id=?", arrayOf(challenge.id.toString()))
                    db.close()
                    refreshData()
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "챌린지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    // 수정 결과 처리
    private val updateLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val isUpdated = result.data?.getBooleanExtra("updated", false) ?: false
            if (isUpdated) {
                refreshData()
            }
        }
    }

    // D-day 계산
    private fun calculateDDay(startDate: String, duration: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val today = Date()

        val diffInMillis = start.time - today.time
        val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

        val remainingDays = duration - diffInDays
        return if (remainingDays > 0) {
            "D-$remainingDays"
        } else {
            "D-day"
        }
    }
}
