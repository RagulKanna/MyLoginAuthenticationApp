package com.example.myloginapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView

class LabelAdapter(
    private var labelList: ArrayList<LabelData>,
    private val context: Context
) : RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_label, parent, false)
        return LabelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        val label: LabelData = labelList[position]
        val labelName = label.labelName
        if(labelName != null){
            assignValues(holder,label)
        }
    }

    private fun assignValues(holder: LabelViewHolder,label: LabelData) {
      // holder.selectLabelName.text = label.labelName
    }

    override fun getItemCount(): Int {
        return labelList.size
    }

    class LabelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}