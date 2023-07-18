package com.example.musicapp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

sealed class HomeViewModelState(
    open val errorMessage: String = "",
    open val loginButtonEnabled: Boolean = false
) {

    object Success : HomeViewModelState()
    data class UpdateLogin(override val loginButtonEnabled: Boolean) :
        HomeViewModelState("", loginButtonEnabled)

    data class Failure(override val errorMessage: String) : HomeViewModelState(errorMessage, true)
}


class HomeViewModel : ViewModel() {

    private val state = MutableLiveData<HomeViewModelState>()
    fun getState(): LiveData<HomeViewModelState> = state

    fun login(username: String, password: String) {
        if (validateLogin(username, password)) {
            state.value = HomeViewModelState.Success
        } else {
            state.value = HomeViewModelState.Failure("Wrong username / password!")
        }
    }

    fun updateLogin(username: String, password: String) {
        val buttonEnabled = username.isNotBlank() && password.isNotBlank()
        state.value = HomeViewModelState.UpdateLogin(buttonEnabled)
    }
}

fun validateLogin(username: String, password: String): Boolean =
    username == "kotlin" && password == "rocks"