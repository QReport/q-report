package ru.redenergy.report.common.network.packet

import cpw.mods.fml.common.network.ByteBufUtils
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufUtil
import ru.redenergy.report.common.network.Message

class BlockPlayerPacket(var player: String): Message<BlockPlayerPacket> {

    constructor(): this("")

    override fun toBytes(buf: ByteBuf) {
        ByteBufUtils.writeUTF8String(buf, player)
    }

    override fun fromBytes(buf: ByteBuf) {
        this.player = ByteBufUtils.readUTF8String(buf)
    }

    override fun onMessage(message: BlockPlayerPacket, ctx: MessageContext): IMessage? {

        return null
    }
}