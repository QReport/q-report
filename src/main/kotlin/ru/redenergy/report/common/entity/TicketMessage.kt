package ru.redenergy.report.common.entity

import java.util.*

/**
 * Entry of ticket message history
 */
class TicketMessage {

    /**
     * Unical identifier of message
     */
    var uid: UUID
        public get
        private set

    /**
     * Sender of message
     */
    var sender: String
        public get
        private set

    /**
     * Text of a message
     */
    var text: String
        public get
        private set

    /**
     * Time of message sending
     */
    var timestamp: Long
        public get
        private set

    /**
     * Empty constructor requested by ORMLite
     */
    constructor(){
        this.uid = UUID.randomUUID()
        this.sender = "unknown"
        this.text = ""
        this.timestamp = 0L
    }

    /**
     * Constructs new message
     * @param sender - message sender
     * @param text - message text
     * @param uid - identifier of a message, if not provided will be generated randomly
     * @param timestamp - message send time, if not provided will be taken from system
     */
    constructor(sender: String, text: String, uid: UUID = UUID.randomUUID(), timestamp: Long = System.currentTimeMillis()){
        this.sender = sender
        this.uid = uid
        this.text = text
        this.timestamp = timestamp
    }
}