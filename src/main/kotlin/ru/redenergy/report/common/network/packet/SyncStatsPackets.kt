package ru.redenergy.report.common.network.packet

import com.google.gson.Gson
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer
import ru.redenergy.report.client.QReportClient
import ru.redenergy.report.common.Stats
import ru.redenergy.report.common.network.AbstractPacket

class SyncStatsPackets(var stats: Stats): AbstractPacket {

    override fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, Gson().toJson(stats))
    }

    override fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf) {
        val json = ByteBufUtils.readUTF8String(buf)
        this.stats = Gson().fromJson(json, Stats::class.java)
    }

    override fun handleClient(player: EntityPlayer) {
        QReportClient.syncedStats = stats
    }

    override fun handleServer(player: EntityPlayer) {
        throw UnsupportedOperationException()
    }
}