package ru.redenergy.report.common.network.packet.sync

import com.google.gson.Gson
import com.rabbit.gui.GuiFoundation
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import org.apache.commons.lang3.reflect.FieldUtils
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.compression.GZIPCompressor
import ru.redenergy.report.common.entity.Ticket
import ru.redenergy.report.common.network.Message

class SyncTickets(var tickets: MutableList<Ticket>) : Message<SyncTickets> {

    constructor(): this(mutableListOf<Ticket>())

    override fun toBytes(buf: ByteBuf) {
        val json = Gson().toJson(tickets)
        val bytes = GZIPCompressor.compress(json)
        buf.writeBytes(bytes)
    }

    override fun fromBytes(buf: ByteBuf) {
        val bytes = buf.array().copyOfRange(1, buf.array().size) //we need to cut out first byte because it is packet discriminator
        val json = GZIPCompressor.decompress(bytes)
        this.tickets = Gson().fromJson(json,
                FieldUtils.getField(SyncTickets::class.java, "tickets", true).genericType)
    }

    override fun onMessage(message: SyncTickets, ctx: MessageContext): IMessage? {
        QReportClient.syncedTickets.clear()
        QReportClient.syncedTickets.addAll(message.tickets)
        GuiFoundation.getCurrentStage()?.reinitShow()
        return null
    }


}