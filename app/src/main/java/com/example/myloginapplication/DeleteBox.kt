package com.example.myloginapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlin.properties.Delegates

class DeleteBox : Fragment() {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteArrayList: ArrayList<NoteData>
    private var flag by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        noteArrayList = arrayListOf()
        noteAdapter = NoteAdapter(noteArrayList, requireContext())
        deleteAlertBox()
    }


    private fun deleteAlertBox() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete Note")
        builder.setMessage("Are You want to delete this note?")
        builder.setIcon(R.drawable.ic_baseline_warning_24)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            val noteServices = NoteServices()
            val context: Context = requireContext()
            val data = this.arguments
            val id: String = data?.getString("id").toString()
            flag = data?.getInt("flag")!!
            noteServices.deleteNote(context, noteAdapter, id)
        }
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(context, "Deleting note is cancelled", Toast.LENGTH_SHORT).show()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }
}

