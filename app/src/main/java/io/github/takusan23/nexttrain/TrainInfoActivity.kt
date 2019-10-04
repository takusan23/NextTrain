package io.github.takusan23.nexttrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import io.github.takusan23.nexttrain.Adapter.TrainInfoAdapter
import io.github.takusan23.nexttrain.Scraping.TrainTimeTable
import io.github.takusan23.nexttrain.ViewPagerAdapter.TrainTimeInfoFragmentViewPagerAdapter
import kotlinx.android.synthetic.main.activity_train_info.*
import kotlinx.android.synthetic.main.adapter_next_train_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.util.*
import kotlin.collections.ArrayList

class TrainInfoActivity : AppCompatActivity() {

    val trainTimeTable = TrainTimeTable()

    //  lateinit var adapter: TrainInfoAdapter
    //  val nextTrainList = arrayListOf<ArrayList<String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_info)

        val up = intent.getStringExtra("up")
        val down = intent.getStringExtra("down")

        //タイトルバー非表示
        supportActionBar?.hide()

        //Intentから受け取る
        val intentStationName = intent.getStringExtra("name")
        val intentStationTime = intent.getStringExtra("time")
        val intentStationDescription = intent.getStringExtra("description")
        adapter_next_train_station_name_textview.text = intentStationName
        adapter_next_train_time_textview.text = intentStationTime
        adapter_next_train_type_for_textview.text = intentStationDescription


        // //recyclerview
        // activity_train_info_recyclerview.setHasFixedSize(true)
        // val layoutManager = LinearLayoutManager(this)
        // activity_train_info_recyclerview.layoutManager = layoutManager
        // adapter = TrainInfoAdapter(nextTrainList)
        // activity_train_info_recyclerview.adapter = adapter

        activity_train_info_bottom_menu.setOnNavigationItemSelectedListener {
            // nextTrainList.clear()
            activity_train_info_tab_layout.getTabAt(0)?.select()
            when (it.itemId) {
                R.id.menu_train_info_up -> {
                    //上り
                    trainTimeTable.upTrainTimeTableListHourList.forEach {
                        if (it.isNotEmpty()) {
                            val title = "${it}時"
                            val tabItem = activity_train_info_tab_layout.newTab()
                            tabItem.text = title
                            activity_train_info_tab_layout.addTab(tabItem)
                        }
                    }
                }
                R.id.menu_train_info_down -> {
                    //下り
                    trainTimeTable.downTrainTimeTableListHourList.forEach {
                        if (it.isNotEmpty()) {
                            val title = "${it}時"
                            val tabItem = activity_train_info_tab_layout.newTab()
                            tabItem.text = title
                            activity_train_info_tab_layout.addTab(tabItem)
                        }
                    }
                }
            }
            true
        }


