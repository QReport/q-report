package ru.redenergy.report.common.network.packet.requests

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.common.network.Message
import ru.redenergy.report.server.QReportServer

/**
 * Sent from client ot server when user opens ui with previous requests
 */
class RequestSyncPacket() : Message<RequestSyncPacket> {

    override fun toBytes(buf: ByteBuf) {}

    override fun fromBytes(buf: ByteBuf) {}

    override fun onMessage(message: RequestSyncPacket, ctx: MessageContext): IMessage? {
        QReportServer.ticketManager.handleSyncRequest(ctx.serverHandler.playerEntity)
        return null
    }
}