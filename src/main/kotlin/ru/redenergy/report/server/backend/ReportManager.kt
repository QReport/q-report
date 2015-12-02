package ru.redenergy.report.server.backend

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.field.DatabaseFieldConfig
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.DatabaseTableConfig
import com.j256.ormlite.table.TableUtils
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.SyncTickets
import java.util.*

class ReportManager(val connectionSource: ConnectionSource) {

    val ticketConfig = configureTicketEntity()
    var ticketDao = DaoManager.createDao<Dao<Ticket, UUID>, Ticket>(connectionSource, ticketConfig)

    public fun initialize(){
        TableUtils.createTableIfNotExists(connectionSource, Ticket::class.java)
    }

    public fun addTicket(ticket: Ticket) = ticketDao.create(ticket)

    public fun getTickets(): MutableList<Ticket> = ticketDao.queryForAll()

    public fun deleteTicket(ticket: Ticket) = ticketDao.delete(ticket)

    public fun handleNewTicket(text: String, reason: TicketReason, player: EntityPlayerMP) = newTicket(text, reason, player.commandSenderName)

    public fun newTicket(text: String, reason: TicketReason, sender: String) = addTicket(Ticket(sender = sender, text = text, reason = reason))

    public fun handleSyncRequest(player: EntityPlayerMP) = NetworkHandler.instance.sendTo(SyncTickets(getTickets()), player)

    private fun configureTicketEntity() : DatabaseTableConfig<Ticket> {
        return DatabaseTableConfig(Ticket::class.java, arrayListOf<DatabaseFieldConfig>().apply {
            add(DatabaseFieldConfig("uid").apply { isId = true })
            add(DatabaseFieldConfig("sender"))
            add(DatabaseFieldConfig("text"))
            add(DatabaseFieldConfig("reason"))
            add(DatabaseFieldConfig("timestamp"))
        }).apply { tableName = "tickets" }
    }
}