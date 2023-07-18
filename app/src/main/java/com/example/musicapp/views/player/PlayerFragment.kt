package com.example.musicapp.v2.views.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.musicapp.databinding.FragmentPlayerBinding
import com.example.musicapp.player.PlayerViewModel
import com.example.musicapp.player.PlayerViewModelState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding
    private val model: PlayerViewModel by viewModels { PlayerViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.getState().observe(viewLifecycleOwner, { updateUi(it!!) })
        initViewModel()
    }

    private fun initViewModel() {
        GlobalScope.launch {
            model.initViewModel()
        }
    }

    private fun updateUi(state: PlayerViewModelState) {
        return when (state) {
            is PlayerViewModelState.Success -> {
                model.setMediaPlayer(state.player)
            }

            is PlayerViewModelState.Loading -> {
                model.setMediaPlayer(state.player)
            }

            is PlayerViewModelState.Failed -> {
                model.setMediaPlayer(state.player)
            }

            is PlayerViewModelState.NeverPlayed -> {
                model.initMediaPlayer()
            }
        }
    }
}