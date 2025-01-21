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
                R.id.fragment_home-> {
                    val mainFragment = FragmentMain()
                    loadFragment(mainFragment)
                    true
                }
                R.id.fragment_add -> {
                    val addTodoFragment = FragmentAddTodo()
                    loadFragment(addTodoFragment)
                    true
                }
                R.id.fragment_search -> {
                    val profileFragment = FragmentInfoTodo()
                    loadFragment(profileFragment)
                    true
                }
                else -> false
            }
        }

        // 앱 실행 시 처음 화면은 Main Fragment로 설정
        if (savedInstanceState == null) {
            val fragment = FragmentMain() // 처음 화면을 "MainFragment"로 설정
            loadFragment(fragment)
        }
    }

    // Fragment를 동적으로 로딩하는 함수
    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // 뒤로가기 버튼을 위한 트랜잭션 추가
        transaction.commit()
    }
}