        GlobalScope.launch {
            async {
                trainTimeTable.getNextTrain(up, down)
            }.await()

            //TabLayout
            runOnUiThread {
                trainTimeTable.upTrainTimeTableListHourList.forEach {
                    if (it.isNotEmpty()) {
                        val title = "${it}時"
                        val tabItem = activity_train_info_tab_layout.newTab()
                        tabItem.text = title
                        activity_train_info_tab_layout.addTab(tabItem)
                    }
                }

                //取得成功したら表示をVisibleに切り替える
                //表示アニメーションに挑戦した。
                val showAnimation =
                    AnimationUtils.loadAnimation(this@TrainInfoActivity, R.anim.show_anim)
                activity_train_info_card_info_cardview.startAnimation(showAnimation)
                activity_train_info_card_info_cardview.visibility = View.VISIBLE

                //タイトル変更
                supportActionBar?.title = trainTimeTable.stationName
                //Cardの部分を作る
                setCard()

                //今の時間の時刻表を表示させる
                setNowHourTimeTable()

                //Cardの上り下り選択中を取得
                setTimeTable()
                //選んだとき
                adapter_next_train_tab_layout.addOnTabSelectedListener(object :
                    TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        setTimeTable()
                    }
                })
            }
        }
    }

    //時刻表設定
    fun setTimeTable() {
        val tabLayoutPos = adapter_next_train_tab_layout.selectedTabPosition
        //上り配列
        val upTimelist = arrayListOf<ArrayList<String>>()
        val upDesclist = arrayListOf<ArrayList<String>>()
        val upTitleList = arrayListOf<String>()
        //下り配列
        val downTimelist = arrayListOf<ArrayList<String>>()
        val downDesclist = arrayListOf<ArrayList<String>>()
        val downTitleList = arrayListOf<String>()
        //上り
        for (s in 0 until trainTimeTable.upTrainTimeTableListHourList.size) {
            if (trainTimeTable.upTrainTimeTableListHourList[s].isNotEmpty()) {
                //時間
                val hour = trainTimeTable.upTrainTimeTableListHourList[s]
                //配列取得
                val trainList = trainTimeTable.upTrainTimeTableListList[s]
                //特急
                val trainType = trainTimeTable.upTrainTimeTableListTypeList[s]
                //行き先
                val trainFor = trainTimeTable.upTrainTimeTableListForList[s]
                //指定した時間の時刻表を入れる
                val list = arrayListOf<String>()
                val desc = arrayListOf<String>()
                for (i in 0 until trainList.size) {
                    list.add("${hour}時${trainList[i]}分")
                    desc.add("${trainType[i]} ${trainFor[i]}")
                }
                //リスト追加
                upTimelist.add(list)
                upDesclist.add(desc)
                //名前
                upTitleList.add(hour + "時")
            }
        }
        //下り
        for (s in 0 until trainTimeTable.downTrainTimeTableListHourList.size) {
            if (trainTimeTable.downTrainTimeTableListHourList[s].isNotEmpty()) {
                //時間
                val hour = trainTimeTable.downTrainTimeTableListHourList[s]
                //配列取得
                val trainList = trainTimeTable.downTrainTimeTableListList[s]
                //特急
                val trainType = trainTimeTable.downTrainTimeTableListTypeList[s]
                //行き先
                val trainFor = trainTimeTable.downTrainTimeTableListForList[s]
                //指定した時間の時刻表を入れる
                val list = arrayListOf<String>()
                val desc = arrayListOf<String>()
                for (i in 0 until trainList.size) {
                    list.add("${hour}時${trainList[i]}分")
                    desc.add("${trainType[i]} ${trainFor[i]}")
                }
                //リスト追加
                downTimelist.add(list)
                downDesclist.add(desc)
                //名前
                downTitleList.add(hour + "時")
            }
        }

        //Adapter
        val adapter = TrainTimeInfoFragmentViewPagerAdapter(
            supportFragmentManager,
            this,
            upTimelist,
            upDesclist,
            upTitleList,
            downTimelist,
            downDesclist,
            downTitleList
        )
        activity_train_info_viewpager.adapter = adapter
        activity_train_info_tab_layout.setupWithViewPager(
            activity_train_info_viewpager
        )

    }

    private fun setNowHourTimeTable() {
        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)
        val tabLayoutPos = adapter_next_train_tab_layout.selectedTabPosition
        //tabを選択
        for (count in 0 until activity_train_info_tab_layout.tabCount) {
            if (activity_train_info_tab_layout.getTabAt(count)?.text.toString() == hour.toString() + "時") {
                //選択
                activity_train_info_tab_layout.getTabAt(count)?.select()
            }
        }
        if (tabLayoutPos == 0) {
            //上り
            for (s in 0 until trainTimeTable.upTrainTimeTableListHourList.size) {
                if (trainTimeTable.upTrainTimeTableListHourList[s] == hour.toString()) {
                    //配列取得
                    val trainList = trainTimeTable.upTrainTimeTableListList[s]
                    //特急
                    val trainType = trainTimeTable.upTrainTimeTableListTypeList[s]
                    //行き先
                    val trainFor = trainTimeTable.upTrainTimeTableListForList[s]
                    //指定した時間の時刻表を入れる
                    for (i in 0 until trainList.size) {
                        val list = arrayListOf<String>()
                        list.add("")
                        list.add("${hour}時${trainList[i]}分")
                        list.add("${trainType[i]} ${trainFor[i]}")
                        // nextTrainList.add(list)
                    }
                }
            }
        } else {
            //下り
            for (s in 0 until trainTimeTable.downTrainTimeTableListHourList.size) {
                if (trainTimeTable.downTrainTimeTableListHourList[s] == hour.toString()) {
                    //配列取得
                    val trainList = trainTimeTable.downTrainTimeTableListList[s]
                    //特急
                    val trainType = trainTimeTable.downTrainTimeTableListTypeList[s]
                    //行き先
                    val trainFor = trainTimeTable.downTrainTimeTableListForList[s]
                    //指定した時間の時刻表を入れる
                    for (i in 0 until trainList.size) {
                        val list = arrayListOf<String>()
                        list.add("")
                        list.add("${hour}時${trainList[i]}分")
                        list.add("${trainType[i]} ${trainFor[i]}")
                        // nextTrainList.add(list)
                    }
                }
            }
        }

    }


    private fun setCard() {
        adapter_next_train_station_name_textview.text = trainTimeTable.stationName
        adapter_next_train_time_textview.text = trainTimeTable.upNextTime
        adapter_next_train_type_for_textview.text =
            "${trainTimeTable.upTrainType} ${trainTimeTable.upTrainFor}"
    }
}
