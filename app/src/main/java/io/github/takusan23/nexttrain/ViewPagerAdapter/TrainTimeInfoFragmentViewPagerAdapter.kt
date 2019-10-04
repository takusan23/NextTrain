package io.github.takusan23.nexttrain.ViewPagerAdapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.takusan23.nexttrain.Fragment.TimeTableInfoFragment
import io.github.takusan23.nexttrain.TrainInfoActivity
import kotlinx.android.synthetic.main.adapter_next_train_layout.*

class TrainTimeInfoFragmentViewPagerAdapter(
    val fragmentManager: FragmentManager,
    val activity: TrainInfoActivity,
    val upTimelist: ArrayList<ArrayList<String>>,
    val upDescriptionlist: ArrayList<ArrayList<String>>,
    val upTrainTimeTableListHourList: ArrayList<String>,
    val downTimelist: ArrayList<ArrayList<String>>,
    val downDescriptionlist: ArrayList<ArrayList<String>>,
    val downTrainTimeTableListHourList: ArrayList<String>
) :
    FragmentPagerAdapter(
        fragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

    override fun getItem(position: Int): Fragment {
        val fragment = TimeTableInfoFragment()
        val arg = Bundle()
        arg.putStringArrayList("uptimelist", upTimelist[position])
        arg.putStringArrayList("updesclist", upDescriptionlist[position])
        arg.putStringArrayList("downtimelist", downTimelist[position])
        arg.putStringArrayList("downdesclist", downDescriptionlist[position])
        fragment.arguments = arg
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (activity.adapter_next_train_tab_layout.selectedTabPosition == 0) {
            return upTrainTimeTableListHourList[position]
        } else {
            return downTrainTimeTableListHourList[position]
        }
    }

    override fun getCount(): Int {
        if (activity.adapter_next_train_tab_layout.selectedTabPosition == 0) {
            return upTrainTimeTableListHourList.size
        } else {
            return downTrainTimeTableListHourList.size
        }
    }

}