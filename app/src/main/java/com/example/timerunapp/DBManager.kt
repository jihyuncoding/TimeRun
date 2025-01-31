package com.example.timerunapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class DBManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ChallengeDB"
        private const val DATABASE_VERSION = 4  // 버전 증가

        private const val TABLE_NAME = "Challenges"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_GOAL = "goal"
        private const val COL_CATEGORY = "category"
        private const val COL_START_DATE = "start_date"
        private const val COL_DURATION = "duration"
        private const val COL_IS_COMPLETED = "is_completed"
        private const val COL_D_DAY = "d_day"
        private const val COL_END_DATE = "end_date"
        private const val COL_IS_CHECKED = "is_checked"  // isChecked 컬럼 추가
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT NOT NULL,
                $COL_GOAL TEXT NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_START_DATE TEXT NOT NULL,
                $COL_DURATION INTEGER NOT NULL,
                $COL_IS_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COL_D_DAY INTEGER NOT NULL,
                $COL_END_DATE TEXT NOT NULL,
                $COL_IS_CHECKED INTEGER NOT NULL DEFAULT 0  -- isChecked 컬럼 추가
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            // 데이터베이스 버전 4로 업그레이드 시 isChecked 컬럼 추가
            val alterTableQuery = "ALTER TABLE $TABLE_NAME ADD COLUMN $COL_IS_CHECKED INTEGER NOT NULL DEFAULT 0"
            db.execSQL(alterTableQuery)
        }
        // 필요 시 더 많은 업그레이드 쿼리 추가 가능
    }

    // 챌린지 추가
    fun insertChallenge(name: String, goal: String, category: String, startDate: String, duration: Int, isCompleted: Int, dDay: String, isChecked: Boolean): Long {
        val db = writableDatabase
        val endDate = calculateEndDate(startDate, duration)  // end_date 계산
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_GOAL, goal)
            put(COL_CATEGORY, category)
            put(COL_START_DATE, startDate)
            put(COL_DURATION, duration)
            put(COL_IS_COMPLETED, isCompleted)
            put(COL_D_DAY, dDay)
            put(COL_END_DATE, endDate)
            put(COL_IS_CHECKED, if (isChecked) 1 else 0)  // isChecked 값을 1 또는 0으로 저장
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // end_date 계산 (start_date + duration)
    private fun calculateEndDate(startDate: String, duration: Int): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val start = dateFormat.parse(startDate)
        val calendar = Calendar.getInstance()
        calendar.time = start
        calendar.add(Calendar.DAY_OF_YEAR, duration)  // duration일을 더함
        return dateFormat.format(calendar.time)
    }

    // 챌린지 업데이트 (isChecked 상태 업데이트)
    fun updateChallengeCheckedStatus(challengeId: Int, isChecked: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_IS_COMPLETED, if (isChecked) 1 else 0) // 체크된 상태를 1로, 해제된 상태를 0으로 저장
        }
        db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(challengeId.toString()))
    }

    fun getChallengeCheckedStatus(challengeId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COL_IS_CHECKED),
            "$COL_ID = ?",
            arrayOf(challengeId.toString()),
            null, null, null
        )

        var isChecked = false
        if (cursor.moveToFirst()) {
            val checkedColumnIndex = cursor.getColumnIndex(COL_IS_CHECKED)
            isChecked = cursor.getInt(checkedColumnIndex) == 1
        }

        cursor.close()
        return isChecked
    }
}
