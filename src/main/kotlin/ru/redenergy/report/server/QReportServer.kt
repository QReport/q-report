package ru.redenergy.report.server

import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource
import cpw.mods.fml.common.FMLLog
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import io.drakon.forge.kotlin.KotlinAdapter
import net.minecraftforge.common.config.Configuration
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.*
import ru.redenergy.report.server.backend.ReportManager
import java.io.File


@Mod(modid = "qreport-server", name = "QReport Server", acceptableRemoteVersions = "*", modLanguageAdapter = "io.drakon.forge.kotlin.KotlinAdapter")
object QReportServer {

    lateinit var ticketManager: ReportManager
    var jdbcPath = "jdbc:sqlite:${File("").absolutePath}${File.separator}reports.sqlite"
    var checkPermission = false
    var permissionNode = "qreport.tickets.access"
    var jdbcLogin = " "
    var jdbcPassword = " "
    var notifications = true
    var server = "unknown"

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent){
        var config = Configuration(event.suggestedConfigurationFile)
        loadConfig(config)
        config.save()

        if(jdbcPath.contains("mysql") && (!jdbcPath.contains("useUnicode=true") || !jdbcPath.contains("characterEncoding=utf8")))
            FMLLog.bigWarning("You're using MySQL but didn't enable unicode support. Problems with cyrillic symbols may occur." +
                             " Add `useUnicode=true&characterEncoding=utf8` to jdbc url to enable unicode support")

        ticketManager = ReportManager(JdbcPooledConnectionSource(jdbcPath, jdbcLogin, jdbcPassword))
        NetworkHandler.initialise()
    }

    /**
     * Loads server variables from given config
     */
    private fun loadConfig(config: Configuration){
        this.jdbcPath = config.getString("jdbc", "QReport DB", jdbcPath, "JDBC database path. Currently supported: MySQL and SQLite")
        this.jdbcLogin = config.getString("jdbc-login", "QReport DB", jdbcLogin, "Login for database connection, leave empty if no authorization needed")
        this.jdbcPassword = config.getString("jdbc-password", "QReport DB", jdbcPassword, "Password for database connection, leave empty if no authorization needed")
        this.checkPermission = config.getBoolean("check-permission", "QReport Permission", checkPermission, "If enabled - access to ticket management would be given only to users with specified permission node")
        this.permissionNode = config.getString("permission", "QReport Permission", permissionNode, "Permission node required to manage tickets (make sure you enabled 'check-permission')")
        this.notifications = config.getBoolean("notifications", "QReport", notifications, "Enables notification for users when somebody adds new message or updates ticket status")
        this.server = config.getString("server", "QReport", server, "Identifier of current server, used when there is more than 1 server in one database")
    }

    @Mod.EventHandler
    fun init(event: FMLPreInitializationEvent){
        NetworkHandler.registerDefaultPackets()
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent){
        println("Serverside QReport Loaded")
        NetworkHandler.postInitialize()
        ticketManager.initialize()
    }
}

