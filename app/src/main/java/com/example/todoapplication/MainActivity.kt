package com.example.todoapplication

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {

    private lateinit var dbref: DatabaseReference
    private lateinit var notesRV: RecyclerView
    private lateinit var notesArrayList: ArrayList<Note>
    private lateinit var addFAB: FloatingActionButton
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        drawerLayout = findViewById(R.id.drawerLayout)
        navView=findViewById(R.id.navView)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout,R.string.nav_open,R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
//        supportActionBar?.setCustomView(R.layout.abs)



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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
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


                    notesArrayList.sortBy {
                        it.Completed
                    }
                    val adapter = NoteRVAdapter(notesArrayList)
                    notesRV.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
