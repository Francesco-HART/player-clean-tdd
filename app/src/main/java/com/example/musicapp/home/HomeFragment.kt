package com.example.musicapp.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentHomeBinding
import com.example.musicapp.databinding.FragmentLoginBinding
import com.example.musicapp.home.HomeViewModel
import com.example.musicapp.login.LoginViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val model: HomeViewModel by viewModels()
    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}