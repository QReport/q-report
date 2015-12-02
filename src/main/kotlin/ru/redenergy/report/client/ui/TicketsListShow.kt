package ru.redenergy.report.client.ui

import com.rabbit.gui.background.DefaultBackground
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
import ru.redenergy.report.common.network.packet.RequestSyncPacket

class TicketsListShow : Show() {

    init{
        background = DefaultBackground()
    }
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
        registerComponent(MultiTextbox(this.width / 5 + this.width / 6 + 15, this.height / 5, this.width / 3 - 15, this.height / 4 * 2 - 8, "Select ticket")
                        .setBackgroundVisibility(false)
                        .setMaxLenght(Int.MAX_VALUE)
                        .setIsEnabled(false)
                        .setId("information_field"))
        registerComponent(MultiTextbox(this.width / 5 + this.width / 6 + 15, this.height / 5 + this.height / 4 * 2, this.width / 3, this.height / 10))
    }

    override fun onDraw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.onDraw(mouseX, mouseY, partialTicks)
    }

    private fun select(selected: Ticket){
        this.selectedTicker = selected
        updateInformation()
    }

    private fun updateInformation() {
        var informationLabel = findComponentById<MultiTextbox>("information_field") as MultiTextbox
        this.selectedTicker?.apply {
            var builder = StringBuilder().apply {
                append("${EnumChatFormatting.UNDERLINE}$sender${EnumChatFormatting.RESET}")
                append("\n")
                append(timestamp)
                append("\n")
                append(text)
                append("\n")
            }
            informationLabel.setText(builder.toString())
        }

    }

   class TicketEntry(val ticket: Ticket, val action: () -> Unit) : ListEntry{

       override fun onClick(list: DisplayList?, mouseX: Int, mouseY: Int) {
           super.onClick(list, mouseX, mouseY)
           action.invoke()
       }

       override fun onDraw(list: DisplayList?, posX: Int, posY: Int, width: Int, height: Int, mouseX: Int, mouseY: Int) {
           TextRenderer.renderString(posX + 5, posY + 5, ticket.sender)
           TextRenderer.renderString(posX + 5, posY + 15, TextRenderer.getFontRenderer().trimStringToWidth(ticket.text, width - 20) + "...")
           TextRenderer.renderString(posX + 5, posY + 25, TextRenderer.getFontRenderer().trimStringToWidth(ticket.reason.getTranslation(), width -5 ))
       }
   }

}