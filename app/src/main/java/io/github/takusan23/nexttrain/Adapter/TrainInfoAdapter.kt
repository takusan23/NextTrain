package io.github.takusan23.nexttrain.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.github.takusan23.nexttrain.R

class TrainInfoAdapter(var list: ArrayList<ArrayList<String>>) :
    RecyclerView.Adapter<TrainInfoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView = itemView.findViewById<TextView>(R.id.adapter_train_info_name)
        var descTextView = itemView.findViewById<TextView>(R.id.adapter_train_info_desc)
        var cardView = itemView.findViewById<View>(R.id.adapter_train_info_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_train_info, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.nameTextView.text = item[1]
        holder.descTextView.text = item[2]

        holder.cardView.setOnClickListener {
            //押したとき

        }

    }

}