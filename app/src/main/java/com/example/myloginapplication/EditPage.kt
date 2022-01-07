package com.example.myloginapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


class EditPage : Fragment() {

    private lateinit var editTitle: EditText
    private lateinit var editNotes: EditText
    private lateinit var saveFab: FloatingActionButton
    private lateinit var backButton: ImageButton
    private lateinit var id: String
    private lateinit var note: NoteData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_edit_notes, container, false)
        saveFab = view.findViewById(R.id.save)
        editTitle = view.findViewById(R.id.savedTitle)
        editNotes = view.findViewById(R.id.savedNote)
        backButton = view.findViewById(R.id.backButton)
        val data = this.arguments
        note = data?.getSerializable("noteObject") as NoteData
        val title = note.title
        val content = note.noteContent
        editTitle.setText(title)
        editNotes.setText(content)
        id = note.id.toString()
        (activity as AppCompatActivity)!!.supportActionBar?.hide()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        backToHomeNoteActivity()
        saveButton(id)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun backToHomeNoteActivity() {
        backButton.setOnClickListener {
            startActivity(Intent(context, NoteHomeActivity::class.java))
        }
    }

    private fun saveButton(Id: String) {
        saveFab.setOnClickListener {
            val newTitle = editTitle.text.toString()
            val newContent = editNotes.text.toString()
            note.title = newTitle
            note.noteContent = newContent
            val noteServices = NoteServices()
            val context = requireContext()
            val simpleDateFormat = SimpleDateFormat("dd MMMM,hh.mm a", Locale.getDefault())
            val currentDateandTime: String = simpleDateFormat.format(Date())
            noteServices.updateNote(
                Id,
                newTitle,
                newContent,
                currentDateandTime,
                context,
                archive = false,
                adapter = NoteAdapter(arrayListOf(NoteData()), context)
            )
            startActivity(Intent(context, NoteHomeActivity()::class.java))
        }
    }



}