package com.example.musicapp.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import com.example.musicapp.testObserver

class ProfileViewModelTest {
    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `empty cache value yield state UpdatingCacheFailure with specific message`(){
        val model = ProfileViewModel()
        val observer = model.getState().testObserver()

        model.updateCacheSize("")

        Assert.assertEquals(
            listOf(
                ProfileViewModelState.UpdatingCacheFailure("Cache value is empty")
            ),
            observer.observedValues
        )
    }
    @Test
    fun `too small cache value yield state UpdatingCacheFailure`(){
        val model = ProfileViewModel()
        val observer = model.getState().testObserver()

        model.updateCacheSize("1")

        Assert.assertEquals(
            listOf(
                ProfileViewModelState.UpdatingCacheFailure("Required a minimum cache size of ${ProfileViewModel.MIN_CACHE_SIZE}Mo")
            ),
            observer.observedValues
        )
    }
    @Test
    fun `too high cache value yield state UpdatingCacheFailure`(){
        val model = ProfileViewModel()
        val observer = model.getState().testObserver()

        model.updateCacheSize("1000000000")

        Assert.assertEquals(
            listOf(
                ProfileViewModelState.UpdatingCacheFailure("Required a maximum cache size of ${ProfileViewModel.MAX_CACHE_SIZE}Mo")
            ),
            observer.observedValues
        )
    }

    @Test
    fun `cache size between min and max value yield state UpdatingCacheSuccess`(){
        val model = ProfileViewModel()
        val observer = model.getState().testObserver()
        val expectedRes:MutableList<ProfileViewModelState> = mutableListOf()

        val typedValue = listOf(
            "",
            "5",
            ProfileViewModel.MIN_CACHE_SIZE.toString(),
            "50",
            "500",
            "5000",
            ProfileViewModel.MAX_CACHE_SIZE.toString()
        )
        typedValue.forEach {
            model.updateCacheSize(it)
            expectedRes.add(when{
                it.isEmpty() -> ProfileViewModelState.UpdatingCacheFailure("Cache value is empty")
                it.toInt()<ProfileViewModel.MIN_CACHE_SIZE -> ProfileViewModelState.UpdatingCacheFailure("Required a minimum cache size of ${ProfileViewModel.MIN_CACHE_SIZE}Mo")
                it.toInt()>ProfileViewModel.MAX_CACHE_SIZE -> ProfileViewModelState.UpdatingCacheFailure("Required a maximum cache size of ${ProfileViewModel.MAX_CACHE_SIZE}Mo")
                else -> ProfileViewModelState.UpdatingCacheSuccess
            })
        }

        Assert.assertEquals(
            expectedRes,
            observer.observedValues
        )
    }

}