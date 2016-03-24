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
import ru.redenergy.report.common.Stats
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.network.NetworkHandler

@Mod(modid = "qreport-client", name = "QReport Client", modLanguageAdapter = "io.drakon.forge.kotlin.KotlinAdapter")
object QReportClient {

    /**
     * Contains tickets which have been received from server
     */
    var syncedTickets: MutableList<Ticket> = arrayListOf()
    /**
     * Contains statistics which have been received from server
     */
    var syncedStats: Stats = Stats(mapOf(TicketReason.BUG to 0, TicketReason.OTHER to 0, TicketReason.GRIEFING to 0, TicketReason.OTHER to 0), mapOf(), -1L)
    /**
     * Shows if current player can manage user requests, if set to true 'admin' button will be available
     */
    var adminAccess = false

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        NetworkHandler.initialise()
        NetworkHandler.registerDefaultPackets()
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        registerKeys()
        NetworkHandler.postInitialize()
        MinecraftForge.EVENT_BUS.register(UIOpenHandler())
        println("QReport Loaded")
    }


    private fun registerKeys() {
        var keyboardController = KeyboardController()
        keyboardController.register(Keyboard.KEY_R, "Support center") { if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) GuiFoundation.display(SupportShow()) }
        keyboardController.submit()
    }
}

