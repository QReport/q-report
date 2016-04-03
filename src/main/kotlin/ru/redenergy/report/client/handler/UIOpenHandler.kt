package ru.redenergy.report.client.handler

import com.rabbit.gui.GuiFoundation
import com.rabbit.gui.render.Renderer
import com.rabbit.gui.utils.Geometry
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainerCreative
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.GuiScreenEvent
import org.lwjgl.opengl.GL11
import ru.redenergy.report.client.ui.SupportShow

class UIOpenHandler {


    @SubscribeEvent
    fun onUIOpen(event: GuiScreenEvent.InitGuiEvent.Post){
        val gameInventory = event.gui is GuiInventory
        val creativeInventory = event.gui is GuiContainerCreative
        val btnX = event.gui.width / 2 + 90 + (if (creativeInventory) 10 else 0)
        val btnY = event.gui.height / 2 - 80 - (if (creativeInventory) 10 else 0)
        if(gameInventory || creativeInventory)
            event.buttonList.add(SupportButton(1024, btnX, btnY))
    }

    @SubscribeEvent
    fun onReportCenterButtonClick(event: GuiScreenEvent.ActionPerformedEvent.Post){
        if(event.button.id == 1024)
            GuiFoundation.display(SupportShow())
    }

    private class SupportButton(id: Int, x: Int, y: Int): GuiButton(id, x, y, 20, 20, "") {

        val texture = ResourceLocation("qreport-client", "icons/support-ic.png")

        override fun drawButton(mc: Minecraft, mouseX: Int, mouseY: Int) {
            if(this.visible){
                val hovered = Geometry.isDotInArea(xPosition, yPosition, width, height, mouseX, mouseY)
                mc.textureManager.bindTexture(texture)
                if(hovered){
                    Renderer.drawTexturedModalRect(xPosition, yPosition, 0, 20, 20, 20, 20, 40, 0F)
                } else {
                    Renderer.drawTexturedModalRect(xPosition, yPosition, 0, 0, 20, 20, 20, 40, 0F)
                }
            }
        }
    }

}