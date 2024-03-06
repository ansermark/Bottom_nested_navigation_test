package com.example.bottomnestednavigationtest.ui.home

import android.annotation.*
import android.os.*
import android.view.*
import androidx.annotation.*
import androidx.core.net.*
import androidx.fragment.app.*
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.fragment.*
import com.example.bottomnestednavigationtest.*
import com.example.bottomnestednavigationtest.R
import com.example.bottomnestednavigationtest.databinding.*

class CountFragment : Fragment() {

    private var _binding: FragmentCountBinding? = null
    private val binding get() = _binding!!
    private val args: CountFragmentArgs by navArgs()

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCountBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.buttonOpenNotification.setOnClickListener {
            val request = NavDeepLinkRequest.Builder
                .fromUri("app://bottomnestednavigationtest/navigation_notification".toUri())
                .build()
            findNavController().navigate(request)
//            val notificationsGraph =
//                findNavController().findDestination(R.id.navigation_graph_notifications) as? NavGraph
//
//            notificationsGraph?.startDestinationId?.let { startDestinationId ->
//                notificationsGraph.setStartDestination(R.id.navigation_notification)
//                findNavController().navigate(
//                    NavigationGraphHomeDirections.actionGraphHomeToGraphNotifications(),
//                    navOptions {
//                        popUpTo(findNavController().graph.findStartDestination().id) {
//                            inclusive = false
//                            saveState = true
//                        }
//                    }
//                )
//                notificationsGraph.setStartDestination(startDestinationId)
//            }
        }

        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(CountFragmentDirections.actionNavigationCountSelf(args.counter + 1))
        }

        binding.buttonAdditional.setOnClickListener {
            findNavController().navigate(
                HomeNavGraphDirections.actionGraphHomeToGraphAdditional()
//                navOptions {
//                    popUpTo(R.id.navigation_home)
//                }
            )
        }

        binding.textCounter.text = args.counter.toString()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun NavController.findGraph(@IdRes graphId: Int): NavGraph {
        val node = graph.findNode(graphId)

        checkNotNull(node) { "Узел графа с переданным id не найден" }
        check(node is NavGraph) { "Найденный $node не является графом" }

        return node
    }
}