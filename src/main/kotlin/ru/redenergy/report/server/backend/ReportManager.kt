package ru.redenergy.report.server.backend

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.field.DataPersisterManager
import com.j256.ormlite.field.DatabaseFieldConfig
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.DatabaseTableConfig
import com.j256.ormlite.table.TableUtils
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.entity.TicketMessage
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.SyncTickets
import ru.redenergy.report.common.network.packet.UpdateAdminAccess
import ru.redenergy.report.server.QReportServer
import ru.redenergy.report.server.orm.JsonPersister
import ru.redenergy.vault.ForgeVault
import java.util.*

class ReportManager(val connectionSource: ConnectionSource) {

    val TICKETS_MANAGEMENT_PERMISSION_NODE = "qreport.tickets.access"

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

    public fun getTicketsByPlayer(player: String): MutableList<Ticket> =
        ticketDao.queryBuilder().where().eq("sender", player).query()

    public fun deleteTicket(ticket: Ticket) = ticketDao.delete(ticket)

    public fun handleNewTicket(text: String, reason: TicketReason, player: EntityPlayerMP) =
            newTicket(text, reason, player.commandSenderName)

    public fun newTicket(text: String, reason: TicketReason, sender: String) = addTicket(
            Ticket(status = TicketStatus.OPEN, sender = sender, reason = reason, messages = arrayListOf(TicketMessage(sender, text))))

    public fun handleSyncRequest(player: EntityPlayerMP) {
        NetworkHandler.instance.sendTo(UpdateAdminAccess(canAccessTicketManagement(player)), player)
        var tickets = if (canAccessTicketManagement(player)) getTickets()
                      else getTicketsByPlayer(player.commandSenderName)
        NetworkHandler.instance.sendTo(SyncTickets(tickets), player)
    }

    public fun handleAddMessage(ticketUid: UUID, message: String, player: EntityPlayerMP) {
        var ticket = ticketDao.queryForId(ticketUid)?: return
        if(canAccessTicket(ticket, player)){
            var ticketMessage = TicketMessage(player.commandSenderName, message)
            ticket.messages.add(ticketMessage)
            ticketDao.update(ticket)
        } else {
            player.addChatMessage(ChatComponentText("${EnumChatFormatting.RED}Ooops, you don't have access to ticket with id ${ticket.shortUid()}."))
        }
    }

    public fun handleUpdateTicketStatus(ticketUid: UUID, status: TicketStatus, player: EntityPlayerMP){
        var ticket = ticketDao.queryForId(ticketUid) ?: return
        if(canAccessTicket(ticket, player)) {
            ticket.status = status
            ticketDao.update(ticket)
        } else {
            player.addChatMessage(ChatComponentText("${EnumChatFormatting.RED}Ooops, you don't have access to ticket with id ${ticket.shortUid()}."))
        }
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

    /**
     * Checks if player have permission to answer/close this particular ticket
     */
    private fun canAccessTicket(ticket: Ticket, player: EntityPlayerMP): Boolean{
        return ticket.sender.equals(player.commandSenderName, true) || canAccessTicketManagement(player)
    }

    /**
     * Checks if player have permission to answer/close other players tickets
     */
    private fun canAccessTicketManagement(player: EntityPlayerMP): Boolean {
        if(MinecraftServer.getServer().isDedicatedServer){
            if(QReportServer.usePermission){
                return ForgeVault.getPermission()?.has(null as String, player.commandSenderName, TICKETS_MANAGEMENT_PERMISSION_NODE) ?: false
            } else {
                return isOp(player)
            }
        } else {
            return player.capabilities.isCreativeMode
        }

    }

    /**
     * Returns true if given player has operator permission
     */
    private fun isOp(player: EntityPlayerMP): Boolean = MinecraftServer.getServer().configurationManager
            .func_152603_m().func_152700_a(player.commandSenderName) != null

}