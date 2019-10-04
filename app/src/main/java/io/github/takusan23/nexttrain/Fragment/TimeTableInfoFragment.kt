package io.github.takusan23.nexttrain.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.takusan23.nexttrain.Adapter.TrainInfoAdapter

import io.github.takusan23.nexttrain.R
import kotlinx.android.synthetic.main.adapter_next_train_layout.*
import kotlinx.android.synthetic.main.fragment_time_table_info.*

/**
 * A simple [Fragment] subclass.
 */
class TimeTableInfoFragment : Fragment() {

    lateinit var adapter: TrainInfoAdapter
    val nextTrainList = arrayListOf<ArrayList<String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_table_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //recyclerview
        fragment_timetable_recyclerview.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        fragment_timetable_recyclerview.layoutManager = layoutManager
        adapter = TrainInfoAdapter(nextTrainList)
        fragment_timetable_recyclerview.adapter = adapter

        nextTrainList.clear()

        val upTimeList = arguments?.getStringArrayList("uptimelist")
        val upDescList = arguments?.getStringArrayList("updesclist")
        val downTimeList = arguments?.getStringArrayList("downtimelist")
        val downDescList = arguments?.getStringArrayList("downdesclist")

        if (activity?.adapter_next_train_tab_layout?.selectedTabPosition == 0) {
            for (i in 0 until (upTimeList?.size ?: 0)) {
                val list = arrayListOf<String>()
                list.add("")
                list.add(upTimeList?.get(i) ?: "")
                list.add(upDescList?.get(i) ?: "")
                nextTrainList.add(list)
            }
        } else {
            for (i in 0 until (upDescList?.size ?: 0)) {
                val list = arrayListOf<String>()
                list.add("")
                list.add(downTimeList?.get(i) ?: "")
                list.add(downDescList?.get(i) ?: "")
                nextTrainList.add(list)
            }
        }
        adapter.notifyDataSetChanged()

    }

}
