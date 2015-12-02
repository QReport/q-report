package ru.redenergy.report.client.ui

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.DropDown
import com.rabbit.gui.component.control.MultiTextbox
import com.rabbit.gui.component.control.TextBox
import com.rabbit.gui.show.Show
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.TicketPacket

class ReportShow : Show() {

    init{
        background = DefaultBackground()
        title = "Report"
    }

    public override fun setup(){
        super.setup()
        registerComponent(MultiTextbox(this.width / 3, this.height / 3, this.width / 3, this.height / 3)
                .setId("text_box"))
        registerComponent(DropDown<TicketReason>(this.width / 3, this.height / 3 * 2 + 5, this.width / 3, "Ticket reason:")
                .addAndSetDefault(TicketReason.OTHER)
                .addAll(*TicketReason.values.filter { !it.equals(TicketReason.OTHER) }.toTypedArray())
                .setId("reason_dropdown"))
        registerComponent(Button(this.width / 3, this.height / 3 * 2 + 22, this.width / 3, 20, "Send")
                .setClickListener { send() })
        registerComponent(Button(this.width / 3, this.height / 3 * 2 + 44, this.width / 3, 20, "Close")
                .setClickListener { this.getStage().displayPrevious() })
    }

    private fun send(){
        var text = (findComponentById<TextBox>("text_box") as MultiTextbox).text
        var reason = (findComponentById<DropDown<TicketReason>>("reason_dropdown") as DropDown<TicketReason>).selectedElement.value
        NetworkHandler.instance.sendToServer(TicketPacket(text, reason))
        this.getStage().displayPrevious()
    }

}

