package com.example.minilivescore.presentation.ui.detailmatch.comment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minilivescore.databinding.FragmentCmtBottomSheetBinding
import com.example.minilivescore.extension.loadImage
import com.example.minilivescore.presentation.ui.detailmatch.MatchDetailViewModel
import com.example.minilivescore.utils.Resource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BottomSheetFragment: BottomSheetDialogFragment() {
    private var _binding:FragmentCmtBottomSheetBinding ?= null
    val binding get() = _binding!!
    //Lay match id thong qua bundle
    private val matchId:String by lazy {
        arguments?.getString("matchId")?:"0"
    }
    @Inject lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: MatchDetailViewModel by viewModels() // Inject ViewModel
    private lateinit var adapter: CommentAdapter
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {dialogInterface ->
            val bottomSheetDialog= dialogInterface as BottomSheetDialog
            val bottomSheetInternal = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheetInternal?.let {sheet->
                val behavior = BottomSheetBehavior.from(sheet)
                val layoutParams = sheet.layoutParams
                //chiếm 80% màn hình
                layoutParams.height = (resources.displayMetrics.heightPixels*0.76).toInt()
                sheet.layoutParams = layoutParams
               // behavior.isHideable = false
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                // Ngăn không cho bottom sheet kéo đóng hoàn toàn
                behavior.skipCollapsed = true
            }


        }
        //Ngăn chuyển màu
        dialog.window?.setDimAmount(0f)
        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCmtBottomSheetBinding.inflate(inflater,container,false)
        viewModel.getComments(matchId)
        adapter = CommentAdapter()
        setupRecycleView()
        observeComment()
        setupClickListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    private fun setupRecycleView(){
        binding.commentRecyclerView.adapter = adapter
        binding.commentRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }
    private fun setupClickListener(){
        binding.sendButton.setOnClickListener {
            val cmtText = binding.commentInput.text.toString()
            if(cmtText.isNotBlank()){
                viewModel.postComment(matchId,cmtText)
            }
            binding.commentInput.text.clear()
        }
        val userImg = firebaseAuth.currentUser?.photoUrl.toString()
        binding.userAvatar.loadImage(userImg)
    }
    private fun observeComment() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cmt.collectLatest { cmts ->
                when(cmts){
                    is Resource.Error -> Log.d("Lỗi","Không thể đăng bình luận")
                    is Resource.Loading -> Log.d("Đang tải","Loading...")
                    is Resource.Success -> {
                        cmts.data?.let { adapter.submitList(it){
                            if(it.isNotEmpty()){
                                binding.commentRecyclerView.smoothScrollToPosition(it.size-1)
                            }
                        } }
                        Log.d(
                            "Comments",
                            "Submitting ${cmts.data?.size} items to adapter"
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}