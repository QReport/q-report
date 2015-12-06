package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.server.QReportServer
import ru.redenergy.report.common.TicketReason
import ru.redenergy.report.common.network.AbstractPacket

/**
 * Represents new ticket which is sent from client to server
 */
class TicketPacket(var text: String, var reason: TicketReason): AbstractPacket {

    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, text)
        ByteBufUtils.writeUTF8String(buf, reason.name)
    }

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        this.text = ByteBufUtils.readUTF8String(buf)
        this.reason = TicketReason.valueOf(ByteBufUtils.readUTF8String(buf))
    }

    override fun handleClient(player: EntityPlayer) {
        throw UnsupportedOperationException()
    }

    override fun handleServer(player: EntityPlayer) {
        QReportServer.ticketManager.handleNewTicket(text, reason, player as EntityPlayerMP)
    }

}