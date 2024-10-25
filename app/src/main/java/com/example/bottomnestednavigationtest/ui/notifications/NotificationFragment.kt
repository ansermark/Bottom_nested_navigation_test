package com.example.bottomnestednavigationtest.ui.notifications

import android.os.*
import android.view.*
import androidx.fragment.app.*
import androidx.navigation.fragment.*
import com.example.bottomnestednavigationtest.*
import com.example.bottomnestednavigationtest.databinding.*

class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        binding.textNotification.text = "Уведомление"

        binding.buttonOpenDial.setOnClickListener {
            findNavController().navigate(GlobalHostGraphDirections.showDialInfo())
            findNavController().navigate(GlobalHostGraphDirections.showDialInfo())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}