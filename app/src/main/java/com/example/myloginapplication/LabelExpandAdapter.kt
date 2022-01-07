package com.example.myloginapplication

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible

class LabelExpandAdapter(
    private var context: Context,
    private var labelName: MutableList<String>,
    private var labelList: HashMap<String, MutableList<String>>,
    private var expandableListView: ExpandableListView
) : BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return labelName.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return labelList.get(labelName.get(groupPosition))!!.size
    }

    override fun getGroup(groupPosition: Int): String {
        return labelName.get(groupPosition)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return labelList.get(labelName.get(groupPosition))!!.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.group_item, null)
        }
        val title: TextView = convertView?.findViewById(R.id.labelText)!!
        val addLabelButton: ImageButton = convertView?.findViewById(R.id.addLabel)!!
        addLabelButton.isVisible = true
        title.text = getGroup(groupPosition)
        title.setOnClickListener {
            if (expandableListView.isGroupExpanded(groupPosition))
                expandableListView.collapseGroup(groupPosition)
            else {
                expandableListView.expandGroup(groupPosition)
            }
        }
        addLabelButton.setOnClickListener {
            addLabelBox()
        }
        return convertView
    }

    private fun addLabelBox() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.add_label)
        val labelName: TextView = dialog.findViewById(R.id.labelAddFromUser)
        val addButton: ImageButton = dialog.findViewById(R.id.createLabelName)
        val closeButton: ImageButton = dialog.findViewById(R.id.closeLabelBox)
        addButton.setOnClickListener{
            val labelFromUser = labelName.text.toString()
            val labelServices = LabelServices()
            val labelRetrieve = NoteHomeActivity()
            labelServices.addLabelCollection(labelFromUser)
            labelRetrieve.showList(context)
            dialog.dismiss()
        }
        closeButton.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.inside_expand, null)
        }
        val title: TextView = convertView?.findViewById(R.id.expandLabel)!!
        title.text = getChild(groupPosition, childPosition)
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}
