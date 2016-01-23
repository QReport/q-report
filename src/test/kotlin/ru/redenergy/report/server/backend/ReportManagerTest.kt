package ru.redenergy.report.server.backend

import com.j256.ormlite.dao.BaseDaoImpl
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.DatabaseTableConfig
import com.j256.ormlite.table.TableUtils
import junit.framework.TestSuite
import org.apache.commons.lang3.reflect.FieldUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.redenergy.report.common.entity.Ticket
import java.io.File
import org.junit.Assert.*
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.entity.TicketMessage
import java.util.*

val path = "jdbc:sqlite:${File("").absolutePath}/reports-test.sqlite"

class ReportManagerTest {

    val reportManager = ReportManager(JdbcConnectionSource(path))

    @Before
    fun testInitialize() {
        reportManager.initialize()
    }

    @Test
    fun testGetActiveUsers(){
        val tickets = arrayListOf<Ticket>();

        //create a list with 15 tickets per 5 users
        for((amount, user) in arrayOf("First", "Second", "Third", "Fourth", "Fifth", "Sixth").withIndex()){
            for(index in 0..amount){
                tickets.add(Ticket(UUID.randomUUID(), TicketStatus.OPEN, user, TicketReason.OTHER,
                        arrayListOf(TicketMessage(user, "Text"))))
            }
        }

        //get five most active users
        val users = reportManager.getActiveUsers(tickets, 5)

        //check if user with lowest tickets amount not included
        assertFalse(users.containsKey("First"))

        //check if tickets amount calculated correctly
        assertTrue(users["Second"] == 2)
        assertTrue(users["Third"] == 3)
        assertTrue(users["Fourth"] == 4)
        assertTrue(users["Fifth"] == 5)
        assertTrue(users["Sixth"] == 6)
    }

    @Test
    fun testAddReport(){
        //create new ticket and insert it into database
        val ticket = reportManager.newTicket("Ticket text", TicketReason.BUG, "Sender")
        //check if tickets has been inserted
        assertNotNull(reportManager.ticketDao.queryForId(ticket.uid))
    }

    @After
    fun clean(){
        TableUtils.clearTable(reportManager.connectionSource, (reportManager.ticketDao as BaseDaoImpl<*, *>).tableConfig)
    }
}