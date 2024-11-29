package com.example.minilivescore.presentation.ui.matches

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.minilivescore.R
import com.example.minilivescore.data.model.football.LeagueMatches
import com.example.minilivescore.databinding.FragmentSportEventBinding
import com.example.minilivescore.extension.setOnDebounceClickListener
import com.example.minilivescore.presentation.ui.HomeFragmentDirections
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MatchesFragment : Fragment() {
    private var _binding: FragmentSportEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MatchesViewModel by activityViewModels()
    private val adapter: MatchesAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MatchesAdapter(Glide.with(this)){match ->
            navigateToDetailMatch(match)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSportEventBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        setupRecycleView()
        observeViewModel()
        chooseRoundListener()
        //fetchMatchesForCurrentLeague()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        binding.info.date.text = when(currentHour){
            in 0..11 -> ContextCompat.getString(requireContext(),R.string.morning)
            in 12..17 ->ContextCompat.getString(requireContext(),R.string.noon)
            in 18..24 ->ContextCompat.getString(requireContext(),R.string.night)

            else -> null
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            // Use the current league code when refreshing
            fetchMatchesForCurrentLeague()
        }
        Log.d("MatchesFragment","${viewModel.currentMatchday.value}")
    }

    private fun chooseRoundListener(){
        binding.info.leftButton.setOnDebounceClickListener {
            viewModel.decrementMatchday()
        }
        binding.info.rightButton.setOnDebounceClickListener {
            viewModel.incrementMatchday()
        }
    }
    private fun fetchMatchesForCurrentLeague() {
        viewModel.currentLeague.value.let { leagueCode ->
            Log.d("currentLeague","$leagueCode")
            viewModel.fetchMatches(leagueCode)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentLeague.collect { league ->
               fetchMatchesForCurrentLeague()
            }
        }
        //vòng đấu hiện tại
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedMatchday.collect { currentMatchday ->
                binding.info.noOfEvents.text = "Vòng $currentMatchday"
            }
        }
       /* //Cập nhật vòng đấu khác
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.matchday.collect { matchday ->
                matchday?.let {
                    binding.info.noOfEvents.text = "Vòng $it"
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentMatchday.collect{setRound ->
                setRound?.let {
                    currentRound = setRound
                }
            }
        }*/
        viewModel.matches.observe(viewLifecycleOwner){resource ->
            binding.swipeRefreshLayout.isRefreshing = false
            when(resource){
                is Resource.Success ->{
                    binding.indicator.visibility = View.GONE
                    resource.data?.matches?.let {
                        if(it.isEmpty()){
                            binding.noEventsTextView.visibility = View.VISIBLE
                        }else{
                            binding.noEventsTextView.visibility = View.GONE
                            adapter.submitList(it)
                        }
                    }
                }
                is Resource.Error ->{
                    binding.indicator.visibility =View.GONE
                    Toast.makeText(context,"Lỗi ${resource.message}",Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    binding.indicator.visibility = View.VISIBLE

                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.matchError.collect { error ->
                error?.let {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }

    }
    private fun setupRecycleView() {
        //  adapter = MatchesAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }
    //Truyền dữ liệu sang cho DetailFragment
    private fun navigateToDetailMatch(match: LeagueMatches.Matche){
      val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(match)

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {

        @JvmStatic
        fun newInstance() = MatchesFragment()


    }
}