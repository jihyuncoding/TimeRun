package com.example.timerunapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val emailEditText: EditText = findViewById(R.id.etSignUpEmail)
        val passwordEditText: EditText = findViewById(R.id.etSignUpPassword)
        val signUpButton: Button = findViewById(R.id.btnSignUp)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 회원가입 로직 (여기서는 단순히 SharedPreferences에 저장)
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                // 사용자 이메일과 비밀번호를 SharedPreferences에 저장 (비밀번호는 암호화 권장)
                editor.putString("userEmail", email)
                editor.putString("userPassword", password)
                editor.apply()

                Toast.makeText(this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show()

                // 회원가입 후 로그인 화면으로 이동
                val loginIntent = Intent(this, ActivityLogin::class.java)
                startActivity(loginIntent)
                finish() // 회원가입 화면 종료
            }
        }
    }
}