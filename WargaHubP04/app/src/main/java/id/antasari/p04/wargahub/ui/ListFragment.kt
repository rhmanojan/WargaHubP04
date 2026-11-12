package id.antasari.p04.wargahub.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.antasari.p04.wargahub.R
import id.antasari.p04.wargahub.adapter.LaporanAdapter
import id.antasari.p04.wargahub.data.LaporanContoh
import id.antasari.p04.wargahub.databinding.FragmentListBinding

class ListFragment : Fragment(R.layout.fragment_list) {
    private var _binding: FragmentListBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var contohKosong = false
    private var laporanAdapter: LaporanAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contohKosong = savedInstanceState?.getBoolean(KEY_KOSONG) ?: false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListBinding.bind(view)
        val adapter = LaporanAdapter { laporan ->
            val nav = findNavController()
            if (nav.currentDestination?.id == R.id.listFragment) {
                nav.navigate(
                    R.id.action_list_to_detail,
                    bundleOf(DetailFragment.ARG_ID to laporan.id)
                )
            }
        }
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        laporanAdapter = adapter
        binding.rvLaporan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLaporan.adapter = adapter
        binding.btnModeKosong.setOnClickListener {
            contohKosong = !contohKosong
            tampilkanDaftar()
        }
        tampilkanDaftar()
    }

    private fun tampilkanDaftar() {
        val data = if (contohKosong) emptyList() else LaporanContoh.semua
        laporanAdapter?.submitList(data)
        binding.rvLaporan.isVisible = data.isNotEmpty()
        binding.emptyContainer.isVisible = data.isEmpty()
        binding.tvJumlah.text = getString(R.string.list_count, data.size)
        binding.btnModeKosong.setText(
            if (contohKosong) R.string.show_sample else R.string.try_empty
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_KOSONG, contohKosong)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        binding.rvLaporan.adapter = null
        laporanAdapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val KEY_KOSONG = "contoh_kosong"
    }
}
