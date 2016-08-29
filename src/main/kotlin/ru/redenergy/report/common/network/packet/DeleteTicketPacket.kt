package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.common.network.Message
import ru.redenergy.report.server.QReportServer

class DeleteTicketPacket(var id: Int): Message<DeleteTicketPacket> {

    constructor(): this(-1)

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(id)
    }

    override fun fromBytes(buf: ByteBuf) {
        id = buf.readInt()
    }

    override fun onMessage(message: DeleteTicketPacket, ctx: MessageContext): IMessage? {
        QReportServer.ticketManager.handleDeleteTicker(message.id, ctx.serverHandler.playerEntity)
        return null
    }
}