package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.DropDown
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumChatFormatting
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.client.ui.TicketsListShow
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.ChangeTicketStatus
import ru.redenergy.report.common.network.packet.DeleteTicketRequest
import ru.redenergy.report.common.network.packet.RequestSyncPacket

class ManageTicketsShow: TicketsListShow(){

    override fun onInit() {
        super.onInit()
        if(!QReportClient.adminAccess) this.getStage().displayPrevious()
    }

    override fun registerComponents() {
        registerComponent(DropDown<TicketStatus>(this.width / 5 + this.width / 6 + this.width / 3 - this.width / 10 - 20, this.height / 6 - 5, this.width / 10)
                .setIsVisible(false)
                .setIsEnabled(false)
                .setId("status_dropdown")
                .setItemSelectedListener( { dd, sel -> changeStatus() } ))
        registerComponent(Button(this.width / 5 + this.width / 2, this.height / 6 - 6, 40, 15, I18n.format("show.tickets.delete"))
                .setIsEnabled(false)
                .setIsVisible(false)
                .setClickListener { deleteTicket() }
                .setId("delBtn"))
        super.registerComponents()
    }

    fun deleteTicket(){
        val ticket = selectedTicket ?: return
        NetworkHandler.instance.sendToServer(DeleteTicketRequest(ticket.uid))
        NetworkHandler.instance.sendToServer(RequestSyncPacket())
    }

    fun changeStatus(){
        val ticket = this.selectedTicket ?: return
        var dropdown = findComponentById<DropDown<TicketStatus>>("status_dropdown")
        var updatedStatus = dropdown.selectedElement.value
        NetworkHandler.instance.sendToServer(ChangeTicketStatus(ticket.uid, updatedStatus))
        NetworkHandler.instance.sendToServer(RequestSyncPacket())
    }

    override fun getTicketsListContent(): List<TicketEntry> =
        QReportClient.syncedTickets
                .sortedBy { it.messages[0].timestamp }
                .reversed()
                .map { TicketEntry(it, {select(it)}) }

    override fun updateInformation() {
        super.updateInformation()
        val ticket = selectedTicket ?: return;
        findComponentById<DropDown<TicketStatus>>("status_dropdown")
                .setIsEnabled(true)
                .setIsVisible(true)
                .clear()
                .addAndSetDefault(ticket.status)
                .addAll(*TicketStatus.values().filter { it != ticket.status }.toTypedArray())
        findComponentById<Button>("delBtn")
                .setIsVisible(true)
                .setIsEnabled(true)
    }
}