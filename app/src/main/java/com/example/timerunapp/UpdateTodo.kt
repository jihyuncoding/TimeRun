package com.example.timerunapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class UpdateTodo : AppCompatActivity() {

    private lateinit var challengeNameInput: EditText
    private lateinit var goalInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var threeDaysCheckBox: CheckBox
    private lateinit var sevenDaysCheckBox: CheckBox
    private lateinit var saveButton: Button

    private lateinit var dbManager: DBManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_update)

        // DBManager 초기화
        dbManager = DBManager(this)

        // XML 요소 연결
        challengeNameInput = findViewById(R.id.challengeName)
        goalInput = findViewById(R.id.goalInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        threeDaysCheckBox = findViewById(R.id.threeDaysCheckBox)
        sevenDaysCheckBox = findViewById(R.id.sevenDaysCheckBox)
        saveButton = findViewById(R.id.addChallengeButton)

        // 전달받은 데이터 읽기
        val id = intent.getIntExtra("id", -1)
        val name = intent.getStringExtra("name") ?: ""
        val goal = intent.getStringExtra("goal") ?: ""
        val category = intent.getStringExtra("category") ?: ""

        // 뒤로가기 버튼
        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()  // 현재 액티비티 종료 (이전 화면으로 이동)
        }

        // UI에 기존 데이터 표시
        challengeNameInput.setText(name)
        goalInput.setText(goal)

        // Spinner 초기화
        val categories = arrayOf("운동", "독서", "건강", "학습", "기타")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        categorySpinner.setSelection(categories.indexOf(category))

        // 체크박스 상호 배제 로직
        threeDaysCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) sevenDaysCheckBox.isChecked = false
        }

        sevenDaysCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) threeDaysCheckBox.isChecked = false
        }

        // 저장 버튼 클릭 리스너
        saveButton.setOnClickListener {
            val updatedName = challengeNameInput.text.toString()
            val updatedGoal = goalInput.text.toString()
            val updatedCategory = categorySpinner.selectedItem.toString()
            val updatedDuration = when {
                threeDaysCheckBox.isChecked -> 3
                sevenDaysCheckBox.isChecked -> 7
                else -> 0
            }

            if (updatedName.isNotEmpty() && updatedGoal.isNotEmpty() && updatedDuration > 0) {
                // DB 업데이트
                val contentValues = ContentValues().apply {
                    put("name", updatedName)
                    put("goal", updatedGoal)
                    put("category", updatedCategory)
                    put("duration", updatedDuration)
                }
                dbManager.writableDatabase.update("Challenges", contentValues, "id=?", arrayOf(id.toString()))

                // 결과 반환
                val resultIntent = Intent().apply {
                    putExtra("updated", true)
                }
                setResult(RESULT_OK, resultIntent)

                Toast.makeText(this, "챌린지가 수정되었습니다!", Toast.LENGTH_SHORT).show()
                finish() // 액티비티 종료


            } else {
                Toast.makeText(this, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
