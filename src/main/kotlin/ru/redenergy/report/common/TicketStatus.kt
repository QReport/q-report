package ru.redenergy.report.common

import net.minecraft.util.StatCollector
import java.awt.Color

/**
 * Represents status of ticket
 */
enum class TicketStatus(val translateKey: String, val color: Color) {

    /**
     * The ticket has just been received and have not been reviewed
     */
    OPEN("ticket.status.open", Color(255, 255, 52)),
    /**
     * The ticket has been reviewed by moderator and currently in progress of clarification/fixing
     */
    IN_PROGRESS("ticket.status.inprogress", Color(54, 182, 255)),
    /**
     * The ticket is closed. The problem has been fixed/bad player has been punished
     */
    CLOSED("ticket.status.closed", Color(29, 232, 88));

    public fun getTranslation(): String = StatCollector.translateToLocal(translateKey)

    override fun toString() = getTranslation()

}