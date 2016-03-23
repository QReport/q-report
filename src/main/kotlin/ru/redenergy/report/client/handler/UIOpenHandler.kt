package ru.redenergy.report.client.handler

import com.rabbit.gui.GuiFoundation
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.GuiScreenEvent
import ru.redenergy.report.client.ui.SupportShow

class UIOpenHandler {


    @SubscribeEvent
    fun onUIOpen(event: GuiScreenEvent.InitGuiEvent.Post){
        if(event.gui is GuiInventory){
            event.buttonList.add(GuiButton(1024, event.gui.width / 2 + 90, event.gui.height / 2 - 80, 20, 20, "${EnumChatFormatting.BOLD}?"))
        }
    }

    @SubscribeEvent
    fun onReportCenterButtonClick(event: GuiScreenEvent.ActionPerformedEvent.Post){
        if(event.button.id == 1024)
            GuiFoundation.display(SupportShow())
    }

}