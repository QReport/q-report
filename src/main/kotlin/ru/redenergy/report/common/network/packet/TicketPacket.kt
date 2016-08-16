package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.network.Message
import ru.redenergy.report.server.QReportServer

/**
 * Represents new ticket which is sent from client to server
 */
class TicketPacket(var text: String, var reason: TicketReason): Message<TicketPacket>{

    constructor(): this("", TicketReason.OTHER)

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, text)
        ByteBufUtils.writeUTF8String(buf, reason.name)
    }

    override fun fromBytes(buf: ByteBuf) {
        this.text = ByteBufUtils.readUTF8String(buf)
        this.reason = TicketReason.valueOf(ByteBufUtils.readUTF8String(buf))
    }

    override fun onMessage(message: TicketPacket, ctx: MessageContext): IMessage? {
        QReportServer.ticketManager.handleNewTicket(message.text, message.reason, ctx.serverHandler.playerEntity)
        return null
    }

}