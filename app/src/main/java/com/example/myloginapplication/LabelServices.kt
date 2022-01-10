package com.example.myloginapplication


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*


class LabelServices {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var documentLabelReference: CollectionReference
    private lateinit var documentNoteReference: CollectionReference

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        documentLabelReference =
            firestore.collection("Users").document(firebaseUser.uid).collection("Label")
        documentNoteReference =
            firestore.collection(USER_COLLECTION).document(firebaseUser.uid).collection(
                NOTE_COLLECTION
            )
    }

    fun addLabelCollection(label: String) {
        val labelNote = mutableMapOf<String, String>()
        labelNote["label"] = label
        documentLabelReference.document().set(labelNote)
    }

    fun retrieveLabelCollection(
        context: Context,
        expandableListView: ExpandableListView
    ) {
        val labelData: ArrayList<LabelData> = arrayListOf()
        val labelList: MutableList<String> = mutableListOf()
        documentLabelReference.addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Toast.makeText(
                        context,
                        "Cannot Retrieve Data ${error.message.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(ContentValues.TAG, error.localizedMessage!!.toString());
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val data = dc.document.toObject(LabelData::class.java)
                        data.id = dc.document.id
                        labelData.add(data)
                        labelList.add(dc.document.getString("label").toString())
                    }
                }
                val labelName: MutableList<String> = mutableListOf()
                val labelHashMap: HashMap<String, MutableList<String>> = hashMapOf()
                labelName.add("Label")
                labelHashMap[labelName[0]] = labelList
                val expandListAdapter =
                    LabelExpandAdapter(
                        context,
                        labelName,
                        labelHashMap,
                        expandableListView,
                        labelData
                    )
                expandableListView.setAdapter(expandListAdapter)
            }
        })
    }

    fun updateLabelCollection(id: String, value: String) {
        documentLabelReference.document(id).update("label", value)
    }

    fun deleteLabelCollection(id: String) {
        documentLabelReference.document(id).delete()
    }


    fun retrieveLabel(
        context: Context,
        recyclerView: RecyclerView,
        addButton: Button,
        noteData: NoteData,
        adapter: NoteAdapter
    ) {
        val labelList: ArrayList<LabelRecyclerData> = arrayListOf()
        documentLabelReference.addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null) {
                    Toast.makeText(
                        context,
                        "Cannot Retrieve Data ${error.message.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(ContentValues.TAG, error.localizedMessage!!.toString());
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val data = dc.document.toObject(LabelRecyclerData::class.java)
                        data.id = dc.document.id
                        labelList.add(data)
                    }
                }
                val labelAdapter = LabelRecyclerAdapter(context, labelList, addButton, noteData)
                recyclerView.adapter = labelAdapter
                adapter.notifyDataSetChanged()
            }
        })
    }

    fun addLabelCollectionInNote(label: ArrayList<String>, id: String, context: Context) {
        val labelNote = mutableMapOf<String, ArrayList<String>>()
        labelNote["label"] = label
        Log.d("label", "$label")
        documentNoteReference.document(id).update(labelNote as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(context, "Label added successfully", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
            Toast.makeText(context, "Failed to add Label", Toast.LENGTH_SHORT)
                .show()
        }
    }

    companion object {
        private const val USER_COLLECTION = "Users"
        private const val NOTE_COLLECTION = "Notes"
    }
}