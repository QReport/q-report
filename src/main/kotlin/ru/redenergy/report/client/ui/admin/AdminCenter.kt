package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.render.TextAlignment
import com.rabbit.gui.show.Show
import net.minecraft.client.resources.I18n
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.requests.RequestSyncPacket

class AdminCenter: Show() {

    init {
        background = DefaultBackground()
    }

    override fun onInit() {
        super.onInit()
        NetworkHandler.sendToServer(RequestSyncPacket())
    }

    override fun setup() {
        super.setup()
        registerComponent(TextLabel(this.width / 3, this.height / 4, this.width / 3, 20, I18n.format("show.admin.title"), TextAlignment.CENTER))
        registerComponent(Button(this.width / 3, this.height / 3, this.width / 3, 20, I18n.format("show.admin.tickets"))
                    .setClickListener { this.getStage().display(ManageTicketsShow()) })
        registerComponent(Button(this.width / 3, this.height / 3 + 30, this.width / 3, 20, I18n.format("show.stats.title"))
                    .setClickListener { this.getStage().display(StatisticsShow()) })
        registerComponent(Button(this.width / 3, this.height / 3 * 2, this.width / 3, 20, I18n.format("show.admin.back"))
                    .setClickListener { this.getStage().displayPrevious() })
    }
}