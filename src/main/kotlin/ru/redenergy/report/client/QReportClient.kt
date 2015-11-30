package ru.redenergy.report.client

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPostInitializationEvent

@Mod(modid = "qreport-client", name = "QReport - Q&A communication tool for players and admins", modLanguageAdapter = "ru.redenergy.report.kotlin.KotlinAdapter")
object QReportClient {

    @Mod.EventHandler
    public fun postInit(event: FMLPostInitializationEvent){
        println("QReport Loaded")
    }
}

