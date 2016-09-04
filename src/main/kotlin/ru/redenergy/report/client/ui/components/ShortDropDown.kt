package ru.redenergy.report.client.ui.components

import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.DropDown

/**
 * Implementation of dropdown which doesn't displays selected item but allows to select them
 */
class ShortDropDown<T>(xPos: Int, yPos: Int, width: Int): DropDown<T>(xPos, yPos, width) {


    override fun onDraw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (isVisible()) {
            if (isUnrolled) {
                drawExpandedList(mouseX, mouseY, partialTicks)
            }
        }
        componentsList.forEach { com -> com.onDraw(mouseX, mouseY, partialTicks) }
    }

    override fun initDropButton() {
        this.dropButton = Button(getX(), getY(), 12, 12, "\u25BC")
    }
}