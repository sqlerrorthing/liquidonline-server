package `fun`.sqlerrorthing.liquidonline.ws.sessionTask

import `fun`.sqlerrorthing.liquidonline.session.UserSession
import java.time.Duration

abstract class SessionTask(
    val initialDelay: Duration,
    val period: Duration
) {
    constructor(period: Duration) : this(period, period)

    abstract fun run(session: UserSession)
}