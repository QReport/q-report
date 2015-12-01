package ru.redenergy.report.server.backend

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.server.backend.entity.Report
import java.util.*

class ReportManager(val connectionSource: ConnectionSource) {

    var reportDao = DaoManager.createDao<Dao<Report, UUID>, Report>(connectionSource, Report::class.java)

    public fun initialize(){
        TableUtils.createTableIfNotExists(connectionSource, Report::class.java)
    }

    public fun addReport(report: Report) = reportDao.create(report)

    public fun getReports(): MutableList<Report> = reportDao.queryForAll()

    public fun deleteReport(report: Report) = reportDao.delete(report)

    public fun handleNewTicket(text: String, player: EntityPlayerMP) = newTicket(text, player.commandSenderName)

    public fun newTicket(text: String, sender: String) = addReport(Report(sender = sender, text = text))
}