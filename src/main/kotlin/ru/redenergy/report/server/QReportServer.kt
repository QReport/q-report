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

@Mod(modid = "qreport-server", name = "QReport Server", modLanguageAdapter = "ru.redenergy.report.kotlin.KotlinAdapter")
object QReportServer {

    lateinit var ticketManager: ReportManager
    var jdbcPath = "jdbc:sqlite:${File("").absolutePath}/reports.sqlite"
    var usePermission = false

    @Mod.EventHandler
    public fun preInit(event: FMLPreInitializationEvent){
        var config = Configuration(event.suggestedConfigurationFile)
        this.jdbcPath = config.getString("jdbc path", "Database", jdbcPath, "JDBC database path can be mysql or sqlite")
        this.usePermission = config.getBoolean("user permission", "Ticket Management", false, "If enabled will give access to manage tickets only to users with permission node")
        config.save()
        ticketManager = ReportManager(JdbcConnectionSource(jdbcPath))
        NetworkHandler.instance.initialise()
    }

    @Mod.EventHandler
    public fun init(event: FMLPreInitializationEvent){
        registerPackets()
    }

    private fun registerPackets(){
        NetworkHandler.instance.apply {
            registerPacket(TicketPacket::class)
            registerPacket(RequestSyncPacket::class)
            registerPacket(SyncTickets::class)
            registerPacket(AddMessagePacket::class)
            registerPacket(UpdateAdminAccess::class)
            registerPacket(ChangeTicketStatus::class)
        }
    }

    @Mod.EventHandler
    public fun postInit(event: FMLPostInitializationEvent){
        println("Serverside QReport Loaded")
        NetworkHandler.instance.postInitialize()
        ticketManager.initialize()
    }
}

