package ru.redenergy.report.common.network.packet

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.common.network.AbstractPacket
import ru.redenergy.report.server.QReportServer

class DeleteTicketRequest(var id: Int): AbstractPacket {

    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        buf.writeInt(id)
    }


    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        id = buf.readInt()
    }

    override fun handleClient(player: EntityPlayer) {
        throw UnsupportedOperationException()
    }

    override fun handleServer(player: EntityPlayer) {
        QReportServer.ticketManager.handleDeleteTicker(id, player as EntityPlayerMP)
    }
}