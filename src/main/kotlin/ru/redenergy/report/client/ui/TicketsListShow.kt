package ru.redenergy.report.client.ui

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.MultiTextbox
import com.rabbit.gui.component.control.TextBox
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.component.list.DisplayList
import com.rabbit.gui.component.list.ScrollableDisplayList
import com.rabbit.gui.component.list.entries.ListEntry
import com.rabbit.gui.render.TextRenderer
import com.rabbit.gui.show.Show
import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.AddMessagePacket
import ru.redenergy.report.common.network.packet.RequestSyncPacket
import java.text.SimpleDateFormat
import java.util.*

class TicketsListShow : Show() {


    init{
        background = DefaultBackground()
    }

    val timeFormatter = SimpleDateFormat("dd.M HH:mm:ss")

    var selectedTicker: Ticket? = null

    override fun onInit(){
        super.onInit()
        NetworkHandler.instance.sendToServer(RequestSyncPacket())
    }

    override fun setup(){
        super.setup()
        registerComponent(TextLabel(this.width / 5, this.height / 5 - 15, this.width / 6, "Tickets:"))
        registerComponent(ScrollableDisplayList(this.width / 5, this.height / 5, this.width / 6 , this.height / 5 * 3, 45,
                QReportClient.syncedTickets
                        .sortedBy { it.messages.get(0).timestamp }
                        .reversed()
                        .filter { it.sender.equals(Minecraft.getMinecraft().thePlayer.commandSenderName, true) }
                        .map { TicketEntry(it, {select(it)}) } ))
        registerComponent(TextLabel(this.width / 5 + this.width / 6 + 15, this.height / 5 - 15, this.width / 6, "Information about ticket:"))
        registerComponent(MultiTextbox(this.width / 5 + this.width / 6 + 15, this.height / 5, this.width / 3 - 15, this.height / 4 * 2 - 23, "Select ticket")
                        .setBackgroundVisibility(false)
                        .setMaxLenght(Int.MAX_VALUE)
                        .setIsEnabled(false)
                        .setId("information_field"))
        registerComponent(TextLabel(this.width / 5 + this.width / 6 + 15, this.height / 5 + this.height / 4 * 2 - 15, this.width / 3, "Add message:")
                        .setIsVisible(false)
                        .setId("add_message_label"))
        registerComponent(MultiTextbox(this.width / 5 + this.width / 6 + 15, this.height / 5 + this.height / 4 * 2, this.width / 3, this.height / 10)
                        .setId("new_message_field")
                        .setIsVisible(false)
                        .setIsEnabled(false))
        registerComponent(Button(this.width / 5 + this.width / 6 + 15, this.height / 5 + this.height / 4 * 2 + this.height / 10 + 5, this.width / 3, 20, "Send")
                        .setIsVisible(false)
                        .setIsEnabled(false)
                        .setId("send_message_button")
                        .setClickListener({ addMessage() }));

        updateInformation()
    }

    private fun select(selected: Ticket){
        this.selectedTicker = selected
        updateInformation()
    }

    private fun updateInformation() {
        val ticket = this.selectedTicker ?: return
        var informationLabel = findComponentById<MultiTextbox>("information_field") as MultiTextbox
        informationLabel.setText(generateInformation(ticket))
        var hasAccess = QReportClient.adminAccess || ticket.sender.equals(Minecraft.getMinecraft().thePlayer.commandSenderName, true)
        setMessageSectionStatus(hasAccess)
    }

    private fun generateInformation(ticket: Ticket): String{
        var builder = StringBuilder()
        ticket.messages.forEach {
            with(builder) {
                append("${EnumChatFormatting.BOLD}${it.sender}")
                append("\n")
                append(timeFormatter.format(Date(it.timestamp)))
                append("\n")
                append(it.text)
                append("\n\n")
            }
        }
        return builder.toString()
    }

    private fun setMessageSectionStatus(status: Boolean){
        var label = findComponentById<TextLabel>("add_message_label") as TextLabel
        label.setIsVisible(status)
        var messageField = findComponentById<TextBox>("new_message_field") as TextBox
        messageField.setIsEnabled(status).setIsVisible(status)
        var sendMessageButton = findComponentById<Button>("send_message_button") as Button
        sendMessageButton.setIsVisible(status).setIsEnabled(status)
    }

    private fun addMessage(){
        var message = (findComponentById<MultiTextbox>("new_message_field") as MultiTextbox).text
        if(this.selectedTicker == null || message.trim().equals("")) return
        NetworkHandler.instance.sendToServer(AddMessagePacket(this.selectedTicker!!.uid, message))
        NetworkHandler.instance.sendToServer(RequestSyncPacket())
    }


   class TicketEntry(val ticket: Ticket, val action: () -> Unit) : ListEntry{

       override fun onClick(list: DisplayList?, mouseX: Int, mouseY: Int) {
           super.onClick(list, mouseX, mouseY)
           action.invoke()
       }

       override fun onDraw(list: DisplayList?, posX: Int, posY: Int, width: Int, height: Int, mouseX: Int, mouseY: Int) {
           TextRenderer.renderString(posX + 5, posY + 5, ticket.sender)
           TextRenderer.renderString(posX + 5, posY + 15, ticket.shortUid())
           TextRenderer.renderString(posX + 5, posY + 25, "Reason: " + ticket.reason.getTranslation())
           TextRenderer.renderString(posX + 5, posY + 35, "${EnumChatFormatting.WHITE}Status: ${EnumChatFormatting.RESET}" + ticket.status.getTranslation(), ticket.status.color)
       }
   }

}