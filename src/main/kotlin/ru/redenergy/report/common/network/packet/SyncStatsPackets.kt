package ru.redenergy.report.common.network.packet

import com.google.gson.Gson
import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.Stats
import ru.redenergy.report.common.network.Message

class SyncStatsPackets(var stats: Stats): Message<SyncStatsPackets> {

    constructor(): this(QReportClient.syncedStats) //it doesn't serves any purpose just dummy data for packet creation

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, Gson().toJson(stats))
    }

    override fun fromBytes(buf: ByteBuf) {
        val json = ByteBufUtils.readUTF8String(buf)
        this.stats = Gson().fromJson(json, Stats::class.java)
    }

    override fun onMessage(message: SyncStatsPackets, ctx: MessageContext): IMessage? {
        QReportClient.syncedStats = message.stats
        return null
    }

}