package ru.redenergy.report.server.backend

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.field.DataPersisterManager
import com.j256.ormlite.field.DatabaseFieldConfig
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.DatabaseTableConfig
import com.j256.ormlite.table.TableUtils
import net.minecraft.entity.player.EntityPlayerMP
import org.apache.commons.lang3.reflect.FieldUtils
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.entity.TicketMessage
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.SyncTickets
import ru.redenergy.report.server.orm.JsonPersister
import java.util.*
import kotlin.reflect.declaredFunctions

class ReportManager(val connectionSource: ConnectionSource) {

    init{
        DataPersisterManager.registerDataPersisters(JsonPersister.getSingleton())
    }

    val ticketConfig = configureTicketEntity()
    var ticketDao = DaoManager.createDao<Dao<Ticket, UUID>, Ticket>(connectionSource, ticketConfig)

    public fun initialize() {
        TableUtils.createTableIfNotExists(connectionSource, Ticket::class.java)
    }

    public fun addTicket(ticket: Ticket) = ticketDao.create(ticket)

    public fun getTickets(): MutableList<Ticket> = ticketDao.queryForAll()

    public fun deleteTicket(ticket: Ticket) = ticketDao.delete(ticket)

    public fun handleNewTicket(text: String, reason: TicketReason, player: EntityPlayerMP) = newTicket(text, reason, player.commandSenderName)

    public fun newTicket(text: String, reason: TicketReason, sender: String) = addTicket(
            Ticket(status = TicketStatus.OPEN, sender = sender, reason = reason, messages = arrayListOf(TicketMessage(sender, text))))

    public fun handleSyncRequest(player: EntityPlayerMP) = NetworkHandler.instance.sendTo(SyncTickets(getTickets()), player)

    public fun handleAddMessage(ticketUid: UUID, message: String, player: EntityPlayerMP) {
        var ticket = ticketDao.queryForId(ticketUid)?: return
        var ticketMessage = TicketMessage(player.commandSenderName, message)
        ticket.messages.add(ticketMessage)
        ticketDao.update(ticket)
    }

    private fun configureTicketEntity(): DatabaseTableConfig<Ticket> {
        return DatabaseTableConfig(Ticket::class.java, arrayListOf<DatabaseFieldConfig>().apply {
            add(DatabaseFieldConfig("uid").apply { isId = true })
            add(DatabaseFieldConfig("status"))
            add(DatabaseFieldConfig("sender"))
            add(DatabaseFieldConfig("reason"))
            add(DatabaseFieldConfig("messages").apply { persisterClass = JsonPersister::class.java })
        }).apply { tableName = "tickets" }
    }
}