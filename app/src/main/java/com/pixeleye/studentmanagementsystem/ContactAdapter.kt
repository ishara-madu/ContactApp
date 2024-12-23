package com.pixeleye.studentmanagementsystem

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contacts: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.textViewName)
        val phone: TextView = itemView.findViewById(R.id.textViewPhone)
        val image: ImageView = itemView.findViewById(R.id.imageViewContact)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone

        // Set image if imagePath is valid, otherwise use a default image
        fun loadImageFromFilePath(filePath: String, imageView: ImageView) {
            val bitmap = BitmapFactory.decodeFile(filePath)
            imageView.setImageBitmap(bitmap)
        }

        if (contact.imagePath.isNotEmpty()) {
            loadImageFromFilePath(contact.imagePath,  holder.image)
        } else {
            holder.image.setImageResource(R.drawable.user)
        }
    }

    override fun getItemCount(): Int = contacts.size
}
