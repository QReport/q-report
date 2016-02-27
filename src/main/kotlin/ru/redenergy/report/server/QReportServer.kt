package ru.redenergy.report.server

import com.j256.ormlite.jdbc.JdbcConnectionSource
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.common.config.Configuration
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.*
import ru.redenergy.report.server.backend.ReportManager
import java.io.File

@Mod(modid = "qreport-server", name = "QReport Server", acceptableRemoteVersions = "*", modLanguageAdapter = "io.drakon.forge.kotlin.KotlinAdapter")
object QReportServer {

    lateinit var ticketManager: ReportManager
    var jdbcPath = "jdbc:sqlite:${File("").absolutePath}\\reports.sqlite"
    var checkPermission = false
    var permissionNode = "qreport.tickets.access"
    var jdbcLogin = " "
    var jdbcPassword = " "
    var notifications = true
    var server = "unknown"

    @Mod.EventHandler
    public fun preInit(event: FMLPreInitializationEvent){
        var config = Configuration(event.suggestedConfigurationFile)
        loadConfig(config)
        config.save()
        ticketManager = ReportManager(JdbcConnectionSource(jdbcPath, jdbcLogin, jdbcPassword))
        NetworkHandler.instance.initialise()
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
    public fun init(event: FMLPreInitializationEvent){
        registerPackets()
    }

    /**
     * Registers packets in NetworkHandler instance
     */
    private fun registerPackets(){
        NetworkHandler.instance.apply {
            registerPacket(TicketPacket::class)
            registerPacket(RequestSyncPacket::class)
            registerPacket(SyncTickets::class)
            registerPacket(AddMessagePacket::class)
            registerPacket(UpdateAdminAccess::class)
            registerPacket(ChangeTicketStatus::class)
            registerPacket(SyncStatsPackets::class)
        }
    }

    @Mod.EventHandler
    public fun postInit(event: FMLPostInitializationEvent){
        println("Serverside QReport Loaded")
        NetworkHandler.instance.postInitialize()
        ticketManager.initialize()
    }
}

