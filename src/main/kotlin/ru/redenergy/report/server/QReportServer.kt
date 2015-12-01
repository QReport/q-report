package ru.redenergy.report.client

import com.j256.ormlite.jdbc.JdbcConnectionSource
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.config.Configuration
import ru.redenergy.report.server.backend.ReportManager
import java.io.File

annotation class ReportManager

@Mod(modid = "qreport-server", name = "QReport - Q&A communication tool for players and admins", modLanguageAdapter = "ru.redenergy.report.kotlin.KotlinAdapter")
object QReportServer {

    var reportManager: ReportManager? = null
    var jdbcPath = "jdbc:sqlite:${File("").getAbsolutePath()}/reports.sqlite"

    @Mod.EventHandler
    public fun preInit(event: FMLPreInitializationEvent){
        var config = Configuration(event.suggestedConfigurationFile)
        this.jdbcPath = config.getString("jdbc path", "Database", jdbcPath, "JDBC database path can be mysql or sqlite")
        config.save()
    }

    @Mod.EventHandler
    public fun postInit(event: FMLPostInitializationEvent){
        println("Serverside QReport Loaded")
        reportManager = ReportManager(JdbcConnectionSource("jdbc:sqlite:${File("").getAbsolutePath()}/reports.sqlite"))
        reportManager?.initialize()
    }
}

