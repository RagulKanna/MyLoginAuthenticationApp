package com.example.myloginapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.type.Date
import java.text.SimpleDateFormat
import java.util.*


class NewNotePage : Fragment() {

    private lateinit var createNewTitle: EditText
    private lateinit var createNewNotes: EditText
    private lateinit var saveFab: FloatingActionButton
    private lateinit var backButton: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_new_note_page, container, false)

        saveFab = view.findViewById(R.id.save)
        createNewTitle = view.findViewById(R.id.createTitle)
        createNewNotes = view.findViewById(R.id.createNote)
        backButton = view.findViewById(R.id.backButton)
        (activity as AppCompatActivity?)!!.supportActionBar?.hide()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        saveDataToFireStore()
        backToHomeNoteActivity()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun backToHomeNoteActivity() {
        backButton.setOnClickListener {
            startActivity(Intent(context, NoteHomeActivity::class.java))
        }
    }

    private fun saveDataToFireStore() {
        saveFab.setOnClickListener() {
            val noteServices = NoteServices()
            var title = createNewTitle.text.toString()
            var noteContent = createNewNotes.text.toString()
            val archive: Boolean = false
            if (title.isEmpty() || noteContent.isEmpty()) {
                Toast.makeText(context, "Title and Notes must be entered", Toast.LENGTH_SHORT)
                    .show()
            } else {
                var context = requireContext()
                val sdf = SimpleDateFormat("dd MMMM,hh.mm a", Locale.getDefault())
                val currentDateandTime: String = sdf.format(Date())
                noteServices.addNote(title, noteContent, currentDateandTime, archive, context)
                startActivity(Intent(activity, NoteHomeActivity::class.java))
            }
        }
    }
}