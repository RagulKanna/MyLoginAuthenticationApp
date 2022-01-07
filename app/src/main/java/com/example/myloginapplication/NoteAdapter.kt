package com.example.myloginapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList


class NoteAdapter(
    private var noteList: ArrayList<NoteData>,
    private val context: Context
) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), Filterable {

    var noteFilterList = ArrayList<NoteData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.notes_layout, parent, false)

        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val db = DatabaseHandler(context)
        val note: NoteData = noteList[position]
        val titleValue = note.title
        val noteContentValue = note.noteContent
        val time = note.dateTime
        val id = note.id
        val archive = note.archive
        if (titleValue != null && noteContentValue != null && time != null && id != null && archive != null) {
                setValuesToViewAndDataBase(holder, note, db)
            onClickImageButton(
                note,
                holder,
                context,
                position
            )
        }
        onClickCard(note, holder)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                noteFilterList = if (charSearch.isEmpty()) {
                    noteList
                } else {
                    val resultList = ArrayList<NoteData>()
                    for (row in noteList) {
                        if (row.title?.lowercase(Locale.ROOT)
                                ?.contains(charSearch.lowercase(Locale.ROOT)) == true
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = noteFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                noteFilterList = results?.values as ArrayList<NoteData>
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    private fun onClickCard(note: NoteData, holder: NoteViewHolder) {
        holder.noteCard.setOnClickListener { v ->
            val check = note.archive!!
            if (check == "false") {
                val bundle = Bundle()
                val changeFragment = R.id.notesPage
                bundle.putSerializable("noteObject", note)
                val fragment = EditPage()
                fragment.arguments = bundle
                val activity = v!!.context as AppCompatActivity
                val fl = activity.findViewById<FrameLayout>(changeFragment)
                fl.removeAllViews()
                activity.supportFragmentManager.beginTransaction()
                    .add(changeFragment, fragment).commit()
            } else {
                Toast.makeText(
                    v!!.context,
                    "Edit cannot be done on archive",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setValuesToViewAndDataBase(
        holder: NoteViewHolder,
        note: NoteData,
        db: DatabaseHandler
    ) {
        val content = SpannableString(note.title)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        holder.titleTextView.text = content
        holder.noteTextView.text = note.noteContent
        holder.timeView.text = note.dateTime
        var labelString: String? = null
        if (note.label == null) {
            holder.labelText.isVisible = false
            holder.labelView.isVisible = false
        } else {
            holder.labelText.isVisible = true
            holder.labelView.isVisible = true
            labelString = note.label!!.joinToString(separator = ",")
            holder.labelView.text = labelString
        }
        db.saveData(note)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onClickImageButton(
        note: NoteData,
        holder: NoteViewHolder,
        context: Context,
        position: Int
    ) {
        holder.buttonInCard.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.buttonInCard)
            popupMenu.menuInflater.inflate(R.menu.note_menu, popupMenu.menu)
            val check = note.archive!!
            if (check == "false") {
                popupMenu.menu.findItem(R.id.archive).title = "Archive"
            } else {
                popupMenu.menu.findItem(R.id.archive).title = "Unarchive"
            }
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete -> {
                        noteList.removeAt(position)
                        val bundle = Bundle()
                        bundle.putString("id", note.id)
                        val fragment = DeleteBox()
                        fragment.arguments = bundle
                        val activity = it!!.context as AppCompatActivity
                        if (check == "true") {
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.archivePage, fragment).commit()
                        } else {
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.notesPage, fragment).commit()
                        }
                    }
                    R.id.archive -> {
                        noteList.removeAt(position)
                        val noteServices = NoteServices()
                        if (check == "false") {
                            val archive = true
                            noteServices.updateNote(
                                note.id!!,
                                note.title!!,
                                note.noteContent!!,
                                note.dateTime!!,
                                context,
                                archive,
                                adapter = NoteAdapter(noteList, context)
                            )
                            notifyDataSetChanged()
                            Toast.makeText(
                                it.context,
                                "Archived",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val archive = false
                            noteServices.updateNote(
                                note.id!!,
                                note.title!!,
                                note.noteContent!!,
                                note.dateTime!!,
                                context,
                                archive,
                                adapter = NoteAdapter(noteList, context)
                            )
                            notifyDataSetChanged()
                            Toast.makeText(
                                it.context,
                                "Unarchived",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                true
            })
            popupMenu.show()
        }
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteCard: CardView = itemView.findViewById(R.id.noteCard)
        val titleTextView: TextView = itemView.findViewById(R.id.noteTitle)
        val noteTextView: TextView = itemView.findViewById(R.id.noteContent)
        val buttonInCard: ImageButton = itemView.findViewById(R.id.btnNoteMenu)
        val timeView: TextView = itemView.findViewById(R.id.timeStamp)
        val labelView: TextView = itemView.findViewById(R.id.labelTitle)
        val labelText: TextView = itemView.findViewById(R.id.labelTextId)
    }
}



