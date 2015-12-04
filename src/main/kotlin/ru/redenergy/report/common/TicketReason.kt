package ru.redenergy.report.common

import net.minecraft.client.resources.I18n
import net.minecraft.util.StatCollector

/**
 * Reasons of tickets
 */
enum class TicketReason(val translateKey: String) {

    /**
     * Some game error
     */
    BUG("ticket.reason.bug"),
    /**
     * Bad player behavior
     */
    GRIEFING("ticket.reason.grief"),
    /**
     * Suggestion of improvement
     */
    SUGGESTION("ticket.reason.suggest"),
    /**
     * Any other thing which don't fits into any of previous reasons
     */
    OTHER("ticket.reason.other");

    public fun getKey(): String = translateKey

    public fun getTranslation(): String = StatCollector.translateToLocal(translateKey)

    override fun toString() = getTranslation()
}