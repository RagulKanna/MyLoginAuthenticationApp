package com.example.myloginapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.*
import kotlin.properties.Delegates


open class NotesView : Fragment() {

    private lateinit var createNewNoteFab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutAsGrid: GridLayoutManager
    private lateinit var layoutAsList: GridLayoutManager
    private lateinit var changeViewOfNotes: ImageButton
    private lateinit var searchNotes: SearchView
    private val noteService = NoteServices()
    private var flag by Delegates.notNull<Int>()
    private lateinit var fl: FrameLayout
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteArrayList: ArrayList<NoteData>
    private lateinit var retrievedList: ArrayList<NoteData>
    private var totalPages by Delegates.notNull<Int>()
    private var currentPage = 0
    private lateinit var previousBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private val page = Paginator()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notes_view, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Note"
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayUseLogoEnabled(true)
        fl = view.findViewById(R.id.notesPage)
        createNewNoteFab = view.findViewById(R.id.createNewNote)
        recyclerView = view.findViewById(R.id.recycler_View)
        changeViewOfNotes = activity?.findViewById(R.id.changeView)!!
        searchNotes = activity?.findViewById(R.id.search)!!
        previousBtn = view.findViewById(R.id.previousButton)
        nextBtn = view.findViewById(R.id.nextButton)
        layoutAsGrid = GridLayoutManager(context, 2)
        layoutAsList = GridLayoutManager(context, 1)
        flag = 1
        noteArrayList = arrayListOf()
        noteAdapter = NoteAdapter(noteArrayList, requireContext())
        recyclerView.layoutManager = layoutAsGrid
        noteService.retrieveNote(
            noteArrayList,
            requireContext(),
            2,
            recyclerView,
            page,
            currentPage
        )
        retrievedList = noteArrayList
            toggleButton(currentPage)
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        creatingNewNotes()
        onClickSearchButton()
        onClickNextButton()
        onClickPreviousButton()
        changeViewButton()
    }

    private fun onClickNextButton() {
        nextBtn.setOnClickListener() {
            currentPage += 1
            toggleButton(currentPage)
            recyclerView.adapter =
                NoteAdapter(
                    page.generatePage(noteArrayList, currentPage, requireContext()),
                    requireContext()
                )
        }
    }

    private fun onClickPreviousButton() {
        previousBtn.setOnClickListener() {
            currentPage -= 1
            toggleButton(currentPage)
            recyclerView.adapter =
                NoteAdapter(
                    page.generatePage(noteArrayList, currentPage, requireContext()),
                    requireContext()
                )
        }
    }

    private fun toggleButton(currentPage: Int) {
        val pages = noteArrayList.count() / Paginator.NOTES_PER_PAGE
        val remainingCheck = noteArrayList.count() % Paginator.NOTES_PER_PAGE
        if (remainingCheck == 0) {
            totalPages = pages - 1
        } else {
            totalPages = pages
        }
        if (noteArrayList.count() < 7) {
            previousBtn.isEnabled = false
            nextBtn.isEnabled = false
        } else if (currentPage == 0) {
            previousBtn.isEnabled = false
            nextBtn.isEnabled = true
        } else if (currentPage == totalPages) {
            nextBtn.isEnabled = false
            previousBtn.isEnabled = true
        } else if (currentPage in 0 until totalPages) {
            nextBtn.isEnabled = true
            previousBtn.isEnabled = true
        }
    }

    private fun onClickSearchButton() {
        searchNotes.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    noteAdapter.filter.filter(query)
                    return false
                }

                @SuppressLint("NotifyDataSetChanged")
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

    private fun changeViewButton() {
        changeViewOfNotes.setOnClickListener {
            changeView()
        }
    }

    private fun changeView() {
        if (flag == 0) {
            setView(layoutAsGrid)
            changeViewOfNotes.setImageResource(R.drawable.ic_baseline_align_horizontal_left_24)
            flag = 1
        } else if (flag == 1) {
            setView(layoutAsList)
            changeViewOfNotes.setImageResource(R.drawable.ic_baseline_horizontal_split_24)
            flag = 0
        }
    }

    private fun setView(layout: RecyclerView.LayoutManager) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layout
        recyclerView.adapter =
            NoteAdapter(
                page.generatePage(noteArrayList, currentPage, requireContext()),
                requireContext()
            )
    }

    private fun creatingNewNotes() {
        createNewNoteFab.setOnClickListener {
            val activity = it!!.context as AppCompatActivity
            fl.removeAllViews()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.drawerLayout, NewNotePage())
                .commit()
        }
    }
}

