package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface SessionTaskService {
    fun startSessionTasks(session: UserSession)

    fun stopSessionTasks(session: UserSession)
}
