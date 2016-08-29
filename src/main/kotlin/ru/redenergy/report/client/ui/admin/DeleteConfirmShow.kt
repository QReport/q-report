package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.render.TextAlignment
import com.rabbit.gui.show.Show
import net.minecraft.client.resources.I18n
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.DeleteTicketPacket
import ru.redenergy.report.common.network.packet.requests.RequestSyncPacket

class DeleteConfirmShow(val ticket: Int): Show() {

    init{
        background = DefaultBackground()
    }

    override fun setup() {
        super.setup()
        registerComponent(TextLabel(this.width / 2 - 100, this.height / 2 - 30, 200, 40, I18n.format("show.delete.content", ticket))
                            .setMultilined(true)
                            .setTextAlignment(TextAlignment.CENTER))
        registerComponent(Button(this.width / 2 - 102, this.height / 2, 75, 20, I18n.format("show.delete.confirm"))
                            .setClickListener { delete() })
        registerComponent(Button(this.width / 2 + 27, this.height / 2, 75, 20, I18n.format("show.delete.cancel"))
                            .setClickListener { getStage().displayPrevious() })
    }

    fun delete(){
        NetworkHandler.sendToServer(DeleteTicketPacket(ticket))
        NetworkHandler.sendToServer(RequestSyncPacket())
        getStage().displayPrevious();
    }
}