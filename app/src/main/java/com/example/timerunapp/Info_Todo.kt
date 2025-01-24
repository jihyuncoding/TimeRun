package com.example.timerunapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class FragmentInfoTodo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_info_todo, container, false)

        val listView: ListView = rootView.findViewById(R.id.listView)

        // DB에서 데이터 읽기
        val dbManager = DBManager(requireContext())
        val db = dbManager.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Challenges", null)

        val challenges = mutableListOf<Challenge>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val goal = cursor.getString(cursor.getColumnIndexOrThrow("goal"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"))
                val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"))
                challenges.add(Challenge(id, name, goal, category, startDate, endDate))
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
}
