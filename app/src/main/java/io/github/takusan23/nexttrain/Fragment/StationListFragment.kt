package io.github.takusan23.nexttrain.Fragment


import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.woxthebox.draglistview.DragListView
import io.github.takusan23.nexttrain.Adapter.DragListViewAdapter
import io.github.takusan23.nexttrain.DataBase.StationSQLiteHelper
import io.github.takusan23.nexttrain.MainActivity

import io.github.takusan23.nexttrain.R
import kotlinx.android.synthetic.main.fragment_station_list.*
import okhttp3.internal.notifyAll

/**
 * A simple [Fragment] subclass.
 */
class StationListFragment : Fragment() {

    //でーたべーす
    lateinit var helper: StationSQLiteHelper
    lateinit var db: SQLiteDatabase

    private var upURLList = arrayListOf<String>()
    private var downURLList = arrayListOf<String>()
    private var nameStringArrayList = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_station_list_add_button.setOnClickListener {
            //追加BottomFragment
            if (fragmentManager != null) {
                val addStationBottomFragment = AddStationBottomFragment()
                addStationBottomFragment.show(fragmentManager!!, "add")
            }
        }

        //データベース読み込み
        loadDB()
        //ListViewドラッグとか
        setDragListView()

    }

    private fun setDragListView() {
        drag_listview.setDragListListener(object : DragListView.DragListListener {
            override fun onItemDragging(itemPosition: Int, x: Float, y: Float) {

            }

            override fun onItemDragEnded(fromPosition: Int, toPosition: Int) {
                //入れ替え実行
                setSortMenu(fromPosition, toPosition)
            }

            override fun onItemDragStarted(position: Int) {

            }
        })
    }

    private fun setSortMenu(start: Int, end: Int) {
        //startを一時保存アンド削除
        val start_item = nameStringArrayList[start]
        nameStringArrayList.remove(start_item)
        //入れる
        nameStringArrayList.add(end, start_item)

        //一時的にSQLiteの内容を配列に入れる
        val name_List = ArrayList<String>()
        val upList = arrayListOf<String>()
        val downList = arrayListOf<String>()
        val settingList = arrayListOf<String>()

        for (i in 0 until nameStringArrayList.size) {
            //Step 1.name/up/down/settingを取得する
            name_List.add(nameStringArrayList[i])
            upList.add(getUpLink(nameStringArrayList[i]) ?: "")
            downList.add(getDownLink(nameStringArrayList[i]) ?: "")
            settingList.add(getSetting(nameStringArrayList[i]) ?: "")
        }
        //Step 2.SQLite更新
        //最初にDB全クリアする
        db.delete("station_db", null, null)
        //println(name_List)
        for (i in 0 until name_List.size) {
            writeSQLiteDB(name_List[i], upList[i], downList[i], settingList[i])
        }
        //再読み込み
        loadDB()
    }

    /**
     * SQLite書き込む
     */
    private fun writeSQLiteDB(name: String, up: String, down: String, setting: String) {
        //入れる
        val values = ContentValues()
        values.put("name", name)
        values.put("up", up)
        values.put("down", down)
        values.put("setting", setting)
        db.insert("station_db", "", values)
    }

    /**
     * SQLiteから指定した名前の上りURLを返します
     */
    private fun getUpLink(name: String): String? {
        var value: String? = null
        val cursor = db.query(
            "station_db",
            arrayOf("up"),
            "name=?",
            arrayOf(name), null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(0)
            cursor.close()
        }
        return value
    }

    /**
     * SQLiteから指定した名前の下りURLを返します
     */
    private fun getDownLink(name: String): String? {
        var value: String? = null
        val cursor = db.query(
            "station_db",
            arrayOf("down"),
            "name=?",
            arrayOf(name), null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(0)
            cursor.close()
        }
        return value
    }

    /**
     * SQLiteから指定した名前の設定を返します
     */
    private fun getSetting(name: String): String? {
        var value: String? = null
        val cursor = db.query(
            "station_db",
            arrayOf("setting"),
            "name=?",
            arrayOf(name), null, null, null
        )
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(0)
            cursor.close()
        }
        return value
    }


    fun loadDB() {

        nameStringArrayList.clear()

        //データベース用意
        if (!this@StationListFragment::helper.isInitialized) {
            helper = StationSQLiteHelper(context!!)
            db = helper.writableDatabase
            db.disableWriteAheadLogging()
        }

        val testArrayList = ArrayList<Pair<Long, String>>()

        val cursor = db.query(
            "station_db",
            arrayOf("up", "down", "name"), null, null, null, null, null
        )
        cursor.moveToFirst()
        for (i in 0 until cursor.count) {
            upURLList.add(cursor.getString(0))
            downURLList.add(cursor.getString(1))
            nameStringArrayList.add(cursor.getString(2))
            testArrayList.add(Pair(i.toLong(), cursor.getString(2)))
            cursor.moveToNext()
        }
        drag_listview.setLayoutManager(LinearLayoutManager(context))
        val listAdapter = DragListViewAdapter(
            testArrayList,
            R.layout.adapter_draglayout,
            R.id.adapter_drag_imageview,
            false,
            activity!!
        )
        drag_listview.setAdapter(listAdapter, true)
        drag_listview.setCanDragHorizontally(false)
        cursor.close()
    }

}
