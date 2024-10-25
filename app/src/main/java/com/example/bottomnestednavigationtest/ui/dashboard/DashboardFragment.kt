package com.example.bottomnestednavigationtest.ui.dashboard

import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import androidx.lifecycle.*
import androidx.navigation.fragment.*
import com.example.bottomnestednavigationtest.databinding.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonAdditional.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.toGraphAdditional("From dashboard"))
        }
        binding.buttonToHome.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.toGraphHome())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}