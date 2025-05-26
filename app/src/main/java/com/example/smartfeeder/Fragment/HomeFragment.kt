package com.example.smartfeeder.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartfeeder.MainActivity
import com.example.smartfeeder.R
import com.example.smartfeeder.StockFeederActivity

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Tetap menggunakan inflate biasa seperti sebelumnya
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kode ActionBar sebelumnya tetap dipertahankan
        if (activity is MainActivity) {
            (activity as MainActivity).setupCustomHomeActionBar("Selamat Datang,")
        }

        // TAMBAHKAN ini untuk handle klik menu Feed Stock
        view.findViewById<View>(R.id.menuFeedStock)?.setOnClickListener {
            // Pindah ke StockFeederActivity ketika diklik
            startActivity(Intent(activity, StockFeederActivity::class.java))

        }
    }

    override fun onPause() {
        super.onPause()
        // Kode sebelumnya tetap dipertahankan
    }
}