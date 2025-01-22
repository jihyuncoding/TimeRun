package com.example.timerunapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ChallengeDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Challenges"
        private const val COL_ID = "id"
        private const val COL_NAME = "name"
        private const val COL_GOAL = "goal"
        private const val COL_CATEGORY = "category"
        private const val COL_START_DATE = "start_date"
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
                $COL_END_DATE TEXT NOT NULL
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertChallenge(name: String, goal: String, category: String, startDate: String, endDate: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_GOAL, goal)
            put(COL_CATEGORY, category)
            put(COL_START_DATE, startDate)
            put(COL_END_DATE, endDate)
        }
        return db.insert(TABLE_NAME, null, values)
    }
}
