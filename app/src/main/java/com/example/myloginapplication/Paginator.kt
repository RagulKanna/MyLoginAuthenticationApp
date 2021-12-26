package com.example.myloginapplication

class Paginator {

    fun generatePage(noteList: ArrayList<NoteData>, currentPage: Int): ArrayList<NoteData> {
        val totalNotes = noteList.size
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
        } else {
            pageData.clear()
            for (i in startItem until startItem + numOfData) {
                pageData.add(noteList[i - 1])
            }
        }
        return pageData
    }

    companion object {
        const val NOTES_PER_PAGE = 7
    }
}