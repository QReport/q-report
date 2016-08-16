package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.server.QReportServer
import ru.redenergy.report.common.TicketStatus
import ru.redenergy.report.common.network.Message
import java.util.*

class ChangeTicketStatus(var ticketUid: Int, var status: TicketStatus): Message<ChangeTicketStatus> {

    constructor(): this(-1, TicketStatus.OPEN)

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(ticketUid)
        ByteBufUtils.writeUTF8String(buf, status.name)
    }

    override fun fromBytes(buf: ByteBuf) {
        this.ticketUid = buf.readInt()
        this.status = TicketStatus.valueOf(ByteBufUtils.readUTF8String(buf))
    }

    override fun onMessage(message: ChangeTicketStatus, ctx: MessageContext): IMessage? {
        QReportServer.ticketManager.handleUpdateTicketStatus(message.ticketUid, message.status, ctx.serverHandler.playerEntity)
        return null
    }

}