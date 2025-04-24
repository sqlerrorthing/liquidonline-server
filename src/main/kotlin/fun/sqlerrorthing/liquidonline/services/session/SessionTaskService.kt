package `fun`.sqlerrorthing.liquidonline.services.session

import `fun`.sqlerrorthing.liquidonline.session.UserSession

interface SessionTaskService {
    fun startSessionTasks(session: UserSession)

    fun stopSessionTasks(session: UserSession)
}
