package com.example.minilivescore.presentation.ui.standing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.minilivescore.MainActivity
import com.example.minilivescore.R
import com.example.minilivescore.databinding.FragmentStandingBinding
import com.example.minilivescore.presentation.ui.matches.MatchesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StandingFragment : Fragment() {
    private var _binding : FragmentStandingBinding? = null
    private val binding get() = _binding!!
    private val viewModel by  activityViewModels<MatchesViewModel>()
    private val adapter: LeaguesStandingAdapter by lazy(LazyThreadSafetyMode.NONE) {
        LeaguesStandingAdapter(Glide.with(this))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentStandingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        observeViewModel()
        binding.swipeRefreshLayout.setOnRefreshListener {
            (activity as? MainActivity)?.let {
                when(it.binding.navViewMain.selectedItemId){
                    R.id.navigation_matches_football -> viewModel.getStandingLeagues("PL")
                    R.id.navigation_matches_basketball -> viewModel.getStandingLeagues("SA")
                    R.id.navigation_matches_american_football -> viewModel.getStandingLeagues("DA")
                }
            }
        }
    }
    private fun observeViewModel(){
        //note:Nhớ từng trường hợp của dữ liệu như loading, success, error, thiết lập với StateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.standingLeague.collect { standing ->
                val tableList = standing.data?.standings?.flatMap { it.table }
                binding.swipeRefreshLayout.isRefreshing = false
                if(standing.data?.standings?.isNotEmpty() == true){
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noEventsTextView.visibility =View.GONE
                    adapter.submitList(tableList)
                }else{
                    binding.recyclerView.visibility = View.GONE
                    binding.noEventsTextView.visibility = View.VISIBLE
                }
            }
        }
  /*      viewModel.standingLeague.observe(viewLifecycleOwner){standing ->
            val tableList = standing.standings.flatMap { it.table }
            binding.swipeRefreshLayout.isRefreshing = false
            if(standing.standings.isNotEmpty()){
                binding.recyclerView.visibility = View.VISIBLE
                binding.noEventsTextView.visibility =View.GONE
                adapter.submitList(tableList)
            }else{
                binding.recyclerView.visibility = View.GONE
                binding.noEventsTextView.visibility = View.VISIBLE
            }
        }*/
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            binding.recyclerView.visibility = View.GONE
            binding.noEventsTextView.visibility = View.VISIBLE
            binding.noEventsTextView.text = errorMessage
        }
    }
    private fun setupRecycleView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {

        @JvmStatic
        fun newInstance() = StandingFragment()
    }
}