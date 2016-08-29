package ru.redenergy.report.common.network.packet.sync

import com.rabbit.gui.GuiFoundation
import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.client.ui.admin.BlockedListShow
import ru.redenergy.report.common.BlockedPlayer
import ru.redenergy.report.common.network.Message
import java.util.*
import kotlin.repeat

class SyncBlockedPlayers(var players: MutableList<BlockedPlayer>): Message<SyncBlockedPlayers> {

    constructor(): this(arrayListOf<BlockedPlayer>())

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(players.size)
        for(player in players){
            ByteBufUtils.writeUTF8String(buf, player.name)
            buf.writeBoolean(player.blocked)
            ByteBufUtils.writeUTF8String(buf, player.blockedBy)
            buf.writeLong(player.blockTime)
        }
    }

    override fun fromBytes(buf: ByteBuf) {
        players = arrayListOf<BlockedPlayer>()
        val amount = buf.readInt()
        repeat(amount){
            val name = ByteBufUtils.readUTF8String(buf)
            val blocked = buf.readBoolean()
            val blockedBy = ByteBufUtils.readUTF8String(buf)
            val blockTime = buf.readLong()
            players.add(BlockedPlayer(name, blocked, blockedBy, blockTime))
        }
    }

    override fun onMessage(message: SyncBlockedPlayers, ctx: MessageContext): IMessage? {
        QReportClient.syncedBlockedPlayers = ArrayList(message.players)
        if(GuiFoundation.getCurrentStage()?.show is BlockedListShow)
            (GuiFoundation.getCurrentStage().show as BlockedListShow).updateBlockedPlayersList()
        return null
    }
}