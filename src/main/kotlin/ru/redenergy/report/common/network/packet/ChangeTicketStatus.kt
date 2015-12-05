package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.client.QReportServer
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.network.AbstractPacket
import java.util.*

class ChangeTicketStatus(var ticketUid: UUID, var status: TicketStatus): AbstractPacket {
    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, ticketUid.toString())
        ByteBufUtils.writeUTF8String(buf, status.name)
    }

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        this.ticketUid = UUID.fromString(ByteBufUtils.readUTF8String(buf))
        this.status = TicketStatus.valueOf(ByteBufUtils.readUTF8String(buf))
    }

    override fun handleClient(player: EntityPlayer) {
        throw UnsupportedOperationException()
    }

    override fun handleServer(player: EntityPlayer) {
        QReportServer.ticketManager.handleUpdateTicketStatus(ticketUid, status, player as EntityPlayerMP)
    }
}