package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.TextBox
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.render.TextAlignment
import com.rabbit.gui.show.Show
import net.minecraft.util.EnumChatFormatting
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.ChangePlayerStatus

class BlockPlayerShow: Show(){

    init {
        background = DefaultBackground()
    }

    override fun setup() {
        super.setup()
        registerComponent(TextLabel(this.width / 2 - 100, this.height / 2 - 70, 200, 10, "Block Player", TextAlignment.CENTER))
        registerComponent(TextBox(this.width / 2 - 100, this.height / 2 - 30, 200, 20)
                    .setId("player_name"))
        registerComponent(TextLabel(this.width / 2 - 100, this.height / 2 - 5, 200, 20, "", TextAlignment.CENTER)
                    .setId("warning")
                    .setMultilined(true))
        registerComponent(Button(this.width / 2 - 100, this.height / 2 + 20, 200, 20, "Block")
                    .setClickListener { blockPlayer() })
        registerComponent(Button(this.width / 2 - 100, this.height / 2 + 42, 200, 20, "Back")
                    .setClickListener { this.getStage().displayPrevious() })
    }

    fun blockPlayer(){
        val playerName = findComponentById<TextBox>("player_name").text
        val blockStatus = QReportClient.syncedBlockedPlayers.firstOrNull { it.blocked && it.name == playerName }
        if(blockStatus != null)
            findComponentById<TextLabel>("warning").text =
                    "${EnumChatFormatting.YELLOW}$playerName already has been blocked by ${blockStatus.blockedBy}"
        else
            NetworkHandler.sendToServer(ChangePlayerStatus(playerName, ChangePlayerStatus.Status.BLOCKED))
    }
}