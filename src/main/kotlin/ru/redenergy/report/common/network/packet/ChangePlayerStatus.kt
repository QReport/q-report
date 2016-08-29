package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.common.network.Message
import ru.redenergy.report.server.QReportServer

class ChangePlayerStatus(var player: String, var status: Status): Message<ChangePlayerStatus> {

    constructor(): this("", Status.UNBLOCKED)

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, player)
        buf.writeInt(status.ordinal)
    }

    override fun fromBytes(buf: ByteBuf) {
        this.player = ByteBufUtils.readUTF8String(buf)
        this.status = Status.values()[buf.readInt()]
    }

    override fun onMessage(message: ChangePlayerStatus, ctx: MessageContext): IMessage? {
        when(message.status){
            Status.UNBLOCKED -> QReportServer.ticketManager.handleUnblockPlayer(message.player, ctx.serverHandler.playerEntity)
            Status.BLOCKED -> QReportServer.ticketManager.handleBlockPlayer(message.player, ctx.serverHandler.playerEntity)
        }
        return null
    }

    enum class Status{
        BLOCKED,
        UNBLOCKED;
    }
}

