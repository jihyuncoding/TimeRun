package com.example.timerunapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 사용자가 로그인했는지 여부 확인
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // 로그인 안 했으면 로그인 화면으로 이동
        if (!isLoggedIn) {
            val loginIntent = Intent(this, ActivityLogin::class.java)
            startActivity(loginIntent)
            finish() // 로그인 화면을 지나 MainActivity로 바로 가지 않도록 함
        } else {
            // 로그인했다면 기존의 메인 화면 레이아웃을 설정
            setContentView(R.layout.activity_main)
        }

        // BottomNavigationView 초기화
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // 초기 선택된 아이템을 홈으로 설정
        bottomNavigationView.selectedItemId = R.id.fragment_home

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