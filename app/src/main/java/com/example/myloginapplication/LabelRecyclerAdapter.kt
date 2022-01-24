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

    class LabelCheck(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val labelCheck: CheckBox = itemView.findViewById(R.id.checkLabelText)
        val labelName: TextView = itemView.findViewById(R.id.labeltext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelCheck {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.check_label, parent, false)
        return LabelCheck(itemView)
    }

    override fun onBindViewHolder(holder: LabelCheck, position: Int) {
        val addLabel: ArrayList<String> = arrayListOf()
        val labelData: LabelRecyclerData = labelList[position]
        holder.labelName.text = labelData.label
        addButton.setOnClickListener {
            if (holder.labelCheck.isChecked) {
                addLabel.add(labelData.label.toString())
                Toast.makeText(context, labelData.label.toString(), Toast.LENGTH_SHORT).show()
            }
            val labelService = LabelServices()
            if (addLabel.size > 0) {
                labelService.addLabelCollectionInNote(addLabel, noteData.id!!, context)
                addButton.isEnabled = false
            }else{
                addLabel.clear()
                labelService.addLabelCollectionInNote(addLabel, noteData.id!!, context)
            }
        }
    }

    override fun getItemCount(): Int {
        return labelList.size
    }

}