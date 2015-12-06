package ru.redenergy.report.common.network.packet

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.server.QReportServer
import ru.redenergy.report.common.network.AbstractPacket

/**
 * Sent from client ot server when user opens ui with previous requests
 */
class RequestSyncPacket() : AbstractPacket{

    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {}

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {}

    override fun handleClient(player: EntityPlayer) {}

    override fun handleServer(player: EntityPlayer) {
        QReportServer.ticketManager.handleSyncRequest(player as EntityPlayerMP)
    }
}