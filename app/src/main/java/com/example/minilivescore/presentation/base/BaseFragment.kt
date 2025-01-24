package com.example.minilivescore.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB:ViewBinding>(
    private val inflater: (LayoutInflater,ViewGroup?,Boolean)->VB
):Fragment() {
    private var _binding:VB ?= null
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater(inflater,container,false).also { _binding = it }.root
    }
    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}