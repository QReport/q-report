package ru.redenergy.report.common.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
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
     * Ticket sender name
     */
    var sender: String
        public get
        private set

    /**
     * Ticket text
     */
    var text: String
        public get
        private set

    var reason: TicketReason
        public get
        private set

    var timestamp: Long
        public get
        private set

    //for ormlite
    private constructor(){
        this.uid = UUID.randomUUID()
        this.sender = "unknown"
        this.text = "empty"
        this.timestamp = -1
        this.reason = TicketReason.OTHER
    }

    /**
     * @param sender - sender of ticket
     * @param text - ticket text
     * @param uid - if not specified will be generated randomly
     * @param time - timestamp of ticket sending
     * @param reason - reason of a ticket
     */
    constructor(sender: String, text: String, uid: UUID = UUID.randomUUID(), time: Long = System.currentTimeMillis(), reason: TicketReason = TicketReason.OTHER){
        this.uid = uid
        this.sender = sender
        this.text = text
        this.timestamp = time
        this.reason = reason
    }
}