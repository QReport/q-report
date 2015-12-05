package ru.redenergy.report.common.network.packet

import com.rabbit.gui.GuiFoundation
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.network.AbstractPacket

/**
 * Sent from server to client to inform about administrator access
 */
class UpdateAdminAccess(var adminAccess: Boolean): AbstractPacket {


    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        buf.writeBoolean(adminAccess)
    }

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        this.adminAccess = buf.readBoolean()
    }

    override fun handleClient(player: EntityPlayer) {
        QReportClient.adminAccess = adminAccess
        GuiFoundation.getCurrentStage()?.reinitShow()
    }

    override fun handleServer(player: EntityPlayer) {
        throw UnsupportedOperationException()
    }
}