package io.github.takusan23.nexttrain.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.github.takusan23.nexttrain.Fragment.NextTrainFragment
import io.github.takusan23.nexttrain.R
import io.github.takusan23.nexttrain.TrainInfoActivity

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
        val up = item[1]
        val down = item[2]
        val name = item[3]
        val upTime = item[4]
        val downTime = item[5]
        val upTrainType = item[6]
        val downTrainType = item[7]
        val upTrainFor = item[8]
        val downTrainFor = item[9]

        val context = holder.cardView.context

        //駅名
        holder.nameTextView.text = name
        holder.timeTextView.text = upTime
        holder.typeForTextView.text = "$upTrainType $upTrainFor"

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
                        holder.typeForTextView.text = "$upTrainType $upTrainFor"
                    }
                    "下り" -> {
                        holder.timeTextView.text = downTime
                        holder.typeForTextView.text = "$downTrainType $downTrainFor"
                    }
                }
            }
        })

        holder.cardView.setOnClickListener {
            //Card押したとき
            if (context is AppCompatActivity) {
                val fragment =
                    (context as AppCompatActivity).supportFragmentManager.findFragmentById(R.id.main_fragment)
                if (fragment is NextTrainFragment) {
                    //アニメーション付きで画面切り替え
                    val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context,
                        holder.cardView,
                        "card"
                    )
                    val intent = Intent(context, TrainInfoActivity::class.java)
                    intent.putExtra("up", up)
                    intent.putExtra("down", down)
                    context.startActivity(intent, compat.toBundle())
                }
            }
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTextView =
            itemView.findViewById<TextView>(R.id.adapter_next_train_station_name_textview)
        var timeTextView = itemView.findViewById<TextView>(R.id.adapter_next_train_time_textview)
        var typeForTextView =
            itemView.findViewById<TextView>(R.id.adapter_next_train_type_for_textview)
        var tabLayout = itemView.findViewById<TabLayout>(R.id.adapter_next_train_tab_layout)
        var cardView = itemView.findViewById<CardView>(R.id.adapter_next_train_station_cardview)

    }

}
