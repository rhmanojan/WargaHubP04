package id.antasari.p04.wargahub.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.antasari.p04.wargahub.R
import id.antasari.p04.wargahub.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        binding.btnLihatLaporan.setOnClickListener {
            val nav = findNavController()
            if (nav.currentDestination?.id == R.id.homeFragment) {
                nav.navigate(R.id.action_home_to_list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
