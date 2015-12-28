package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.component.control.DropDown
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.client.ui.TicketsListShow
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.ChangeTicketStatus
import ru.redenergy.report.common.network.packet.RequestSyncPacket

class ManageTicketsShow: TicketsListShow(){

    override fun onInit() {
        super.onInit()
        if(!QReportClient.adminAccess) this.getStage().displayPrevious()
    }

    override fun registerComponents() {
        registerComponent(DropDown<TicketStatus>(this.width / 5 + this.width / 6 + this.width / 3 - this.width / 10 , this.height / 6 - 5, this.width / 10)
                .setIsVisible(false)
                .setIsEnabled(false)
                .setId("status_dropdown")
                .setItemSelectedListener( { dd, sel -> changeStatus() } ))
        super.registerComponents()
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
                .sortedBy { it.messages.get(0).timestamp }
                .reversed()
                .map { TicketEntry(it, {select(it)}) }

    override fun updateInformation() {
        super.updateInformation()
        this.selectedTicket?.apply{
            var dropdown = findComponentById<DropDown<TicketStatus>>("status_dropdown")
            dropdown.apply {
                setIsEnabled(true)
                setIsVisible(true)
                clear()
                addAndSetDefault(status)
                addAll(*TicketStatus.values.filter { it != status }.toTypedArray())
            }
        }
    }
}