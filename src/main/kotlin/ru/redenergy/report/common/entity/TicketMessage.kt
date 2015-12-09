package ru.redenergy.report.common.entity

import java.util.*

/**
 * Entry of ticket message history
 *
 * @constructor
 * @param sender - message sender
 * @param text - message text
 * @param uid - identifier of a message, if not provided will be generated randomly
 * @param timestamp - message send time, if not provided will be taken from system
 */
data class TicketMessage(var sender: String, var text: String, var uid: UUID = UUID.randomUUID(), var timestamp: Long = System.currentTimeMillis()) {

    /**
     * Empty constructor requested by ORMLite
     */
    constructor(): this("unknown", "", UUID.randomUUID(), 0L)

}