package ru.redenergy.report.common.network

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.minecraft.entity.player.EntityPlayer

/**
 * Representation of network packet
 */
interface AbstractPacket {

    /**
     * Extracts variables from packet into given ByteBuf
     */
    fun encodeInto(ctx: ChannelHandlerContext, buf: ByteBuf)

    /**
     * Imports variables from given ByteBuf to the packet object
     */
    fun decodeInto(ctx: ChannelHandlerContext, buf: ByteBuf)

    /**
     * Called when packet is received on client
     */
    fun handleClient(player: EntityPlayer)

    /**
     * Called when packet is received on server
     */
    fun handleServer(player: EntityPlayer)
}