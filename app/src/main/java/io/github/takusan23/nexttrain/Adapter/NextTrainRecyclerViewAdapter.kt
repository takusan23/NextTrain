package io.github.takusan23.nexttrain.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.github.takusan23.nexttrain.R

class NextTrainRecyclerViewAdapter(var list: ArrayList<ArrayList<String>>) :
    RecyclerView.Adapter<NextTrainRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_next_train_layout, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //入れる
        val item = list[position]
        //
        val name = item[1]
        val upTime = item[2]
        val downTime = item[3]

        //駅名
        holder.nameTextView.text = name
        holder.timeTextView.text = upTime

        //切り替える
        holder.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                //切り替え
                when (tab?.text ?: "上り") {
                    "上り" -> {
                        holder.timeTextView.text = upTime
                    }
                    "下り" -> {
                        holder.timeTextView.text = downTime
                    }
                }
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView =
            itemView.findViewById<TextView>(R.id.adapter_next_train_station_name_textview)
        var timeTextView = itemView.findViewById<TextView>(R.id.adapter_next_train_time_textview)
        var tabLayout = itemView.findViewById<TabLayout>(R.id.adapter_next_train_tab_layout)
    }

}
