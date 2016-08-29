package ru.redenergy.report.common.network

import cpw.mods.fml.common.network.simpleimpl.*
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.Side.*
import net.minecraft.entity.player.EntityPlayerMP
import ru.redenergy.report.common.network.packet.*
import ru.redenergy.report.common.network.packet.requests.RequestBlockedPlayers
import ru.redenergy.report.common.network.packet.requests.RequestSyncPacket
import ru.redenergy.report.common.network.packet.sync.SyncBlockedPlayers
import ru.redenergy.report.common.network.packet.sync.SyncStatsPackets
import ru.redenergy.report.common.network.packet.sync.SyncTickets
import kotlin.reflect.KClass

object  NetworkHandler {

    private var packetsRegistered = false
    private var nextPacketIndex = 0
    val network = SimpleNetworkWrapper("qreport")

    fun registerDefaultPackets(){
        if(packetsRegistered) return
        registerPacket(TicketPacket::class, SERVER)
        registerPacket(RequestSyncPacket::class, SERVER)
        registerPacket(SyncTickets::class, CLIENT)
        registerPacket(AddMessagePacket::class, SERVER)
        registerPacket(UpdateAdminAccess::class, CLIENT)
        registerPacket(ChangeTicketStatus::class, SERVER)
        registerPacket(SyncStatsPackets::class, CLIENT)
        registerPacket(DeleteTicketPacket::class, SERVER)
        registerPacket(RequestBlockedPlayers::class, SERVER)
        registerPacket(SyncBlockedPlayers::class, CLIENT)
        registerPacket(ChangePlayerStatus::class, SERVER)
        packetsRegistered = true
    }

    fun <T> registerPacket(packetClass: KClass<T>, side: Side) where T : IMessage, T: IMessageHandler<T, IMessage>{
        network.registerMessage(packetClass.java, packetClass.java, nextPacketIndex++, side)
    }

    fun sendTo(message: Message<*>, receiver: EntityPlayerMP){
        network.sendTo(message, receiver)
    }

    fun sendToServer(message: Message<*>){
        network.sendToServer(message)
    }
}