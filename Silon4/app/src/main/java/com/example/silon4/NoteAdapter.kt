package com.example.silon4



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private val notes: List<TodoItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textNoteTitle)
        val dateText: TextView = itemView.findViewById(R.id.textNoteDate)
        val statusText: TextView = itemView.findViewById(R.id.textNoteStatus)
        val categoryText: TextView = itemView.findViewById(R.id.textNoteCategory)

        init {
            itemView.setOnClickListener {
                onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleText.text = "📝 ${note.title}"
        holder.dateText.text = "📅 ${note.createdDate} → ${note.dueDate}"
        holder.statusText.text = "สถานะ: ${note.status}"
        holder.categoryText.text = "หมวด: ${note.category}"
    }
}
