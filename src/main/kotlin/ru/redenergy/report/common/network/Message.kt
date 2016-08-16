package ru.redenergy.report.common.network

import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import io.netty.buffer.ByteBuf

interface Message<T : IMessage>: IMessage, IMessageHandler<T, IMessage>{

    override fun toBytes(buf: ByteBuf)

    override fun fromBytes(buf: ByteBuf)

    override fun onMessage(message: T, ctx: MessageContext): IMessage?
}