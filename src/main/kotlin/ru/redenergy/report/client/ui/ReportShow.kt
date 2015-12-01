package ru.redenergy.report.client.ui

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.MultiTextbox
import com.rabbit.gui.component.control.TextBox
import com.rabbit.gui.show.Show
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.ReportPacket

class ReportShow : Show() {

    init{
        background = DefaultBackground()
        title = "Report"
    }

    public override fun setup(){
        super.setup()
        registerComponent(MultiTextbox(this.width / 3, this.height / 3, this.width / 3, this.height / 3)
                .setId("text_box"))
        registerComponent(Button(this.width / 3, this.height / 3 * 2 + 2, this.width / 3, 20, "Send")
                .setClickListener { send() })
        registerComponent(Button(this.width / 3, this.height / 3 * 2 + 24, this.width / 3, 20, "Close")
                .setClickListener { this.getStage().displayPrevious() })
    }

    private fun send(){
        var text = (findComponentById<TextBox>("text_box") as MultiTextbox).text
        NetworkHandler.instance.sendToServer(ReportPacket(text))
        this.getStage().displayPrevious()
    }

}

