package com.example.musicapp.v2.player.infra

import com.example.musicapp.v2.player.application.EventBusProvider
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance

class StubEventBus : EventBusProvider() {
    var isPublishCalled = false
    var haveSubscribed = false

    var value: Any? = null
    var sendEvents = mutableListOf<Any>()


    override suspend fun publish(emittedValue: Any) {

        isPublishCalled = true
        value = emittedValue
        sendEvents.add(emittedValue)
        super.publish(emittedValue)
    }
}