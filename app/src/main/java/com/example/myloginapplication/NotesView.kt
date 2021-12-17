package com.example.myloginapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.properties.Delegates


open class NotesView : Fragment() {

    private lateinit var createNewNoteFab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutAsGrid: StaggeredGridLayoutManager
    private lateinit var layoutAsList: StaggeredGridLayoutManager
    private lateinit var changeViewOfNotes: ImageButton
    private lateinit var searchNotes: SearchView
    private val noteService = NoteServices()
    private var flag by Delegates.notNull<Int>()
    private lateinit var fl: FrameLayout
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteArrayList: ArrayList<NoteData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_notes_view, container, false)

        createNewNoteFab = view.findViewById(R.id.createNewNote)
        recyclerView = view.findViewById(R.id.recycler_View)
        changeViewOfNotes = activity?.findViewById(R.id.changeView)!!
        searchNotes = activity?.findViewById(R.id.search)!!
        layoutAsGrid = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutAsList = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutAsGrid
        noteArrayList = arrayListOf()
        noteAdapter = NoteAdapter(noteArrayList, requireContext())
        recyclerView.adapter = noteAdapter
        flag = 1
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Note"
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayUseLogoEnabled(true)
        fl = view.findViewById(R.id.notesPage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        creatingNewNotes()
        changeViewButton()
        viewNoteDetails()
        onClickSearchButton()
    }

    fun onClickSearchButton() {
        searchNotes.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    noteAdapter.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    noteAdapter.filter.filter(newText)
                    val tempArr = ArrayList<NoteData>()

                    for (arr in noteArrayList) {
                        if (arr.title!!.lowercase(Locale.getDefault())
                                .contains(newText.lowercase())
                        ) {
                            tempArr.add(arr)
                        }
                    }
                    noteAdapter = NoteAdapter(tempArr, requireContext())
                    recyclerView.adapter = noteAdapter
                    noteAdapter.notifyDataSetChanged()
                    return false
                }
            })
    }

    private fun viewNoteDetails() {
        val context = requireContext()
        val passFlag = 2
        noteService.retrieveNote(noteArrayList, context, noteAdapter, passFlag)
    }

    private fun changeViewButton() {
        changeViewOfNotes.setOnClickListener {
            changeView()
        }
    }

    fun changeView() {
        if (flag == 0) {
            setView(layoutAsGrid)
            viewNoteDetails()
            flag = 1
        } else if (flag == 1) {
            setView(layoutAsList)
            viewNoteDetails()
            flag = 0
        }
    }

    private fun setView(layout: RecyclerView.LayoutManager) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layout
        noteArrayList = arrayListOf()
        noteAdapter = NoteAdapter(noteArrayList, requireContext())
        recyclerView.adapter = noteAdapter
    }

    private fun creatingNewNotes() {
        createNewNoteFab.setOnClickListener {
            val activity = it!!.context as AppCompatActivity
            fl.removeAllViews()
            activity.supportFragmentManager.beginTransaction().add(R.id.notesPage, NewNotePage())
                .commit()
        }
    }
}
