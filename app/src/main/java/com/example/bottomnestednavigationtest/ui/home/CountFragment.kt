package com.example.bottomnestednavigationtest.ui.home

import android.annotation.*
import android.os.*
import android.view.*
import androidx.annotation.*
import androidx.fragment.app.*
import androidx.navigation.*
import androidx.navigation.fragment.*
import androidx.navigation.ui.*
import com.example.bottomnestednavigationtest.*
import com.example.bottomnestednavigationtest.databinding.*

class CountFragment : Fragment() {

    private var _binding: FragmentCountBinding? = null
    private val binding get() = _binding!!
    private val args: CountFragmentArgs by navArgs()

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCountBinding.inflate(inflater, container, false)
        val root = binding.root

        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(
                CountFragmentDirections.actionNavigationCountSelf(args.counter + 1)
            )
        }

        binding.buttonAdditional.setOnClickListener {
            findNavController().navigate(
                HomeNavGraphDirections.actionGraphHomeToGraphAdditional("From count fragment")
            )
        }

        binding.textCounter.text = args.counter.toString()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as MainActivity

        binding.toolbar.setupWithNavController(activity.navController, activity.appBarConfiguration)
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