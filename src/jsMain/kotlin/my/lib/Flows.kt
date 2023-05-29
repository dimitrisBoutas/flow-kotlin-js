package my.lib

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

@JsExport
interface Disconnector {
    fun disconnect()
}

internal class FlowDisconnector(
    private val job: Job
) : Disconnector {

    override fun disconnect() = job.cancel()
}

@JsExport
interface FlowConnector<T> {
    fun connect(subscriber: (value: T) -> Unit): Disconnector
}

internal class DefaultFlowConnector<T>(
    private val flow: Flow<T>
) : FlowConnector<T> {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun connect(subscriber: (value: T) -> Unit): Disconnector {

        val flowCollectionJob = scope.launch {
            flow.collect {
                subscriber(it)
            }
        }

        return FlowDisconnector(flowCollectionJob)
    }
}

@JsExport
class FlowManager {

    private val _numbersFlow: Flow<Int> = flow {
        while (true) {
            val next = Random.nextInt()
            emit(next).also {
                println("produced value $next")
            }
            delay(1000)
        }
    }

   val numbersFlow: FlowConnector<Int>
        get() = flowConnector(_numbersFlow)
}

fun <T> flowConnector(flow: Flow<T>): FlowConnector<T> = DefaultFlowConnector(flow)
