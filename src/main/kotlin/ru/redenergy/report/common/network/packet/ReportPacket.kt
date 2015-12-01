package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.client.QReportServer
import ru.redenergy.report.common.network.AbstractPacket

/**
 * Represents new ticket which is sent from client to server
 */
class ReportPacket(var text: String): AbstractPacket {

    constructor() : this("") {}

    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, text)
    }

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        this.text = ByteBufUtils.readUTF8String(buf)
    }

    override fun handleClient(player: EntityPlayer) {
        throw UnsupportedOperationException()
    }

    override fun handleServer(player: EntityPlayer) {
        QReportServer.reportManager?.handleNewTicket(text, player as EntityPlayerMP)
    }

}