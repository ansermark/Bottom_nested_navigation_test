package com.example.bottomnestednavigationtest.ui.home

import android.annotation.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import androidx.lifecycle.*
import androidx.navigation.*
import androidx.navigation.fragment.*
import com.example.bottomnestednavigationtest.R
import com.example.bottomnestednavigationtest.databinding.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        binding.buttonOpenNotification.setOnClickListener {
            val notificationsGraph =
                findNavController().findDestination(R.id.navigation_graph_notifications) as? NavGraph

            notificationsGraph?.startDestinationId?.let { startDestinationId ->
                notificationsGraph.setStartDestination(R.id.navigation_notification)
                findNavController().navigate(R.id.action_navigation_home_to_navigation_notifications)
                notificationsGraph.setStartDestination(startDestinationId)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}