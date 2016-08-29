package ru.redenergy.report.common.network.packet.requests

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.common.network.Message
import ru.redenergy.report.server.QReportServer

class RequestBlockedPlayers(): Message<RequestBlockedPlayers> {
    override fun toBytes(buf: ByteBuf) {}

    override fun fromBytes(buf: ByteBuf) {}

    override fun onMessage(message: RequestBlockedPlayers, ctx: MessageContext): IMessage? {
        QReportServer.ticketManager.syncBlockedPlayersWith(ctx.serverHandler.playerEntity)
        return null
    }
}