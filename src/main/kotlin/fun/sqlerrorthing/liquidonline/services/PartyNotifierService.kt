package `fun`.sqlerrorthing.liquidonline.services

import `fun`.sqlerrorthing.liquidonline.packets.s2c.party.S2CPartyKicked
import `fun`.sqlerrorthing.liquidonline.session.Party
import `fun`.sqlerrorthing.liquidonline.session.PartyMember

interface PartyNotifierService {
    /**
     * Уведомляет всех участников пати
     * (не включая того, кто вошел в пати)
     *
     * О новом игроке
     */
    fun notifyPartyMemberJoined(
        party: Party,
        joinedMember: PartyMember
    )

    /**
     * Игрок вышел с пати.
     * Отправляет всем участникам пати о его выходе
     */
    fun notifyPartyMemberLeaved(
        party: Party,
        leavedMember: PartyMember
    )

    /**
     * Отправляет всем тем кто был приглашен в пати
     * о том что инвайт отзывается
     *
     * не удаляет инвайт, только уведомляет
     */
    fun notifyRevokedAllPartyInvites(
        party: Party
    )

    /**
     * Отправляет всем участникам пати
     * информацию о том что участник в пати
     * поменялся владелец
     */
    fun notifyPartyOwnerTransferred(
        party: Party,
        newOwner: PartyMember
    )

    /**
     * Отправляет пакет участнику пати о том что
     * его выгнали из пати
     *
     * не кикает из пати, только уведомляет
     */
    fun notifyKickedMember(
        member: PartyMember,
        reason: S2CPartyKicked.Reason
    )
}