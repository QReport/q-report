package ru.redenergy.report.server.backend

import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import org.junit.Before
import org.junit.Test
import ru.redenergy.report.common.entity.Ticket
import java.io.File
import org.junit.Assert.*

val path = "jdbc:sqlite:${File("").absolutePath}/reports-test.sqlite"

class ReportManagerTest {

    val reportManager = ReportManager(JdbcConnectionSource(path))

    @Before
    fun testInitialize() {
        reportManager.initialize()
    }

    @Test
    fun testAddReport(){
        reportManager.addTicket(Ticket("MagicPlayer", "Text"))
    }

    @Test
    fun testGetReports(){
        reportManager.getTickets().forEach { println(it) }
    }

}