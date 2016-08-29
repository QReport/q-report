package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.base.WidgetContainer
import com.rabbit.gui.component.GuiWidget
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.TextBox
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.component.list.DisplayList
import com.rabbit.gui.component.list.ScrollableDisplayList
import com.rabbit.gui.component.list.entries.ListEntry
import com.rabbit.gui.render.Renderer
import com.rabbit.gui.render.TextAlignment
import com.rabbit.gui.render.TextRenderer
import com.rabbit.gui.show.Show
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.commons.lang3.time.DurationFormatUtils
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.client.ui.components.UnblockPlayerComponent
import ru.redenergy.report.common.BlockedPlayer
import ru.redenergy.report.common.network.NetworkHandler
import ru.redenergy.report.common.network.packet.ChangePlayerStatus
import ru.redenergy.report.common.network.packet.requests.RequestBlockedPlayers
import java.util.*

class BlockedListShow(): Show() {

    init {
        background = DefaultBackground()
    }

    lateinit var unblockDialog: UnblockPlayerComponent

    override fun onInit() {
        super.onInit()
        NetworkHandler.sendToServer(RequestBlockedPlayers())
    }

    override fun setup() {
        super.setup()
        unblockDialog = UnblockPlayerComponent(this.width / 2 - 100, this.height / 2 - 50, 200, 100, null)
        registerComponent(unblockDialog)
        unblockDialog.enabled = false
        unblockDialog.listener = { player, response -> processDialogResponse(player, response)}

        registerComponent(TextLabel(this.width / 2 - 50, this.height / 2 - 180, 100, 20, "Blocked Player List"))
        registerComponent(TextBox(this.width / 2 - 200, this.height / 2 - 130, 300, 20)
                .setId("search_filter"))
        registerComponent(Button(this.width / 2 + 105, this.height / 2 - 130, 100, 20, "Search")
                .setClickListener { searchByFilter() })
        registerComponent(ScrollableDisplayList(this.width / 2 - 200, this.height / 2 - 100, 400, this.height / 2 + 60, 30,
                mutableListOf())
                .setVisibleBackground(false)
                .setId("blocked_players"))
        registerComponent(Button(this.width / 2 + 105, this.height - 30, 95, 20, "Block Player")
                .setClickListener { this.getStage().display(BlockPlayerShow()) })
        registerComponent(Button(this.width / 2 - 100, this.height - 30, 200, 20, "Back")
                .setClickListener { this.getStage().displayPrevious() })
        updateBlockedPlayersList()
    }

    fun searchByFilter(){
        val searchFilter = findComponentById<TextBox>("search_filter")
        val filter = searchFilter.text.toLowerCase()
        updateBlockedPlayersList(players =
            QReportClient.syncedBlockedPlayers
                    .filter { it.name.toLowerCase().contains(filter) ||
                                it.blockedBy.toLowerCase().contains(filter)})
    }

    fun updateBlockedPlayersList(players: Collection<BlockedPlayer> = QReportClient.syncedBlockedPlayers){
        val blockedPlayers = findComponentById<ScrollableDisplayList>("blocked_players")
        blockedPlayers.clear()
        blockedPlayers.addAll(*players.map { BlockedPlayerEntry(it) }.toTypedArray())
    }

    fun displayDialogFor(player: BlockedPlayer){
        unblockDialog.player = player
        unblockDialog.enabled = true
    }

    fun processDialogResponse(player: BlockedPlayer?, response: Boolean){
        unblockDialog.player = null
        unblockDialog.enabled = false
        if(player == null) return
        if(response)
            NetworkHandler.sendToServer(ChangePlayerStatus(player.name, ChangePlayerStatus.Status.UNBLOCKED))

    }

    private class BlockedPlayerEntry(val player: BlockedPlayer): ListEntry {

        val button = Button(0, 0, 70, 20, "Unblock") //x and y will be updated dynamically on render

        init {
            button.setup()
        }

        override fun onClick(list: DisplayList, mouseX: Int, mouseY: Int) {
            super.onClick(list, mouseX, mouseY)
            if(button.onMouseClicked(mouseX, mouseY, 0, false))
                (list.parent as? BlockedListShow)?.displayDialogFor(player)

        }

        override fun onDraw(list: DisplayList?, posX: Int, posY: Int, width: Int, height: Int, mouseX: Int, mouseY: Int) {
            Renderer.drawRect(posX, posY, posX + width, posY + height, -6250336)
            Renderer.drawRect(posX + 1, posY + 1, posX + width - 1, posY + height - 1, -0xFFFFFF - 1)
            
            button.x = posX + width - 90
            button.y = posY + height / 2 - 10
            button.onDraw(mouseX, mouseY, 0F)

            TextRenderer.renderString(posX + 5, posY + height / 2 - 5, player.name)
            val blockTime = DateFormatUtils.format(player.blockTime, "HH:mm dd/MM")
            TextRenderer.renderString(posX + width / 2, posY + height / 2 - 5, "Blocked by ${player.blockedBy} at $blockTime", TextAlignment.CENTER)
        }
    }
}