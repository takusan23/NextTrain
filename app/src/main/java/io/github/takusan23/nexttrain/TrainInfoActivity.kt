package io.github.takusan23.nexttrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import io.github.takusan23.nexttrain.Adapter.NextTrainRecyclerViewAdapter
import io.github.takusan23.nexttrain.Adapter.TrainInfoAdapter
import io.github.takusan23.nexttrain.Scraping.TrainTimeTable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_train_info.*
import kotlinx.android.synthetic.main.adapter_next_train_layout.*
import kotlinx.android.synthetic.main.fragment_next_train.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrainInfoActivity : AppCompatActivity() {

    val trainTimeTable = TrainTimeTable()

    lateinit var adapter: TrainInfoAdapter
    val nextTrainList = arrayListOf<ArrayList<String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_info)

        val up = intent.getStringExtra("up")
        val down = intent.getStringExtra("down")

        //タイトルバー非表示
        supportActionBar?.hide()

        //recyclerview
        activity_train_info_recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        activity_train_info_recyclerview.layoutManager = layoutManager
        adapter = TrainInfoAdapter(nextTrainList)
        activity_train_info_recyclerview.adapter = adapter

        activity_train_info_bottom_menu.setOnNavigationItemSelectedListener {
            nextTrainList.clear()
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

                //タイトル変更
                supportActionBar?.title = trainTimeTable.stationName
                //Cardの部分を作る
                setCard()

                //時間（スクロールするやつ）押したとき
                activity_train_info_tab_layout.addOnTabSelectedListener(object :
                    TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        nextTrainList.clear()
                        var text = (tab?.text ?: "4時").toString()
                        text = text.replace("時", "")

                        //Cardの上り下り選択中を取得
                        val tabLayoutPos = adapter_next_train_tab_layout.selectedTabPosition
                        if (tabLayoutPos == 0) {
                            //上り
                            for (s in 0 until trainTimeTable.upTrainTimeTableListHourList.size) {
                                if (trainTimeTable.upTrainTimeTableListHourList[s] == text) {
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
                                        list.add("${tab?.text}${trainList[i]}分")
                                        list.add("${trainType[i]} ${trainFor[i]}")
                                        nextTrainList.add(list)
                                    }
                                }
                            }
                        } else {
                            //下り
                            for (s in 0 until trainTimeTable.downTrainTimeTableListHourList.size) {
                                if (trainTimeTable.downTrainTimeTableListHourList[s] == text) {
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
                                        list.add("${tab?.text}${trainList[i]}分")
                                        list.add("${trainType[i]} ${trainFor[i]}")
                                        nextTrainList.add(list)
                                    }
                                }
                            }
                        }
                        //更新
                        adapter.notifyDataSetChanged()
                    }
                })

            }
        }
    }

    private fun setCard() {
        adapter_next_train_station_name_textview.text = trainTimeTable.stationName
    }
}
