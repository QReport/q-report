package ru.redenergy.report.common.network.packet

import com.rabbit.gui.GuiFoundation
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.network.Message

/**
 * Sent from server to client to inform about administrator access
 */
class UpdateAdminAccess(var adminAccess: Boolean): Message<UpdateAdminAccess> {

    constructor(): this(false)

    override fun toBytes(buf: ByteBuf) {
        buf.writeBoolean(adminAccess)
    }

    override fun fromBytes(buf: ByteBuf) {
        this.adminAccess = buf.readBoolean()
    }

    override fun onMessage(message: UpdateAdminAccess, ctx: MessageContext): IMessage? {
        QReportClient.adminAccess = message.adminAccess
        GuiFoundation.getCurrentStage()?.reinitShow()
        return null
    }

}