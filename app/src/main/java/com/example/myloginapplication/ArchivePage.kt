package com.example.myloginapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
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
    private lateinit var retrievedList: ArrayList<NoteData>
    private lateinit var changeViewOfNotes: ImageButton
    private val noteService = NoteServices()
    private var flag by Delegates.notNull<Int>()
    private lateinit var fl: FrameLayout
    private var currentPage = 0
    private val page = Paginator()
    private lateinit var previousBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private var totalPages by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_archive_page, container, false)

        recyclerView = view.findViewById(R.id.recycler_View)
        changeViewOfNotes = activity?.findViewById(R.id.changeView)!!
        layoutAsGrid = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutAsList = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        noteArrayList = arrayListOf()
        noteAdapter = NoteAdapter(noteArrayList, requireContext())
        val context = requireContext()
        val passFlag = 1
        noteService.retrieveNote(noteArrayList, context, passFlag)
        flag = 0
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Archived"
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayUseLogoEnabled(true)
        fl = view.findViewById(R.id.archivePage)
        previousBtn = view.findViewById(R.id.previousButton)
        nextBtn = view.findViewById(R.id.nextButton)
        retrievedList = noteArrayList
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onClickNextButton()
        onClickPreviousButton()
        changeViewButton()
        viewNote()
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
            NoteAdapter(page.generatePage(noteArrayList, currentPage), requireContext())
    }

    private fun onClickNextButton() {
        nextBtn.setOnClickListener() {
            currentPage += 1
            toggleButton(currentPage)
            recyclerView.adapter =
                NoteAdapter(page.generatePage(noteArrayList, currentPage), requireContext())
        }
    }

    private fun onClickPreviousButton() {
        previousBtn.setOnClickListener() {
            currentPage -= 1
            toggleButton(currentPage)
            recyclerView.adapter =
                NoteAdapter(page.generatePage(noteArrayList, currentPage), requireContext())
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
        if(currentPage == 0 && currentPage == totalPages){
            previousBtn.isEnabled = false
            nextBtn.isEnabled = false
        }
        else if (currentPage == 0) {
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

    @SuppressLint("NotifyDataSetChanged")
    private fun viewNote() {
        recyclerView.layoutManager = layoutAsGrid
        recyclerView.adapter = NoteAdapter(retrievedList, requireContext())
    }

}