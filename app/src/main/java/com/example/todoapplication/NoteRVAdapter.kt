package com.example.todoapplication

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.todoapplication.R.color.green
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NoteRVAdapter(private val noteList: ArrayList<Note>) : Adapter<NoteRVAdapter.MyViewHolder>() {
    private lateinit var database: DatabaseReference

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val noteTitle: TextView = itemView.findViewById(R.id.idTVNoteTitle)
        val noteTimeStamp: TextView = itemView.findViewById(R.id.idTVTimeStamp)
        val delete: ImageView = itemView.findViewById(R.id.idIVDelete)
        val done: ImageView = itemView.findViewById(R.id.idIVDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.note_rv_item,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    @SuppressLint("ResourceAsColor", "SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = noteList[position]
        holder.noteTitle.text = currentItem.title
        holder.noteTimeStamp.text = currentItem.timeStamp
        database = FirebaseDatabase.getInstance().getReference("Notes")

        holder.delete.setOnClickListener {

            val title = holder.noteTitle.text.toString()
            database.child(title).removeValue().addOnSuccessListener {
                Log.i("ArrayList", "Deleted Successfully")

            }.addOnFailureListener {
                Log.i("ArrayList", "Failed")
            }
        }


        Log.i("ArrayList", noteList[position].toString() + " Adapter")
        val isDone = noteList[position].Completed
        Log.i("ArrayList", isDone.toString())

        holder.done.setOnClickListener {
            Log.i("ArrayList", "$isDone OnClickListener")
            val titleNote = noteList[position].title
            if (titleNote != null) {
                if (isDone != null) {

                    database.child(titleNote).child("Completed").setValue("yes")

                }
            }

        }
        if (isDone == "yes") {
            holder.cardView.setBackgroundResource(green)
            holder.done.setImageResource(R.drawable.donetask)
        }


    }

    override fun getItemCount(): Int {
        return noteList.size
    }
}

