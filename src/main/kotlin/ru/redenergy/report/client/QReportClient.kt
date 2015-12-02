package ru.redenergy.report.client

import com.rabbit.gui.GuiFoundation
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.client.keyboard.KeyboardController
import ru.redenergy.report.client.ui.ReportShow
import ru.redenergy.report.client.ui.TicketsListShow
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.RequestSyncPacket
import ru.redenergy.report.common.network.packet.SyncTickets
import ru.redenergy.report.common.network.packet.TicketPacket

@Mod(modid = "qreport-client", name = "QReport - Q&A communication tool for players and admins", modLanguageAdapter = "ru.redenergy.report.kotlin.KotlinAdapter")
object QReportClient {

    public var syncedTickets : MutableList<Ticket> = arrayListOf()

    @Mod.EventHandler
    public fun postInit(event: FMLPostInitializationEvent){
        registerKeys()
        NetworkHandler.instance.initialise()
        println("QReport Loaded")
    }

    @Mod.EventHandler
    public fun init(event: FMLInitializationEvent){
        registerPackets()
    }

    private fun registerPackets(){
        NetworkHandler.instance.apply {
            registerPacket(TicketPacket::class)
            registerPacket(RequestSyncPacket::class)
            registerPacket(SyncTickets::class)
        }
    }

    private fun registerKeys(){
        NetworkHandler.instance.postInitialize()
        var keyboardController = KeyboardController()
        keyboardController.register(Keyboard.KEY_R, "Opens ticket form") { if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) GuiFoundation.display(ReportShow()) }
        keyboardController.register(Keyboard.KEY_G, "Opens tickets list") { if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) GuiFoundation.display(TicketsListShow()) }
        keyboardController.submit()
    }
}

