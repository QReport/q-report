package ru.redenergy.report.common.network.packet

import com.google.gson.Gson
import com.rabbit.gui.GuiFoundation
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import org.apache.commons.lang3.reflect.FieldUtils
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.network.AbstractPacket

class SyncTickets(var tickets: MutableList<Ticket>) : AbstractPacket{

    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, Gson().toJson(tickets))
    }

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        this.tickets = Gson().fromJson(ByteBufUtils.readUTF8String(buf),
                FieldUtils.getField(SyncTickets::class.java, "tickets", true).genericType)
    }

    override fun handleClient(player: EntityPlayer) {
        QReportClient.syncedTickets.clear()
        QReportClient.syncedTickets.addAll(tickets)
        GuiFoundation.getCurrentStage()?.reinitShow()
    }

    override fun handleServer(player: EntityPlayer) {}
}