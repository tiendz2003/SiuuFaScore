package com.example.minilivescore.presentation.ui.favorite.teammatches

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.minilivescore.R
import com.example.minilivescore.databinding.FragmentTeamMatchesBinding
import com.example.minilivescore.extension.loadImage
import com.example.minilivescore.presentation.ui.favorite.FavoriteViewModel
import com.example.minilivescore.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [TeamMatchesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TeamMatchesFragment : Fragment() {
    private var _binding: FragmentTeamMatchesBinding? = null
    private val binding get() = _binding!!
    private var teamId: String? = null
    private val viewModel by viewModels<FavoriteViewModel>()
    private val adapter:TeamMatchesAdapter by lazy(LazyThreadSafetyMode.NONE){
        TeamMatchesAdapter(Glide.with(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            teamId = it.getString(ARG_PARAM1)
        }
        Log.d("TeamId","$teamId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamMatchesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        teamId?.let { teamId->
            viewModel.getMatchesByID(teamId)
        }
        observeViewModel()


    }
    private fun setupRecycleView(){
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TeamMatchesFragment.adapter
        }
    }
    private fun observeViewModel() {
        // Coroutine sẽ chỉ chạy khi View của Fragment còn sống
        viewLifecycleOwner.lifecycleScope.launch {
           viewModel.matches.collect{resource ->
               when(resource){
                   is Resource.Loading -> {
                       binding.indicator.visibility = View.VISIBLE
                   }
                   is Resource.Success -> {
                       binding.indicator.visibility = View.GONE
                       resource.data?.let { data ->
                           adapter.submitList(data.matches.filter {
                               it.status == "TIMED" || it.status == "SCHEDULED"
                           })
                       }
                   }
                   is Resource.Error -> {
                       binding.indicator.visibility = View.GONE
                       Toast.makeText(requireContext(),resource.message,Toast.LENGTH_SHORT).show()
                   }
               }
           }
       }
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TeamMatchesFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val ARG_PARAM1 = "team_id"
        @JvmStatic
        fun newInstance(teamId: String) =
            TeamMatchesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, teamId)
                }
            }
    }
}