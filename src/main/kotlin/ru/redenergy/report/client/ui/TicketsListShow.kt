package ru.redenergy.report.client.ui

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.MultiTextbox
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.component.list.DisplayList
import com.rabbit.gui.component.list.ScrollableDisplayList
import com.rabbit.gui.component.list.entries.ListEntry
import com.rabbit.gui.render.TextRenderer
import com.rabbit.gui.show.Show
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
        registerComponent(ScrollableDisplayList(this.width / 5, this.height / 5, this.width / 6 , this.height / 5 * 3, 35,
                QReportClient.syncedTickets.map { TicketEntry(it, {select(it)}) } ))
        registerComponent(TextLabel(this.width / 5 + this.width / 6 + 15, this.height / 5 - 15, this.width / 6, "Information about ticket:"))
        registerComponent(MultiTextbox(this.width / 5 + this.width / 6 + 15, this.height / 5, this.width / 3 - 15, this.height / 4 * 2 - 23, "Select ticket")
                        .setBackgroundVisibility(false)
                        .setMaxLenght(Int.MAX_VALUE)
                        .setIsEnabled(false)
                        .setId("information_field"))
        registerComponent(TextLabel(this.width / 5 + this.width / 6 + 15, this.height / 5 + this.height / 4 * 2 - 15, this.width / 3, "Add message:"))
        registerComponent(MultiTextbox(this.width / 5 + this.width / 6 + 15, this.height / 5 + this.height / 4 * 2, this.width / 3, this.height / 10)
                        .setId("new_message_field"))
        registerComponent(Button(this.width / 5 + this.width / 6 + 15, this.height / 5 + this.height / 4 * 2 + this.height / 10 + 5, this.width / 3, 20, "Send")
                        .setClickListener({ addMessage() }));

        updateInformation()
    }

    private fun select(selected: Ticket){
        this.selectedTicker = selected
        updateInformation()
    }

    private fun updateInformation() {
        var informationLabel = findComponentById<MultiTextbox>("information_field") as MultiTextbox
        this.selectedTicker?.apply {
            var builder = StringBuilder().apply {
                messages.forEach {
                    append("${EnumChatFormatting.BOLD}${it.sender}")
                    append("\n")
                    append(timeFormatter.format(Date(it.timestamp)))
                    append("\n")
                    append(it.text)
                    append("\n\n")
                }

            }
            informationLabel.setText(builder.toString())
        }

    }

    private fun addMessage(){
        var message = (findComponentById<MultiTextbox>("new_message_field") as MultiTextbox).text
        if(this.selectedTicker == null || message.trim().equals("")) return
        NetworkHandler.instance.sendToServer(AddMessagePacket(this.selectedTicker?.uid as UUID, message))
        NetworkHandler.instance.sendToServer(RequestSyncPacket())
    }


   class TicketEntry(val ticket: Ticket, val action: () -> Unit) : ListEntry{

       override fun onClick(list: DisplayList?, mouseX: Int, mouseY: Int) {
           super.onClick(list, mouseX, mouseY)
           action.invoke()
       }

       override fun onDraw(list: DisplayList?, posX: Int, posY: Int, width: Int, height: Int, mouseX: Int, mouseY: Int) {
           TextRenderer.renderString(posX + 5, posY + 5, ticket.sender)
           TextRenderer.renderString(posX + 5, posY + 25, TextRenderer.getFontRenderer().trimStringToWidth(ticket.reason.getTranslation(), width -5 ))
       }
   }

}