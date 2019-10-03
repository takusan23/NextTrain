package io.github.takusan23.nexttrain.Fragment


import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.takusan23.nexttrain.Adapter.NextTrainRecyclerViewAdapter
import io.github.takusan23.nexttrain.DataBase.StationSQLiteHelper

import io.github.takusan23.nexttrain.R
import io.github.takusan23.nexttrain.Scraping.TrainTimeTable
import kotlinx.android.synthetic.main.fragment_next_train.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.concurrent.thread


/**
 * A simple [Fragment] subclass.
 */
class NextTrainFragment : Fragment() {

    lateinit var adapter: NextTrainRecyclerViewAdapter
    val nextTrainList = arrayListOf<ArrayList<String>>()

    //でーたべーす
    lateinit var helper: StationSQLiteHelper
    lateinit var db: SQLiteDatabase

    //並び順。非同期処理なのでどうしても合わなくなる


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_train, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //recyclerview
        fragment_next_train_recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        fragment_next_train_recyclerview.layoutManager = layoutManager
        adapter = NextTrainRecyclerViewAdapter(nextTrainList)
        fragment_next_train_recyclerview.adapter = adapter

        setNextTrain()

        fragment_next_train_swipe.setOnRefreshListener {
            setNextTrain()
        }

    }

    fun setNextTrain() {

        nextTrainList.clear()

        //くるくる
        fragment_next_train_swipe.isRefreshing = true

        //データベース用意
        if (!this@NextTrainFragment::helper.isInitialized) {
            helper = StationSQLiteHelper(context!!)
            db = helper.writableDatabase
            db.disableWriteAheadLogging()
        }

        //取り出し
        val cursor = db.query(
            "station_db",
            arrayOf("up", "down", "name"),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        GlobalScope.launch {
            for (i in 0 until cursor.count) {
                //リクエスト
                val up = cursor.getString(0)
                val down = cursor.getString(1)
                val trainTimeTable = TrainTimeTable()
                //スクレイピングできるまで待つ
                async {
                    trainTimeTable.getNextTrain(
                        up, down
                    )
                    val arrayList = arrayListOf<String>()
                    arrayList.add("")
                    arrayList.add(trainTimeTable.stationName)
                    arrayList.add(trainTimeTable.upNextTime)
                    arrayList.add(trainTimeTable.downNextTime)
                    arrayList.add(trainTimeTable.upTrainType)
                    arrayList.add(trainTimeTable.downTrainType)
                    arrayList.add(trainTimeTable.upTrainFor)
                    arrayList.add(trainTimeTable.downTrainFor)
                    nextTrainList.add(arrayList)
                    //次に進む
                    cursor.moveToNext()
                }.await()
            }
            activity?.runOnUiThread {
                adapter.notifyDataSetChanged()
                //くるくる
                fragment_next_train_swipe.isRefreshing = false
                //閉じる
                cursor.close()
            }
        }

/*
        for (i in 0 until cursor.count) {
            //リクエスト
            val up = cursor.getString(0)
            val down = cursor.getString(1)

            GlobalScope.launch {
                val trainTimeTable = TrainTimeTable()
                //スクレイピングできるまで待つ
                async {
                    trainTimeTable.getNextTrain(
                        up, down
                    )
                }.await()

                activity?.runOnUiThread {
                    val arrayList = arrayListOf<String>()
                    arrayList.add("")
                    arrayList.add(trainTimeTable.stationName)
                    arrayList.add(trainTimeTable.upNextTime)
                    arrayList.add(trainTimeTable.downNextTime)
                    arrayList.add(trainTimeTable.upTrainType)
                    arrayList.add(trainTimeTable.downTrainType)
                    arrayList.add(trainTimeTable.upTrainFor)
                    arrayList.add(trainTimeTable.downTrainFor)
                    nextTrainList.add(arrayList)
                    adapter.notifyDataSetChanged()
                    //くるくる
                    fragment_next_train_swipe.isRefreshing = false
                }
            }
            cursor.moveToNext()
        }
*/

    }


}
