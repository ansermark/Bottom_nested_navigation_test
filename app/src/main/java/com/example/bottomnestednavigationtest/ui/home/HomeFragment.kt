package com.example.bottomnestednavigationtest.ui.home

import android.annotation.*
import android.os.*
import android.view.*
import android.widget.*
import androidx.fragment.app.*
import androidx.lifecycle.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.*
import com.example.bottomnestednavigationtest.*
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
            findNavController().navigate(
                R.id.notifications_nav_graph,
                null,
                navOptions {
                    launchSingleTop = true
                    restoreState = true

                    popUpTo(findNavController().graph.findStartDestination().id) {
                        saveState = true
                    }
                }
            )
        }

        binding.buttonOpenCounter.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToNavigationCount())
        }

        binding.buttonAdditional.setOnClickListener {
            findNavController().navigate(
                HomeNavGraphDirections.actionGraphHomeToGraphAdditional("From home")
            )
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}