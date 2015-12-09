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
public class  NetworkHandler: MessageToMessageCodec<FMLProxyPacket, AbstractPacket>() {

    /**
     * Synchronous message channel pair based on netty
     */
    private lateinit var channels: EnumMap<Side, FMLEmbeddedChannel>

    /**
     * Contains packets, registered in current network
     */
    private var packets : MutableList<KClass<out AbstractPacket>> = arrayListOf()

    private var initialized: Boolean = false
    private var postInitialized: Boolean = false

    /**
     * Initializes Netty channels
     */
    public fun initialise(){
        if(!initialized){
            channels = NetworkRegistry.INSTANCE.newChannel("qreport", this)
            initialized = true
        }
    }

    /**
     * Registers packet in network <br>
     * Returns <code>true</code> if packet has been successfully registered, <code>false</code> otherwise <br>
     * Note: max packet limit is 256 (because it's max value one byte can store)
     */
    public fun registerPacket(type: KClass<out AbstractPacket>) : Boolean{
        if(this.packets.size > 256 || this.packets.contains(type) || this.postInitialized){
            return false
        }
        this.packets.add(type)
        return true
    }

    /**
     * Sorts packets and disables new packets registration
     */
    public fun postInitialize(){
        if(postInitialized){
            this.packets.sortBy { it.qualifiedName }
            this.postInitialized = true
        }
    }

    /**
     * Called when packet is received
     */
    override fun decode(ctx: ChannelHandlerContext, msg: FMLProxyPacket, out: MutableList<Any>) {
        var payload = msg.payload()
        var discriminator = payload.readByte()
        var type = this.packets[discriminator.toInt()]
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

    /**
     * Called when packet is sent
     */
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

    /**
     * Sends packet to everyone on server
     */
    fun sendToAll(message: AbstractPacket) {
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL)
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).writeAndFlush(message)
    }

    /**
     * Sends packet to specified player
     */
    fun sendTo(message: AbstractPacket, player: EntityPlayerMP) {
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER)
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player)
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).writeAndFlush(message)
    }

    /**
     * Sends packet to everyone in given target point
     */
    fun sendToAllAround(message: AbstractPacket, point: NetworkRegistry.TargetPoint) {
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT)
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point)
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).writeAndFlush(message)
    }

    /**
     * Sends packet to everyone in dimension with given id
     */
    fun sendToDimension(message: AbstractPacket, dimensionId: Int) {
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION)
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimensionId))
        (this.channels[Side.SERVER] as FMLEmbeddedChannel).writeAndFlush(message)
    }

    /**
     * Sends packet to the server
     */
    fun sendToServer(message:AbstractPacket) {
        (this.channels[Side.CLIENT] as FMLEmbeddedChannel).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER)
        (this.channels[Side.CLIENT] as FMLEmbeddedChannel).writeAndFlush(message)
    }

    companion object {
        public val instance = NetworkHandler()
    }
}