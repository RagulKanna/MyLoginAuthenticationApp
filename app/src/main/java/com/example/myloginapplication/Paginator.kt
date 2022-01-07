package com.example.myloginapplication

import android.content.Context
import android.util.Log
import android.widget.Toast

class Paginator {

    fun generatePage(noteList: ArrayList<NoteData>, currentPage: Int,context: Context): ArrayList<NoteData> {
        val totalNotes = noteList.size
        Log.d("check","$totalNotes")
        val notesRemaining = totalNotes % NOTES_PER_PAGE
        val lastPageNotes = totalNotes / NOTES_PER_PAGE
        val startItem = currentPage * NOTES_PER_PAGE + 1
        val numOfData = NOTES_PER_PAGE
        val pageData = ArrayList<NoteData>()
        if (currentPage == lastPageNotes && notesRemaining > 0) {
            pageData.clear()
            for (i in startItem until startItem + notesRemaining) {
                pageData.add(noteList[i - 1])
            }
        }else if(totalNotes == 0){
            noItemInArray(context)
        }
        else {
            pageData.clear()
            for (i in startItem until startItem + numOfData) {
                pageData.add(noteList[i - 1])
            }
        }
        return pageData
    }

    private fun noItemInArray(context: Context){
        Toast.makeText(context,"No Data to Show", Toast.LENGTH_SHORT).show()
    }
    companion object {
        const val NOTES_PER_PAGE = 7
    }
}