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
import ru.redenergy.report.common.Stats
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

    init{
        DataPersisterManager.registerDataPersisters(JsonPersister.getSingleton())
    }

    /**
     * ORM configuration for Ticket.class
     */
    val ticketConfig = configureTicketEntity()
    /**
     * ORM Dao for Ticket.class
     */
    var ticketDao = DaoManager.createDao<Dao<Ticket, UUID>, Ticket>(connectionSource, ticketConfig)

    /**
     * Contains tickets from last database query <br>
     * May be empty if no database queries has been performed
     */
    var cachedTickets: MutableList<Ticket> = arrayListOf()

    public fun initialize() {
        TableUtils.createTableIfNotExists(connectionSource, Ticket::class.java)
    }

    /**
     * Stores ticket in database
     */
    public fun addTicket(ticket: Ticket) = ticketDao.create(ticket)

    /**
     * Deletes given ticket from database
     */
    public fun deleteTicket(ticket: Ticket) = ticketDao.delete(ticket)

    /**
     * Queries all tickets from database <br>
     * Replaces tickets cache with obtained from database <br>
     * Returns cache
     */
    public fun getTickets(): MutableList<Ticket> {
        cachedTickets.clear()
        cachedTickets.addAll(ticketDao.queryForAll())
        return cachedTickets
    }

    /**
     * Returns all tickets sent by player with the given name
     */
    public fun getTicketsByPlayer(player: String): MutableList<Ticket> =
        ticketDao.queryBuilder().where().eq("sender", player).query()

    /**
     * Creates new ticket and stores it's in database
     * @parama text - text for a first message
     * @param reason - reason of a ticket
     * @param sender - name of a sender
     */
    public fun newTicket(text: String, reason: TicketReason, sender: String) = addTicket(
            Ticket(status = TicketStatus.OPEN, sender = sender, reason = reason, messages = arrayListOf(TicketMessage(sender, text))))

    public fun gatherStats(): Stats{
        val tickets = getTickets()
        val countTickets = countTickets(tickets)
        val activeUsers = getActiveUsers(tickets, 5)
        val averageResponseTime = countAverageResponseTime(tickets)
        return Stats(countTickets, activeUsers, averageResponseTime)
    }

    /**
     * Counts tickets in each category and returns map (Category (TicketReasons) : Amount of tickets (Int))
     */
    private fun countTickets(tickets: MutableList<Ticket>): MutableMap<TicketReason, Int>{
        val ticketsStats = hashMapOf<TicketReason, Int>()
        for(reason in TicketReason.values){
            ticketsStats.put(reason, tickets.filter { it.reason.equals(reason) } .count())
        }
        return ticketsStats
    }

    /**
     * Returns map with users who send most of tickets (User Name: Amount of Tickets)
     */
    public fun getActiveUsers(tickets: MutableList<Ticket>, amount: Int): Map<String, Int>{
        val userList = arrayListOf(*tickets.map { it.sender } .toTypedArray())
        val userStats = linkedMapOf(*userList.
                map { user -> Pair(user, tickets.filter { it.sender == user } .count()) }
                .toTypedArray())
        val sortedStats = userStats.toList().sortedBy { it.second } .reversed()
        val limiter = Math.min(amount - 1, sortedStats.size - 1)
        return mapOf(*sortedStats.toTypedArray().sliceArray(0..limiter))
    }

    /**
     * Returns average response time between first original sender's message and first response of administrator <br>
     * Will return -1 if no responses registered
     */
    public fun countAverageResponseTime(tickets: MutableList<Ticket>): Long{
        val ticketsWithAnswer = tickets.filter { it.messages.size > 1 }
        val lotOfResponses = ticketsWithAnswer.map { ticket ->
            val originalSenderMessage = ticket.messages[0]
            val administratorFirstMessage = ticket.messages.filter { it.sender != ticket.sender } [0]
            administratorFirstMessage.timestamp - originalSenderMessage.timestamp
        }
        val result = lotOfResponses.sum() / Math.max(1, lotOfResponses.size)
        if(result == 0L){
            return -1L
        }
        return result
    }

    /**
     * Returns ORM configuration for Ticket.class
     */
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
            if(QReportServer.checkPermission){
                return ForgeVault.getPermission()?.has(null as String?, player.commandSenderName, QReportServer.permissionNode) ?: false
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

    /**
     * Returns list with names of users who sent messages to given ticket
     */
    private fun getParticipants(ticket: Ticket): MutableList<String>{
        return hashSetOf(*ticket.messages.map { it.sender }.toTypedArray()).toArrayList()
    }

    /**
     * Sends message to all players with the names in the list <br>
     * If any of players is offline he won't receive message
     */
    private fun notifyUsers(users: MutableList<String>, message: String){
        for(user in users){
            var player = MinecraftServer.getServer().configurationManager.func_152612_a(user) ?: continue
            player.addChatMessage(ChatComponentText(message))
        }
    }

    public fun handleUpdateTicketStatus(ticketUid: UUID, status: TicketStatus, player: EntityPlayerMP){
        var ticket = ticketDao.queryForId(ticketUid) ?: return
        if(canAccessTicket(ticket, player)) {
            ticket.status = status
            ticketDao.update(ticket)
        } else {
            player.addChatMessage(ChatComponentText("${EnumChatFormatting.RED}Ooops, you don't have access to ticket with id ${ticket.shortUid}."))
        }
    }

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
            notifyUsers(getParticipants(ticket).apply{remove(player.commandSenderName)},
                    "${EnumChatFormatting.GREEN}${player.displayName} added new message to the ticket ${ticket.shortUid}")
        } else {
            player.addChatMessage(ChatComponentText("${EnumChatFormatting.RED}Ooops, you don't have access to ticket with id ${ticket.shortUid}."))
        }
    }

    public fun handleNewTicket(text: String, reason: TicketReason, player: EntityPlayerMP) =
            newTicket(text, reason, player.commandSenderName)
}