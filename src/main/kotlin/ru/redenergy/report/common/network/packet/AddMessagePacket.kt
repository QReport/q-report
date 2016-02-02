package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.server.QReportServer
import ru.redenergy.report.common.network.AbstractPacket
import java.util.*

class AddMessagePacket(var ticketUid: Int, var message: String) : AbstractPacket {

    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        buf.writeInt(ticketUid)
        ByteBufUtils.writeUTF8String(buf, message)
    }

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        this.ticketUid = buf.readInt()
        this.message = ByteBufUtils.readUTF8String(buf)
    }

    override fun handleClient(player: EntityPlayer) {}

    override fun handleServer(player: EntityPlayer) {
        QReportServer.ticketManager.handleAddMessage(this.ticketUid, this.message, player as EntityPlayerMP)
    }

}