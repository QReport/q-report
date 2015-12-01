package ru.redenergy.report.common.network

import net.minecraft.client.resources.I18n

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
    GREFIENG("ticket.reason.grief"),
    /**
     * Suggestion of improvement
     */
    SUGGESTION("ticket.reason.suggest"),
    /**
     * Any other thing which don't fits into any of previous reasons
     */
    OTHER("ticket.reason.other");

    public fun getKey(): String = translateKey

    public fun getTranslation(): String = I18n.format(translateKey)

    override fun toString() = getTranslation()
}