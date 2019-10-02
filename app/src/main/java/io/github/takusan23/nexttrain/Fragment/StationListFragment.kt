package io.github.takusan23.nexttrain.Fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.takusan23.nexttrain.R
import kotlinx.android.synthetic.main.fragment_station_list.*

/**
 * A simple [Fragment] subclass.
 */
class StationListFragment : Fragment() {

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

    }

}
