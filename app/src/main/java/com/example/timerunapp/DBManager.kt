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
        private const val DATABASE_VERSION = 3

        private const val TABLE_NAME = "Challenges"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_GOAL = "goal"
        private const val COL_CATEGORY = "category"
        private const val COL_START_DATE = "start_date"
        private const val COL_DURATION = "duration"
        private const val COL_IS_COMPLETED = "is_completed" // 체크 안 하면 0, 하면 1
        private const val COL_D_DAY = "d_day"
        private const val COL_END_DATE = "end_date"
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
                $COL_END_DATE TEXT NOT NULL  -- end_date 컬럼 추가
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            val alterTableQuery = "ALTER TABLE $TABLE_NAME ADD COLUMN $COL_END_DATE TEXT"
            db.execSQL(alterTableQuery)
        }
    }

    // 챌린지 추가
    fun insertChallenge(name: String, goal: String, category: String, startDate: String, duration: Int, isCompleted: Int, dDay: String): Long {
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
}
