package com.example.voteme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso

class MyAdapter2(
    private val userList: ArrayList<User2>,
    private val itemClickListener: ItemClickListener2,
    private val context: Context
) :
    RecyclerView.Adapter<MyAdapter2.MyViewHolder>(){


    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val Image : ImageView = itemView.findViewById(R.id.profilePic)
        val Name : TextView = itemView.findViewById(R.id.name)
        val Gender : TextView = itemView.findViewById(R.id.gender)
        val Position : TextView = itemView.findViewById(R.id.position)
        val Party : TextView = itemView.findViewById(R.id.party)
        val voteCount : TextView = itemView.findViewById(R.id.votesCount)
        val vote : TextView = itemView.findViewById(R.id.voteBtn)
//        val delete : ImageView = itemView.findViewById(R.id.delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item2, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val Image = userList[position]
//        val Image = userList[position]
//        holder.Image.toString()[position]
//        holder.Image.setImageResource(Image.image!!)

        val imageTarget = Image.image
        Picasso.get().load(imageTarget).into(holder.Image)
        holder.Name.text = userList[position].name
        holder.Gender.text = userList[position].gender
        holder.Position.text = userList[position].position
        holder.Party.text = userList[position].party
        holder.voteCount.text = userList[position].votes
        holder.vote.setOnClickListener {
            itemClickListener.vote(userList[position], position)
        }

//        holder.delete.setImageResource(deleteImage.delete)

//        holder.delete.setOnClickListener {
//            onItemClick?.invoke(deleteImage)
//        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}