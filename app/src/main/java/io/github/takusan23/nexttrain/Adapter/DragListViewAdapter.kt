package io.github.takusan23.nexttrain.Adapter

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.woxthebox.draglistview.DragItemAdapter
import com.woxthebox.draglistview.DragListView
import io.github.takusan23.nexttrain.DataBase.StationSQLiteHelper
import io.github.takusan23.nexttrain.Fragment.StationListFragment
import io.github.takusan23.nexttrain.R
import kotlinx.android.synthetic.main.adapter_draglayout.view.*

internal class DragListViewAdapter(
    val list: ArrayList<androidx.core.util.Pair<Long, String>>,
    private val mLayoutId: Int,
    private val mGrabHandleId: Int,
    private val mDragOnLongPress: Boolean,
    private val activity: Activity
) : DragItemAdapter<androidx.core.util.Pair<Long, String>, DragListViewAdapter.ViewHolder>() {

    //でーたべーす
    lateinit var helper: StationSQLiteHelper
    lateinit var db: SQLiteDatabase

    init {
        itemList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_draglayout, parent, false)
        return ViewHolder(view)
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position].first!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val context = holder.nameTextView.context

        //名前入れる
        val text = mItemList[position].second
        holder.nameTextView.text = text
        holder.itemView.tag = mItemList[position]

        //削除機能
        holder.deleteButton.setOnClickListener {
            //データベース用意
            if (!this@DragListViewAdapter::helper.isInitialized) {
                helper = StationSQLiteHelper(context!!)
                db = helper.writableDatabase
                db.disableWriteAheadLogging()
            }
            //ほんとに消す？
            Snackbar.make(holder.nameTextView, "本当に削除しますか？", Snackbar.LENGTH_SHORT)
                .setAction("削除") {
                    db.delete("station_db", "name=?", arrayOf(text))
                    Toast.makeText(context, "削除したよ", Toast.LENGTH_SHORT).show()
                    //リスト更新
                    if (activity is AppCompatActivity) {
                        val fragment =
                            activity.supportFragmentManager?.findFragmentById(R.id.main_fragment)
                        if (fragment is StationListFragment) {
                            fragment.loadDB()
                        }
                    }
                }.show()
        }

    }

    internal inner class ViewHolder(itemView: View) :
        DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {

        var nameTextView: TextView
        var deleteButton: ImageView

        init {
            nameTextView = itemView.findViewById<View>(R.id.adapter_drag_name_textview) as TextView
            deleteButton = itemView.findViewById<View>(R.id.adapter_drag_delete_button) as ImageView
        }

        override fun onItemClicked(view: View?) {
        }

        override fun onItemLongClicked(view: View?): Boolean {
            return true
        }
    }


}
