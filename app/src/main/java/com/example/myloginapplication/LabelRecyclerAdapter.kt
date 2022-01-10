package com.example.myloginapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class LabelRecyclerAdapter(
    private var context: Context,
    private var labelList: ArrayList<LabelRecyclerData>,
    private var addButton: Button,
    private var noteData: NoteData
) :
    RecyclerView.Adapter<LabelRecyclerAdapter.LabelCheck>() {
    private var addLabel: ArrayList<String> = arrayListOf()

    class LabelCheck(view: View) : RecyclerView.ViewHolder(view) {
        var labelCheck: CheckBox = view.findViewById(R.id.checkLabelText)
        var labelName: TextView = view.findViewById(R.id.labeltext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelCheck {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.check_label, parent, false)
        return LabelCheck(itemView)
    }

    override fun onBindViewHolder(holder: LabelCheck, position: Int) {
        val labelData: LabelRecyclerData = labelList[position]
        holder.labelName.text = labelData.label
        checkToArrayList(holder)
        addLabelToArray(addLabel)
    }

    private fun addLabelToArray(addLabel: ArrayList<String>) {
        addButton.setOnClickListener {
            addLabel.add("hai")
            addLabel.add("dear")
            Toast.makeText(context,"${addLabel.size}",Toast.LENGTH_SHORT).show()
            if (addLabel.size > 0) {
                val labelService = LabelServices()
                labelService.addLabelCollectionInNote(addLabel, noteData.id!! ,context)
                addButton.isEnabled = false
            }
        }
    }

    private fun checkToArrayList(holder: LabelCheck) {
        if (holder.labelCheck.isChecked) {
            addLabel.add(holder.labelName.text.toString())
        }
    }


    override fun getItemCount(): Int {
        return labelList.size
    }

}