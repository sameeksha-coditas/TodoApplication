package com.example.todoapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var dbref: DatabaseReference
    private lateinit var notesRV: RecyclerView
    private lateinit var notesArrayList: ArrayList<Note>
    private lateinit var addFAB: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        getSupportActionBar()?.setCustomView(R.layout.abs)

        notesRV = findViewById(R.id.idRVNotes)
        notesRV.layoutManager = LinearLayoutManager(this)
        notesRV.setHasFixedSize(true)

        notesArrayList = arrayListOf()
        dbref = FirebaseDatabase.getInstance().getReference(Static.notes)
        getNotesData()

        addFAB = findViewById(R.id.idFABAddNote)
        addFAB.setOnClickListener {
            val intent = Intent(this, AddEditNoteActivity::class.java)
            startActivity(intent)
        }

        notesRV.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {

                val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
                intent.putExtra(Static.noteType, "Edit")
                intent.putExtra(Static.noteTitle, notesArrayList[position].title)
                intent.putExtra(Static.noteDescription, notesArrayList[position].description)
                startActivity(intent)
            }
        })
    }


    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    private fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }

    private fun getNotesData() {
        dbref = FirebaseDatabase.getInstance().getReference(Static.notes)

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    notesArrayList.clear()
                    for (noteSnapshot in snapshot.children) {

                        val note = noteSnapshot.getValue(Note::class.java)
                        Log.i("ArrayList", note.toString())
//                        if(notesArrayList.contains(note))
//                        {
//                            continue
//                        }
//                        else
//                        {
                        notesArrayList.add(note!!)
//                        }
                    }
                    Log.i("ArrayList", notesArrayList.toString())
                    Log.i(
                        "ArrayList",
                        "SIZE=" + notesArrayList.size.toString() + " three In getNotes() after for loop"
                    )
                    val adapter = NoteRVAdapter(notesArrayList)
                    notesRV.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
