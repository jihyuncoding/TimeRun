package com.example.timerunapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 하단 네비게이션 아이템 선택 시 화면 전환
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.fragment_home -> {
                    loadFragment(FragmentMain())
                    true
                }
                R.id.fragment_add -> {
                    loadFragment(FragmentAddTodo())
                    true
                }
                R.id.fragment_search -> {
                    loadFragment(FragmentInfoTodo())
                    true
                }
                else -> false
            }
        }

        // 앱 실행 시 처음 화면 설정
        if (savedInstanceState == null) {
            loadFragment(FragmentMain()) // "MainFragment"로 설정
        }
    }

    // Fragment를 동적으로 로딩하는 함수
    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // 뒤로가기 버튼 지원
        transaction.commit()
    }
}
