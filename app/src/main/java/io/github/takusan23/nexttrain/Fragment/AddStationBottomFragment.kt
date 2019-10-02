package io.github.takusan23.nexttrain.Fragment

import android.content.ContentValues
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.takusan23.nexttrain.DataBase.StationSQLiteHelper
import io.github.takusan23.nexttrain.R
import kotlinx.android.synthetic.main.bottom_fragment_add_station.*

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
            //保存
            val contentValues = ContentValues()
            contentValues.put("name", "")
            contentValues.put("up", bottom_fragment_add_station_up_url_textinput.text.toString())
            contentValues.put(
                "down",
                bottom_fragment_add_station_down_url_textinput.text.toString()
            )
            contentValues.put("setting", "")
            //入れる
            db.insert("station_db", null, contentValues)
            //閉じる
            dismiss()
        }

    }

}