package com.example.smartfeeder.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartfeeder.MainActivity // Pastikan import MainActivity
import com.example.smartfeeder.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout untuk fragment ini
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Beri tahu MainActivity untuk setup ActionBar kustom untuk Home
        if (activity is MainActivity) {
            (activity as MainActivity).setupCustomHomeActionBar("Selamat Datang,")
        }
    }

    override fun onPause() {
        super.onPause()
        // Saat HomeFragment tidak lagi aktif, minta Activity untuk mengembalikan ActionBar
        // ke keadaan yang lebih umum atau yang sesuai untuk fragment berikutnya.
        // Ini membantu jika pengguna menavigasi ke fragment lain yang tidak secara eksplisit
        // mengatur ActionBar-nya sendiri melalui listener BottomNavigationView.
        // Namun, karena listener BottomNav sudah mengatur ActionBar untuk Device & Settings,
        // ini mungkin tidak selalu diperlukan, tetapi bisa menjadi fallback yang baik.
        // if (activity is MainActivity) {
        //    (activity as MainActivity).setupDefaultActionBarFor("SmartFeeder") // Judul generik
        // }
    }
}