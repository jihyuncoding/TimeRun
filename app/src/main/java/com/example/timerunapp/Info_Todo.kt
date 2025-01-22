package com.example.timerunapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.timerunapp.R

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
                val goal = cursor.getString(cursor.getColumnIndexOrThrow("goal"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val startDate = cursor.getString(cursor.getColumnIndexOrThrow("start_date"))
                val endDate = cursor.getString(cursor.getColumnIndexOrThrow("end_date"))
                challenges.add(Challenge(id, name, goal, category, startDate, endDate))
            } while (cursor.moveToNext())
        }
        cursor.close()

        // 어댑터 생성하고 ListView에 설정
        val adapter = ChallengeAdapter(requireContext(), challenges)
        listView.adapter = adapter

        return rootView
    }
}

