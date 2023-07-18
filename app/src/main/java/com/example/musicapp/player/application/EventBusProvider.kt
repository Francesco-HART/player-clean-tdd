package com.example.musicapp.v2.player.application

import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance

abstract class EventBusProvider {
    private val _events = MutableSharedFlow<Any>()
    var subscribers: Int = 0
    val events = _events.asSharedFlow()

    open suspend fun publish(event: Any) {
        _events.emit(event)
    }

    // inline -> copy for every call and not recall function
    // reified -> use generics in inline function, without reified, we can't  verify  execution type of T
    // crossinline -> indique que la lambda onEvent ne doit pas utiliser de return non local,
    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        subscribers += 1
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }

    fun removeAllSubscription() {
        _events.resetReplayCache()
    }
}


class AppEvenBus : EventBusProvider()