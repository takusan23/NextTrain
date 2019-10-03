package io.github.takusan23.nexttrain.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.takusan23.nexttrain.DataBase.StationSQLiteHelper
import io.github.takusan23.nexttrain.R
import io.github.takusan23.nexttrain.Scraping.TrainTimeTable
import kotlinx.android.synthetic.main.bottom_fragment_add_station.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddStationBottomFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_fragment_add_station, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //データベース用意
        val helper = StationSQLiteHelper(context!!)
        val db = helper.writableDatabase
        db.disableWriteAheadLogging()

        bottom_fragment_add_station_add_button.setOnClickListener {

            GlobalScope.launch {
                val up = bottom_fragment_add_station_up_url_textinput.text.toString()
                val down = bottom_fragment_add_station_down_url_textinput.text.toString()
                //スクレイピング
                val trainTimeTable = TrainTimeTable()
                trainTimeTable.getNextTrain(up, true)
                //タイトル取得
                val name = trainTimeTable.stationName
                activity?.runOnUiThread {
                    //保存
                    val contentValues = ContentValues()
                    contentValues.put("name", name)
                    contentValues.put(
                        "up",
                        bottom_fragment_add_station_up_url_textinput.text.toString()
                    )
                    contentValues.put(
                        "down",
                        bottom_fragment_add_station_down_url_textinput.text.toString()
                    )
                    contentValues.put("setting", "")
                    //入れる
                    db.insert("station_db", null, contentValues)
                    //閉じる
                    dismiss()
                    //リスト更新
                    if (activity is AppCompatActivity) {
                        val fragment =
                            (activity as AppCompatActivity).supportFragmentManager?.findFragmentById(
                                R.id.main_fragment
                            )
                        if (fragment is StationListFragment) {
                            fragment.loadDB()
                        }
                    }
                }
            }
        }

    }

}