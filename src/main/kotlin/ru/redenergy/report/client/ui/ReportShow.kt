package ru.redenergy.report.client.ui

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.DropDown
import com.rabbit.gui.component.control.MultiTextbox
import com.rabbit.gui.component.control.TextBox
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.show.Show
import net.minecraft.client.resources.I18n
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.TicketPacket

class ReportShow : Show() {

    init{
        background = DefaultBackground()
        title = "Report"
    }

    override fun setup(){
        super.setup()
        registerComponent(TextLabel(this.width / 3, this.height / 4 - 15, this.width / 3, I18n.format("show.report.problem")))
        registerComponent(MultiTextbox(this.width / 3, this.height / 4, this.width / 3, this.height / 3)
                .setId("text_box"))
        registerComponent(TextLabel(this.width / 3, this.height / 4 + this.height / 3 + 20, this.width /3, I18n.format("show.report.category")))
        registerComponent(DropDown<TicketReason>(this.width / 3 + 2, this.height / 4 + this.height / 3 + 35, this.width / 3 - 4)
                .addItemAndSetDefault(I18n.format(TicketReason.OTHER.translateKey), TicketReason.OTHER)
                .apply {
                    TicketReason.values().filterNot {it == TicketReason.OTHER} .forEach { add(I18n.format(it.translateKey), it) }
                }
                .setId("reason_dropdown"))
        registerComponent(Button(this.width / 3, this.height / 3 + this.height / 4 + 60, this.width / 3 / 2 - 2, 20, I18n.format("show.report.send"))
                .setClickListener { send() })
        registerComponent(Button(this.width / 3 + this.width / 3 / 2 + 2, this.height / 3 + this.height / 4 + 60, this.width / 3 / 2 - 2, 20, I18n.format("show.report.close"))
                .setClickListener { this.getStage().displayPrevious() })
    }

    private fun send(){
        var text = (findComponentById<TextBox>("text_box") as MultiTextbox).text
        var reason = (findComponentById<DropDown<TicketReason>>("reason_dropdown") as DropDown<TicketReason>).selectedElement.value
        NetworkHandler.sendToServer(TicketPacket(text, reason))
        this.getStage().displayPrevious()
    }

}

