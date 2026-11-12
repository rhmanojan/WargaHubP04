package id.antasari.p04.wargahub.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.antasari.p04.wargahub.R
import id.antasari.p04.wargahub.data.LaporanContoh
import id.antasari.p04.wargahub.databinding.FragmentDetailBinding

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)
        val id = arguments?.getLong(ARG_ID, -1L) ?: -1L
        val laporan = LaporanContoh.cari(id)
        if (laporan == null) {
            binding.tvJudul.setText(R.string.detail_missing)
            binding.tvKategori.visibility = View.GONE
            binding.tvStatus.visibility = View.GONE
            binding.tvDeskripsi.visibility = View.GONE
        } else {
            binding.tvJudul.text = laporan.judul
            binding.tvKategori.text = getString(
                R.string.detail_category, laporan.kategori
            )
            binding.tvStatus.text = getString(
                R.string.detail_status, laporan.status
            )
            binding.tvDeskripsi.text = laporan.deskripsi
        }
        binding.btnKembaliDaftar.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_ID = "laporan_id"
    }
}
