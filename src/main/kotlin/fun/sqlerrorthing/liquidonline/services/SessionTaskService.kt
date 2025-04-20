package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.sessionTask.SessionTask
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Service
class SessionTaskService(
    private val tasks: List<SessionTask>
) {
    private val taskScheduler = ThreadPoolTaskScheduler().apply {
        poolSize = 4
        initialize()
    }

    private val sessionFutures = ConcurrentHashMap<String, List<ScheduledFuture<*>>>()

    fun startSessionTasks(session: UserSession) {
        val wsSessionId = session.wsSession.id

        val futures = tasks.map { task ->
            val runnable = Runnable {
                if (session.wsSession.isOpen) {
                    task.run(session)
                } else {
                    stopSessionTasks(session)
                }
            }

            val firstRunTime = Instant.now().plus(task.initialDelay)
            taskScheduler.scheduleAtFixedRate(runnable, firstRunTime, task.period)
        }

        sessionFutures[wsSessionId] = futures
    }

    fun stopSessionTasks(session: UserSession) {
        sessionFutures.remove(session.wsSession.id)?.forEach { it.cancel(true) }
    }
}