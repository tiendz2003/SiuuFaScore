package com.example.minilivescore.ui.searchteam

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.minilivescore.R
import com.example.minilivescore.SearchActivity
import com.example.minilivescore.data.repository.TeamViewModelFactory
import com.example.minilivescore.databinding.FragmentSearchTeamsBinding
import com.example.minilivescore.ui.matches.MatchesViewModel
import com.example.minilivescore.utils.Resource
import kotlinx.coroutines.launch


class SearchTeamsFragment : Fragment() {
    private var _binding : FragmentSearchTeamsBinding? = null
    private val binding get() = _binding!!
    // Sử dụng activityViewModels() để share ViewModel với Activity
    val viewModel: SearchTeamViewModel by activityViewModels {
        TeamViewModelFactory(
            (requireActivity() as SearchActivity).repository
        )
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        SearchTeamAdapter(Glide.with(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchTeamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        observeResult()
    }
    private fun setupRecycleView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SearchTeamsFragment.adapter
            post {
                Log.d("SearchTeamsFragment", "RecyclerView size: ${width}x${height}")
            }
        }
    }
    private fun observeResult(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.collect{result ->
                Log.d("SearchTeamsFragment","$result")
                when(result){
                    is Resource.Success ->{
                        binding.progressBar.isVisible = false
                        binding.errorText.isVisible = false
                        binding.recyclerView.isVisible = true
                        result.data?.forEach { team ->
                            Log.d("SearchTeamsFragment", "Team in list: ${team.name}")
                        }
                        Log.d("SearchTeamsFragment", "Submitting ${result.data?.size} items to adapter")
                        adapter.submitList(result.data)
                    }
                    is Resource.Error -> {
                        binding.progressBar.isVisible = false
                        binding.recyclerView.isVisible = false
                        binding.errorText.isVisible = true
                        binding.errorText.text = result.message
                    }
                    is Resource.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.recyclerView.isVisible = false
                        binding.errorText.isVisible = false
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchTeamsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = SearchTeamsFragment()
    }
}