package com.example.timerunapp

import android.app.AlertDialog
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
        val rootView = inflater.inflate(R.layout.fragment_info_todo, container, false)

        dbManager = DBManager(requireContext())

        listView = rootView.findViewById(R.id.listView)

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

                val dDay = calculateDDay(startDate, duration)

                challenges.add(
                    Challenge(id, name, goal, category, startDate, duration, isCompleted, dDay, endDate)
                )
            } while (cursor.moveToNext())
        }
        cursor.close()

        // ChallengeAdapter 수정: View도 전달하도록 변경
        adapter = ChallengeAdapter(requireContext(), challenges) { challenge, view ->
            showChallengeDetails(challenge, view)
        }
        listView.adapter = adapter
    }

    // 세부 정보 표시 다이얼로그
    private fun showChallengeDetails(challenge: Challenge, itemView: View) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_challenge_details, null)

        val nameTextView = view.findViewById<TextView>(R.id.challengeName)
        val goalTextView = view.findViewById<TextView>(R.id.challengeGoal)
        val categoryTextView = view.findViewById<TextView>(R.id.categorySpinner)
        val dateTextView = view.findViewById<TextView>(R.id.challengeDate)

        val editButton = view.findViewById<Button>(R.id.editButton)
        val deleteButton = view.findViewById<Button>(R.id.deleteButton)

        // ✅ 리스트 아이템 뷰에서 체크박스를 가져옴
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)

        nameTextView.text = challenge.name
        goalTextView.text = challenge.goal
        categoryTextView.text = challenge.category
        dateTextView.text = "${challenge.startDate} ~ ${challenge.endDate}"

        // ✅ 수정 버튼을 항상 활성화 상태로 유지 (색상 변화 X)
        editButton.isEnabled = true

        // ✅ 수정 버튼 클릭 이벤트
        editButton.setOnClickListener {
            if (checkBox.isChecked) {
                // ✅ 체크박스가 체크된 경우 → 경고 메시지 표시
                Toast.makeText(requireContext(), "완료된 목표는 수정이 불가합니다.", Toast.LENGTH_SHORT).show()
            } else {
                // ✅ 체크박스가 체크되지 않은 경우 → 정상적으로 수정 화면 이동
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

        // ✅ 삭제 버튼 클릭
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
    private val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val isUpdated = result.data?.getBooleanExtra("updated", false) ?: false
                if (isUpdated) {
                    refreshData()
                }
            }
        }

    // D-day 계산 함수
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
