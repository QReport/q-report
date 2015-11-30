package ru.redenergy.report.common.network

import com.google.gson.internal.UnsafeAllocator
import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.network.FMLEmbeddedChannel
import cpw.mods.fml.common.network.FMLOutboundHandler
import cpw.mods.fml.common.network.NetworkRegistry
import cpw.mods.fml.common.network.internal.FMLProxyPacket
import cpw.mods.fml.relauncher.Side
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.NetHandlerPlayServer
import java.util.*
import kotlin.reflect.KClass

/**
 * Rewritten in Kotlin
 */
@ChannelHandler.Sharable
public class  NetworkHandler : MessageToMessageCodec<FMLProxyPacket, AbstractPacket>() {

    private var channels: EnumMap<Side, FMLEmbeddedChannel>? = null
    private var initialized: Boolean = false
    private var postInitialized: Boolean = false
    private var packets : MutableList<KClass<out AbstractPacket>> = arrayListOf()

    public fun initialise(){
        if(!initialized){
            channels = NetworkRegistry.INSTANCE.newChannel("qreport", this)
            initialized = true
        }
    }

    public fun registerPacket(type: KClass<out AbstractPacket>) : Boolean{
        if(this.packets.size > 256 || this.packets.contains(type) || this.postInitialized){
            return false
        }
        this.packets.add(type)
        return true
    }

    public fun postInitialize(){
        if(postInitialized){
            this.packets.sortBy { it.qualifiedName }
            this.postInitialized = true
        }
    }

    override fun decode(ctx: ChannelHandlerContext, msg: FMLProxyPacket, out: MutableList<Any>) {
        var payload = msg.payload()
        var discriminator = payload.readByte()
        var type = this.packets.get(discriminator.toInt())
        var packet = UnsafeAllocator.create().newInstance(type.java)
        packet.decodeInto(ctx, payload.slice())
        when(FMLCommonHandler.instance().effectiveSide){
            Side.CLIENT -> {
                packet.handleClient(Minecraft.getMinecraft().thePlayer)
            }
            Side.SERVER -> {
                var netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()
                packet.handleServer((netHandler as NetHandlerPlayServer).playerEntity)
            }
        }
        out.add(packet)
    }

    override fun encode(ctx: ChannelHandlerContext, msg: AbstractPacket, out: MutableList<Any>) {
        var buffer = Unpooled.buffer()
        var type : KClass<out AbstractPacket> = msg.javaClass.kotlin
        if(!this.packets.contains(type))
            throw IllegalAccessException("No packet registered for " + type.qualifiedName)
        val discriminator = this.packets.indexOf(type)
        buffer.writeByte(discriminator)
        msg.encodeInto(ctx, buffer)
        out.add(FMLProxyPacket(buffer.copy(), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get()))
    }

    fun sendToAll(message:AbstractPacket) {
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL)
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).writeAndFlush(message)
    }

    fun sendTo(message:AbstractPacket, player: EntityPlayerMP) {
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER)
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player)
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).writeAndFlush(message)
    }

    fun sendToAllAround(message:AbstractPacket, point: NetworkRegistry.TargetPoint) {
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT)
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point)
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).writeAndFlush(message)
    }

    fun sendToDimension(message:AbstractPacket, dimensionId:Int) {
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION)
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimensionId))
        (this.channels?.get(Side.SERVER) as FMLEmbeddedChannel).writeAndFlush(message)
    }

    fun sendToServer(message:AbstractPacket) {
        (this.channels?.get(Side.CLIENT) as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER)
        (this.channels?.get(Side.CLIENT) as FMLEmbeddedChannel).writeAndFlush(message)
    }

    companion object {
        public val instance = NetworkHandler()
    }
}