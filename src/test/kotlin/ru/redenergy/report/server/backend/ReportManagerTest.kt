package ru.redenergy.report.server.backend

import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import org.junit.Before
import org.junit.Test
import ru.redenergy.report.server.backend.entity.Report
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
        reportManager.addReport(Report(sender = "MagicPlayer", text = "Text"))
    }

    @Test
    fun testGetReports(){
        reportManager.getReports().forEach { println(it) }
    }

}