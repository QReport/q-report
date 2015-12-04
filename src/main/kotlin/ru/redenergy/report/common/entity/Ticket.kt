package ru.redenergy.report.common.entity

import ru.redenergy.report.common.TicketReason
import java.util.*

/**
 * Represents report ticket
 */
class Ticket {

    /**
     * Primary key and identifier of ticket
     */
    var uid: UUID
        public get
        private set

    /**
     * Original ticket initiator
     */
    var sender: String
        public get
        private set

    /**
     * Original reason of ticket
     */
    var reason: TicketReason
        public get
        private set

    /**
     * Message history <br>
     * Note: in database this field will be persisted as json string, check out [ru.redenergy.report.server.orm.JsonPersister]
     */
    var messages: MutableList<TicketMessage>
        public get
        private set

    /**
     * Empty constructor requested by ORMLite
     */
    private constructor(){
        this.uid = UUID.randomUUID()
        this.sender = "unknown"
        this.reason = TicketReason.OTHER
        this.messages = arrayListOf()
    }

    /**
     * @param uid - identifier of a ticket, if not provided will be generated randomly
     * @param sender - original sender of a ticket
     * @param reason - reason of a ticket
     * @param messages - messages in ticket
     */
    constructor(uid: UUID = UUID.randomUUID(), sender: String, reason: TicketReason, messages: MutableList<TicketMessage>){
        this.uid = uid
        this.sender = sender
        this.reason = reason
        this.messages = messages
    }

    /**
     * A short representation of uuid <br>
     * In general, it just returns the first 8 symbols of uuid <br>
     * Should be used as human readable uuid, because full uuid is too long to remember
     */
    public fun shortUid(): String = this.uid.toString().substring(0, 8)

}