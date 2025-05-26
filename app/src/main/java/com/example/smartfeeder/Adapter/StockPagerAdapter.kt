package com.example.smartfeeder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.smartfeeder.Fragment.StockDrinkFragment
import com.example.smartfeeder.Fragment.StockFeederFragment

class StockPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // Tab titles
    private val tabTitles = arrayOf("Makanan", "Minuman")

    override fun getCount(): Int = 2 //

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> StockFeederFragment()
            1 -> StockDrinkFragment()
            else -> StockFeederFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }
}