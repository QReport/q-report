package ru.redenergy.report.common.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer

/**
 * Rewritten in Kotlin
 */
interface AbstractPacket {
    abstract fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf)

    abstract fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf)

    abstract fun handleClient(player: EntityPlayer)

    abstract fun handleServer(player: EntityPlayer)
}