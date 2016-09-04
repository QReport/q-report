package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.DropDown
import com.rabbit.gui.component.display.TextLabel
import net.minecraft.client.resources.I18n
import net.minecraft.util.EnumChatFormatting
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.client.ui.TicketsListShow
import ru.redenergy.report.client.ui.components.ShortDropDown
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.ChangeTicketStatus
import ru.redenergy.report.common.network.packet.DeleteTicketPacket
import ru.redenergy.report.common.network.packet.requests.RequestSyncPacket

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
        registerComponent(TextLabel(this.width / 5 + this.width / 2, this.height / 6 - 2, 100, "Action:")
                .setId("action_label")
                .setIsVisible(false))
        registerComponent(ShortDropDown<String>(this.width / 5 + this.width / 2 + 35, this.height / 6 - 3, 100)
                .add("delete", "Delete ticket")
                .add("block", "Block sender")
                .setItemSelectedListener { dropDown, s -> performAction(s, dropDown) }
                .setId("action_dd")
                .setIsVisible(false)
                .setIsEnabled(false))
        super.registerComponents()
    }

    fun changeStatus(){
        val ticket = this.selectedTicket ?: return
        val dropdown = findComponentById<DropDown<TicketStatus>>("status_dropdown")
        val updatedStatus = dropdown.selectedElement.value
        NetworkHandler.sendToServer(ChangeTicketStatus(ticket.uid, updatedStatus))
        NetworkHandler.sendToServer(RequestSyncPacket())
    }

    fun performAction(action: String, dropDown: DropDown<String>){
        when(action){
            "delete" -> selectedTicket?.let{ t -> getStage().display(DeleteConfirmShow(t.uid))}
            "block" -> selectedTicket?.let{ t -> getStage().display(BlockPlayerShow(t.sender))}
        }
        dropDown.setSelected("placeholder")
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
                .addItemAndSetDefault(I18n.format(ticket.status.translateKey), ticket.status)
                .apply { TicketStatus.values().filter { it != ticket.status }.forEach { add(I18n.format(it.translateKey), it) } }
        findComponentById<DropDown<*>>("action_dd")
                .setIsVisible(true)
                .setIsEnabled(true)
        findComponentById<TextLabel>("action_label")
                .setIsVisible(true)
    }
}