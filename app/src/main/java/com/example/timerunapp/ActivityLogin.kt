package com.example.timerunapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText: EditText = findViewById(R.id.etEmail)
        val passwordEditText: EditText = findViewById(R.id.etPassword)
        val loginButton: Button = findViewById(R.id.btnLogin)
        val signUpTextView: TextView = findViewById(R.id.tvSignUp)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 로그인 로직 (예: 임시로 고정된 값 비교)
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val storedEmail = sharedPreferences.getString("userEmail", "")
                val storedPassword = sharedPreferences.getString("userPassword", "")

                if (email == storedEmail && password == storedPassword) {
                    // 로그인 성공 시 SharedPreferences에 로그인 상태 저장
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.apply()

                    Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "이메일 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 회원가입 화면으로 이동
        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}