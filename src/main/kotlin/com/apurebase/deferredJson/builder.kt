package com.apurebase.deferredJson

import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

public fun CoroutineScope.deferredJsonBuilder(
    timeout: Long? = null,
    propagateables: List<() -> CoroutineContext.Element> = listOf(),
    init: suspend DeferredJsonMap.() -> Unit,

    ): Deferred<JsonObject> = async {
    val block: suspend CoroutineScope.() -> JsonObject = {
        ThreadLocal<String>().asContextElement()
        try {
            val builder = DeferredJsonMap(coroutineContext, propagateables)
            builder.init()
            builder.awaitAndBuild()
        } catch (e: CancellationException) {
            throw e.cause ?: e
        }
    }

    timeout?.let {
        withTimeout(it) {
            block()
        }
    } ?: block()
}
