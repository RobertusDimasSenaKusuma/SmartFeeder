package com.example.smartfeeder.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.semantics.text
import androidx.navigation.activity
// import androidx.navigation.fragment.findNavController // Jika Anda menggunakan Navigation Component untuk navigasi internal
import com.example.smartfeeder.AuthActivity // Import AuthActivity
import com.example.smartfeeder.R // Import R untuk mengakses string resources
import com.example.smartfeeder.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser // Pastikan FirebaseUser diimport

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth // Deklarasikan FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance() // Inisialisasi FirebaseAuth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile() // Panggil fungsi untuk memuat data profil

        binding.logoutButton.setOnClickListener {
            performLogout()
        }

        binding.menuEditProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profil diklik", Toast.LENGTH_SHORT).show()
            // TODO: Navigasi ke Edit Profile
        }

        binding.menuChangePassword.setOnClickListener {
            Toast.makeText(requireContext(), "Ubah Password diklik", Toast.LENGTH_SHORT).show()
            // TODO: Navigasi atau tampilkan dialog Ubah Password
        }

        binding.menuNotifications.setOnClickListener {
            Toast.makeText(requireContext(), "Notifikasi diklik", Toast.LENGTH_SHORT).show()
            // TODO: Navigasi ke Pengaturan Notifikasi
        }
    }

    private fun performLogout() {
        auth.signOut() // Proses logout dari Firebase

        // Arahkan kembali ke AuthActivity
        val intent = Intent(activity, AuthActivity::class.java)
        // Flag untuk membersihkan back stack sehingga pengguna tidak kembali ke MainActivity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish() // Tutup MainActivity

        Toast.makeText(requireContext(), "Anda telah logout", Toast.LENGTH_SHORT).show()
    }

    // Fungsi ini yang kita modifikasi untuk placeholder teks dinamis
    private fun loadUserProfile() {
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            // Mengatur nama pengguna
            val displayName = currentUser.displayName
            binding.profileNameTextView.text = if (!displayName.isNullOrEmpty()) {
                displayName
            } else {
                // Gunakan teks default dari strings.xml jika nama dari Firebase kosong/null
                getString(R.string.default_name)
            }

            // Mengatur email pengguna
            val email = currentUser.email
            binding.profileEmailTextView.text = if (!email.isNullOrEmpty()) {
                email
            } else {
                // Gunakan teks default dari strings.xml jika email dari Firebase kosong/null
                getString(R.string.default_email)
            }

            // Bagian ImageView tidak diubah, akan tetap menampilkan apapun yang
            // diatur di XML (misalnya, R.drawable.ic_placeholder_profile)
            // binding.profileImageView.setImageResource(R.drawable.ic_placeholder_profile) // Baris ini bisa dihapus jika XML sudah benar

        } else {
            // Tidak ada pengguna yang login, gunakan nilai default dari strings.xml untuk teks
            binding.profileNameTextView.text = getString(R.string.default_name)
            binding.profileEmailTextView.text = getString(R.string.default_email)

            // Bagian ImageView tidak diubah
            // binding.profileImageView.setImageResource(R.drawable.ic_placeholder_profile) // Baris ini bisa dihapus jika XML sudah benar
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}