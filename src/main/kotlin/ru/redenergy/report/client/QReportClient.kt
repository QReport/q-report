package ru.redenergy.report.client

import com.rabbit.gui.GuiFoundation
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.input.Keyboard
import ru.redenergy.report.client.handler.UIOpenHandler
import ru.redenergy.report.client.keyboard.KeyboardController
import ru.redenergy.report.client.ui.SupportShow
import ru.redenergy.report.common.BlockedPlayer
import ru.redenergy.report.common.Stats
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.network.NetworkHandler

@Mod(modid = "qreport-client", name = "QReport Client", modLanguageAdapter = "io.drakon.forge.kotlin.KotlinAdapter")
object QReportClient {

    /**
     * Contains tickets which have been received from server
     */
    var syncedTickets = arrayListOf<Ticket>()
    /**
     * Contains statistics which have been received from server
     */
    var syncedStats: Stats = Stats(mapOf(TicketReason.BUG to 0, TicketReason.OTHER to 0, TicketReason.GRIEFING to 0, TicketReason.OTHER to 0), mapOf(), -1L)
    /**
     * Shows if current player can manage user requests, if set to true 'admin' button will be available
     */
    var adminAccess = false
    /**
     * Information about blocked players received from server.
     * If client player is blocked this list will contain object with his name.
     * If client player is admin this list will contain all blocked players.
     */
    var syncedBlockedPlayers = arrayListOf<BlockedPlayer>()

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        NetworkHandler.registerDefaultPackets()
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        registerKeys()
        MinecraftForge.EVENT_BUS.register(UIOpenHandler())
        println("QReport Loaded")
    }


    private fun registerKeys() {
        var keyboardController = KeyboardController()
        keyboardController.register(Keyboard.KEY_R, "Support center") { if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) GuiFoundation.display(SupportShow()) }
        keyboardController.submit()
    }
}

