package com.example.myloginapplication

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.*
import com.google.firebase.storage.StorageReference

class NoteServices {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference

    init {
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        collectionReference =
            firestore.collection(USER_COLLECTION).document(firebaseUser.uid).collection(
                NOTE_COLLECTION)

    }

    fun addNote(
        title: String,
        noteContent: String,
        timeStamp: String,
        archive: Boolean,
        context: Context
    ) {
        val note = mutableMapOf<String, String>()
        note["title"] = title
        note["noteContent"] = noteContent
        note["dateTime"] = timeStamp
        note["archive"] = archive.toString()
        collectionReference.document().set(note).addOnSuccessListener {
            Toast.makeText(context, "Note created successfully", Toast.LENGTH_SHORT)
                .show()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to create Note", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun retrieveNote(
        noteArrayList: ArrayList<NoteData>,
        context: Context,
        flag: Int,
        recyclerView: RecyclerView,
        page: Paginator,
        currentPage: Int
    ) {
        if (flag == 2) {
            retrieveUnarchivedQuery(
                context,
                noteArrayList,
                recyclerView,
                page,
                currentPage
            )
        } else if (flag == 1) {
            retrieveArchivedQuery(
                context,
                noteArrayList,
                recyclerView,
                page,
                currentPage
            )
        }
    }

    private fun retrieveArchivedQuery(
        context: Context,
        noteArrayList: ArrayList<NoteData>,
        recyclerView: RecyclerView,
        page: Paginator,
        currentPage: Int
    ) {
        val documentRef =
            collectionReference
                .orderBy("title", Query.Direction.ASCENDING)
                .whereEqualTo("archive", "true")
        documentRef.addSnapshotListener(object : EventListener<QuerySnapshot> {
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
                val adapter = NoteAdapter(
                    page.generatePage(
                        noteArrayList,
                        currentPage,
                        context
                    ), context
                )
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun retrieveUnarchivedQuery(
        context: Context,
        noteArrayList: ArrayList<NoteData>,
        recyclerView: RecyclerView,
        page: Paginator,
        currentPage: Int
    ) {
        val documentRef =
            collectionReference
                .orderBy("title", Query.Direction.ASCENDING)
                .whereEqualTo("archive", "false")
        documentRef.addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
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
                Log.d("check", "${noteArrayList.size}")
                val adapter = NoteAdapter(
                    page.generatePage(
                        noteArrayList,
                        currentPage,
                        context
                    ), context
                )
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteNote(context: Context, noteAdapter: NoteAdapter, Id: String) {
        collectionReference.document(Id)
            .delete().addOnSuccessListener {
                val db = DatabaseHandler(context)
                db.deleteData(Id)
                Toast.makeText(context, "This note is deleted", Toast.LENGTH_SHORT)
                    .show()
            }.addOnFailureListener {
                Toast.makeText(
                    context,
                    "note deletion is failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        noteAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateNote(
        Id: String,
        newTitle: String,
        newContent: String,
        timeStamp: String,
        context: Context,
        archive: Boolean,
        adapter: NoteAdapter
    ) {
        val docRef = collectionReference
            .document(Id)
        val db = DatabaseHandler(context)
        val note = mutableMapOf<String, String>()
        note["title"] = newTitle
        note["noteContent"] = newContent
        note["dateTime"] = timeStamp
        note["archive"] = archive.toString()
        docRef.update(note as Map<String, Any>).addOnSuccessListener {
            db.updateData(Id, newTitle, newContent, timeStamp)
            adapter.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to update Note", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun retrieveImage(
        context: Context,
        fileRef: StorageReference,
        profilePhoto: ShapeableImageView,
    ) {
        fileRef.child("image").downloadUrl.addOnSuccessListener(object :
            OnSuccessListener<Uri> {
            override fun onSuccess(uri: Uri?) {
                Glide.with(context).load(uri).into(profilePhoto)
            }
        }).addOnFailureListener {
            Toast.makeText(context, "Image Error", Toast.LENGTH_SHORT).show()

        }
    }

    fun deleteFiles(fileReference: StorageReference) {
        fileReference.delete()
    }

    fun uploadFile(
        context: Context,
        fileReference: StorageReference,
        imageUri: Uri,
        databaseRef: DatabaseReference,
        progressBar: ProgressBar
    ) {
        fileReference.putFile(imageUri).addOnSuccessListener {
            Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_SHORT)
                .show()
            val uploadId = databaseRef.push().key
            if (uploadId != null) {
                databaseRef.child(uploadId)
                progressBar.visibility = View.INVISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val USER_COLLECTION = "Users"
        private const val NOTE_COLLECTION = "Notes"

    }
}


