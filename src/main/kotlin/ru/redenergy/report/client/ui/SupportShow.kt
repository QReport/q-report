package ru.redenergy.report.client.ui

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.render.TextAlignment
import com.rabbit.gui.show.Show
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.RequestSyncPacket

class SupportShow: Show() {

    init {
        background = DefaultBackground()
        NetworkHandler.instance.sendToServer(RequestSyncPacket())
    }

    override fun setup() {
        super.setup()
        registerComponent(TextLabel(this.width / 3, this.height / 3 / 2, this.width / 3, "Support center")
                        .setTextAlignment(TextAlignment.CENTER))
        registerComponent(Button(this.width / 3, this.height / 3, this.width / 3, 20, "New ticket")
                        .setClickListener { this.getStage().display(ReportShow()) })
        registerComponent(Button(this.width / 3, this.height / 3 + 30, this.width / 3, 20, "Previous tickets")
                        .setClickListener { this.getStage().display(TicketsListShow()) })
        if(QReportClient.adminAccess)
            registerComponent(Button(this.width / 3, this.height /3 + 60, this.width / 3, 20, "Manage tickets"))
        registerComponent(Button(this.width / 3, this.height / 3 * 2 + 10, this.width / 3, 20, "Close")
                        .setClickListener { this.getStage().displayPrevious() })
    }
}