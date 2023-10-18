package com.example.flaptrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BirdAdapter(private val birdList:ArrayList<BirdieInfo> ):RecyclerView.Adapter<BirdAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tvName:TextView = itemView.findViewById(R.id.tvName)
        val tvSpecies:TextView = itemView.findViewById(R.id.tvSpecies)
        val tvDate:TextView = itemView.findViewById(R.id.tvDate)
        val tvLocation:TextView = itemView.findViewById(R.id.tvLocation)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_bird_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = birdList[position].birdName
        holder.tvSpecies.text = birdList[position].birdSpecies
        holder.tvDate.text = birdList[position].date
        holder.tvLocation.text = birdList[position].location

    }
    override fun getItemCount(): Int {

        return birdList.size
    }


}

//-------------------------------------ooo000EndOfFile000ooo----------------------------------------