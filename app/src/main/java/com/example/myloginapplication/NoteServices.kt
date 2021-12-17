package com.example.myloginapplication

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*


class NoteServices {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
    }

    fun addNote(
        title: String,
        noteContent: String,
        timeStamp: String,
        archive: Boolean,
        context: Context
    ) {
        var documentReference =
            firestore.collection("Users").document(firebaseUser.uid).collection("Notes").document()
        var note = mutableMapOf<String, String>()
        note["title"] = title
        note["noteContent"] = noteContent
        note["dateTime"] = timeStamp
        note["archive"] = archive.toString()
        documentReference.set(note).addOnSuccessListener {
            Toast.makeText(context, "Note created successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to create Note", Toast.LENGTH_SHORT).show()
        }
    }

    fun retrieveNote(
        noteArrayList: ArrayList<NoteData>,
        context: Context,
        myAdapter: NoteAdapter,
        flag: Int
    ) {
        if (flag == 2) {
            retrieveUnarchivedQuery(context, noteArrayList, myAdapter)
        } else if (flag == 1) {
            retrieveArchivedQuery(context, noteArrayList, myAdapter)
        }
    }

    private fun retrieveArchivedQuery(
        context: Context,
        noteArrayList: ArrayList<NoteData>,
        myAdapter: NoteAdapter
    ) {
        val documentRef =
            firestore.collection("Users").document(firebaseUser.uid).collection("Notes")
                .orderBy("title", Query.Direction.ASCENDING)
                .whereEqualTo("archive", "true")
        documentRef.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Toast.makeText(
                        context,
                        "Cannot Retrieve Data ${error.message.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e(TAG, error.localizedMessage!!.toString());
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val data = dc.document.toObject(NoteData::class.java)
                        data.id = dc.document.id
                        noteArrayList.add(data)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun retrieveUnarchivedQuery(
        context: Context,
        noteArrayList: ArrayList<NoteData>,
        myAdapter: NoteAdapter
    ) {
        val documentRef =
            firestore.collection("Users").document(firebaseUser.uid).collection("Notes")
                .orderBy("title", Query.Direction.ASCENDING)
                .whereEqualTo("archive", "false")
        documentRef.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Toast.makeText(
                        context,
                        "Cannot Retrieve Data ${error.message.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val data = dc.document.toObject(NoteData::class.java)
                        data.id = dc.document.id
                        noteArrayList.add(data)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }

    fun deleteNote(context: Context, noteAdapter: NoteAdapter, Id: String) {
        firestore.collection("Users").document(firebaseUser.uid).collection("Notes").document(Id)
            .delete().addOnSuccessListener {
                val db = DatabaseHandler(context)
                db.deleteData(Id)
                Toast.makeText(context, "This note is deleted", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "note deletion is failed", Toast.LENGTH_SHORT).show()
            }
        noteAdapter.notifyDataSetChanged()
    }

    fun updateNote(
        Id: String,
        newTitle: String,
        newContent: String,
        timeStamp: String,
        context: Context,
        archive: Boolean
    ) {
        var docRef = firestore.collection("Users").document(firebaseUser.uid).collection("Notes")
            .document(Id)
        val db = DatabaseHandler(context)
        var note = mutableMapOf<String, String>()
        note["title"] = newTitle
        note["noteContent"] = newContent
        note["dateTime"] = timeStamp
        note["archive"] = archive.toString()
        docRef.update(note as Map<String, Any>).addOnSuccessListener {
            db.updateData(Id, newTitle, newContent, timeStamp)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to update Note", Toast.LENGTH_SHORT).show()
        }
    }
}

