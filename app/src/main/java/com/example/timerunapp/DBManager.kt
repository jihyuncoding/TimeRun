package com.example.timerunapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// 데이터베이스 관리 클래스, SQLiteOpenHelper를 상속받아 구현
class DBManager(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // 데이터베이스 이름과 버전 정의
        private const val DATABASE_NAME = "ChallengeDB" // 데이터베이스 이름
        private const val DATABASE_VERSION = 1 // 데이터베이스 버전

        // 테이블 및 컬럼 이름 정의
        private const val TABLE_NAME = "Challenges" // 테이블 이름
        private const val COL_ID = "id" // 고유 ID 컬럼
        private const val COL_NAME = "name" // 챌린지 이름
        private const val COL_GOAL = "goal" // 챌린지 목표
        private const val COL_CATEGORY = "category" // 챌린지 카테고리
        private const val COL_START_DATE = "start_date" // 시작 날짜
        private const val COL_END_DATE = "end_date" // 종료 날짜
    }

    // 데이터베이스 최초 생성 시 호출되는 메서드
    override fun onCreate(db: SQLiteDatabase) {
        // 테이블 생성 쿼리
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, -- 고유 ID, 자동 증가
                $COL_NAME TEXT NOT NULL, -- 챌린지 이름 (NULL 불가)
                $COL_GOAL TEXT NOT NULL, -- 챌린지 목표 (NULL 불가)
                $COL_CATEGORY TEXT NOT NULL, -- 챌린지 카테고리 (NULL 불가)
                $COL_START_DATE TEXT NOT NULL, -- 시작 날짜 (NULL 불가)
                $COL_END_DATE TEXT NOT NULL -- 종료 날짜 (NULL 불가)
            )
        """
        // 테이블 생성 실행
        db.execSQL(createTableQuery)
    }

    // 데이터베이스 업그레이드 시 호출되는 메서드
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 기존 테이블 삭제
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        // 새로운 테이블 생성
        onCreate(db)
    }

    // 새로운 챌린지를 테이블에 삽입하는 메서드
    fun insertChallenge(name: String, goal: String, category: String, startDate: String, endDate: String): Long {
        val db = writableDatabase // 쓰기 가능한 데이터베이스 객체 가져오기
        // 삽입할 데이터를 ContentValues 객체에 담기
        val values = ContentValues().apply {
            put(COL_NAME, name) // 챌린지 이름
            put(COL_GOAL, goal) // 챌린지 목표
            put(COL_CATEGORY, category) // 챌린지 카테고리
            put(COL_START_DATE, startDate) // 시작 날짜
            put(COL_END_DATE, endDate) // 종료 날짜
        }
        // 데이터를 테이블에 삽입하고 삽입된 행의 ID 반환
        return db.insert(TABLE_NAME, null, values)
    }
}

