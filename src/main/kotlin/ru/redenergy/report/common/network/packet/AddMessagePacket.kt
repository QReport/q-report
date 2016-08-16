package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.server.QReportServer
import ru.redenergy.report.common.network.Message
import java.util.*

class AddMessagePacket(var ticketUid: Int, var message: String) : Message<AddMessagePacket> {

    constructor(): this(-1, "")

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(ticketUid)
        ByteBufUtils.writeUTF8String(buf, message)
    }

    override fun fromBytes(buf: ByteBuf) {
        this.ticketUid = buf.readInt()
        this.message = ByteBufUtils.readUTF8String(buf)
    }

    override fun onMessage(message: AddMessagePacket, ctx: MessageContext): IMessage? {
        QReportServer.ticketManager.handleAddMessage(message.ticketUid, message.message, ctx.serverHandler.playerEntity)
        return null
    }


}