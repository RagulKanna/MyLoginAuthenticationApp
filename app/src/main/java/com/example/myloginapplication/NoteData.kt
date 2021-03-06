package com.example.myloginapplication

import java.io.Serializable

data class NoteData(
    var title: String? = null,
    var noteContent: String? = null,
    var id: String? = null,
    var dateTime: String? = null,
    var archive: String? = null,
    var label: ArrayList<String>? = null
) : Serializable
