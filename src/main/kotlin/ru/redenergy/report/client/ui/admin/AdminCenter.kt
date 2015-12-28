package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.render.TextAlignment
import com.rabbit.gui.show.Show
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.RequestSyncPacket

class AdminCenter: Show() {

    init {
        background = DefaultBackground()
    }

    override fun onInit() {
        super.onInit()
        NetworkHandler.instance.sendToServer(RequestSyncPacket())
    }

    override fun setup() {
        super.setup()
        registerComponent(TextLabel(this.width / 3, this.height / 4, this.width / 3, 20, "Admin center", TextAlignment.CENTER))
        registerComponent(Button(this.width / 3, this.height / 3, this.width / 3, 20, "Manage tickets")
                    .setClickListener { this.getStage().display(ManageTicketsShow()) })
        registerComponent(Button(this.width / 3, this.height / 3 + 30, this.width / 3, 20, "Statistics")
                    .setClickListener { this.getStage().display(StatisticsShow()) })
        registerComponent(Button(this.width / 3, this.height / 3 * 2, this.width / 3, 20, "Back")
                    .setClickListener { this.getStage().displayPrevious() })
    }
}