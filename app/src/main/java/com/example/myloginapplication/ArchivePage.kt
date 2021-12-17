package com.example.myloginapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlin.properties.Delegates


class ArchivePage : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutAsGrid: StaggeredGridLayoutManager
    private lateinit var layoutAsList: StaggeredGridLayoutManager
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteArrayList: ArrayList<NoteData>
    private lateinit var changeViewOfNotes: ImageButton
    private val noteService = NoteServices()
    private var flag by Delegates.notNull<Int>()
    private lateinit var fl: FrameLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archive_page, container, false)

        recyclerView = view.findViewById(R.id.recycler_View)
        changeViewOfNotes = activity?.findViewById(R.id.changeView)!!
        layoutAsGrid = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutAsList = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutAsGrid
        noteArrayList = arrayListOf()
        noteAdapter = NoteAdapter(noteArrayList, requireContext())
        recyclerView.adapter = noteAdapter
        flag = 1
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Archived"
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayUseLogoEnabled(true)
        fl = view.findViewById(R.id.archivePage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        changeViewButton()
        viewNoteDetails()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun changeViewButton() {
        changeViewOfNotes.setOnClickListener {
            changeView()
        }
    }

    private fun changeView() {
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

    private fun viewNoteDetails() {
        val context = requireContext()
        val flag = 1
        noteService.retrieveNote(noteArrayList, context, noteAdapter, flag)
    }

}