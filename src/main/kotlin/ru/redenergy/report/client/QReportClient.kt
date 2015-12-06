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
import ru.redenergy.report.client.ui.SupportShow
import ru.redenergy.report.client.ui.TicketsListShow
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.*

@Mod(modid = "qreport-client", name = "QReport Client", modLanguageAdapter = "ru.redenergy.report.kotlin.KotlinAdapter")
object QReportClient {

    /**
     * Contains tickets which have been received from server
     */
    public var syncedTickets : MutableList<Ticket> = arrayListOf()
    /**
     * Shows if current player can manage user requests, if set to true 'admin' button will be available
     */
    public var adminAccess = false

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
            registerPacket(AddMessagePacket::class)
            registerPacket(UpdateAdminAccess::class)
            registerPacket(ChangeTicketStatus::class)
        }
    }

    private fun registerKeys(){
        NetworkHandler.instance.postInitialize()
        var keyboardController = KeyboardController()
        keyboardController.register(Keyboard.KEY_R, "Support center") { if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) GuiFoundation.display(SupportShow()) }
        keyboardController.submit()
    }
}

