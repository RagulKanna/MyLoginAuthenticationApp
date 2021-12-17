package com.example.myloginapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val db: SQLiteDatabase = this.writableDatabase
    override fun onCreate(db: SQLiteDatabase?) {
        val createQuery =
            "create table $TABLE_NAME($ID INTEGER primary key autoincrement, $TITLE TEXT, $CONTENT TEXT, $DATETIME TEXT, $ARCHIVED TEXT, $FIRESTOREID TEXT);"
        db?.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropQuery = "drop table if exists $TABLE_NAME"
        db?.execSQL(dropQuery)
        onCreate(db)
    }

    fun saveData(noteData: NoteData) {

        val content = ContentValues()
        content.put(FIRESTOREID, noteData.id)
        content.put(TITLE, noteData.title)
        content.put(CONTENT, noteData.noteContent)
        content.put(DATETIME, noteData.dateTime)
        content.put(ARCHIVED, noteData.archive)
        db.insert(TABLE_NAME, null, content)
        db.close()
    }

    fun deleteData(id: String) {
        db.delete(TABLE_NAME, "ID = ?", arrayOf(id))
    }

    fun updateData(id: String, title: String, noteContent: String, datetime: String) {
        val content = ContentValues()
        content.put(FIRESTOREID, id)
        content.put(TITLE, title)
        content.put(CONTENT, noteContent)
        content.put(DATETIME, datetime)
        db.update(TABLE_NAME, content, "ID = ?", arrayOf(id))
    }

    companion object {
        const val DATABASE_NAME = "NoteDatabase"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "NoteTable"
        const val ID = "id"
        const val TITLE = "Title"
        const val CONTENT = "Content"
        const val DATETIME = "DateTime"
        const val ARCHIVED = "Archived"
        const val FIRESTOREID = "FirestoreID"
    }
}