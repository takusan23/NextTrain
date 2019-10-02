package io.github.takusan23.nexttrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.takusan23.nexttrain.Fragment.NextTrainFragment
import io.github.takusan23.nexttrain.Fragment.StationListFragment
import io.github.takusan23.nexttrain.Scraping.TrainTimeTable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //次の電車
        val trans = supportFragmentManager.beginTransaction()
        trans.replace(R.id.main_fragment, NextTrainFragment())
        trans.commit()


        main_bottom_nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_add_station -> {
                    //駅追加
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(R.id.main_fragment, StationListFragment())
                    trans.commit()
                }
                R.id.menu_next_train -> {
                    //次の電車
                    val trans = supportFragmentManager.beginTransaction()
                    trans.replace(R.id.main_fragment, NextTrainFragment())
                    trans.commit()
                }
            }
            true
        }
    }
}
