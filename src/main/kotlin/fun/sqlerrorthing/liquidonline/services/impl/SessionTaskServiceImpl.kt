package `fun`.sqlerrorthing.liquidonline.services.impl

import `fun`.sqlerrorthing.liquidonline.services.SessionTaskService
import `fun`.sqlerrorthing.liquidonline.session.UserSession
import `fun`.sqlerrorthing.liquidonline.ws.sessionTask.SessionTask
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Service
class SessionTaskServiceImpl(
    private val tasks: List<SessionTask>
): SessionTaskService {
    private val taskScheduler = ThreadPoolTaskScheduler().apply {
        poolSize = 4
        initialize()
    }

    private val sessionFutures = ConcurrentHashMap<String, List<ScheduledFuture<*>>>()

    override fun startSessionTasks(session: UserSession) {
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

    override fun stopSessionTasks(session: UserSession) {
        sessionFutures.remove(session.wsSession.id)?.forEach { it.cancel(true) }
    }
}