package ru.redenergy.report.server.backend.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

/**
 * Represents report ticket in database
 */
@DatabaseTable(tableName = "reports")
class Report {

    /**
     * Primary key and identifier of ticket
     */
    @DatabaseField(id = true)
    var uid: UUID
        public get
        private set

    /**
     * Ticket sender name
     */
    @DatabaseField
    var sender: String
        public get
        private set

    /**
     * Ticket text
     */
    @DatabaseField
    var text: String
        public get
        private set

    //for ormlite
    private constructor(){
        this.uid = UUID.randomUUID()
        this.sender = "unknown"
        this.text = "empty"
    }

    /**
     * @param uid - if not specified will be generated randomly
     * @param sender - sender of ticket
     * @param text - ticket text
     */
    constructor(uid: UUID = UUID.randomUUID(), sender: String, text: String){
        this.uid = uid
        this.sender = sender
        this.text = text
    }
}