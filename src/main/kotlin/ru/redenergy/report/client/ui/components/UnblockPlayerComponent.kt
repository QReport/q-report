package ru.redenergy.report.client.ui.components

import com.rabbit.gui.component.GuiWidget
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.render.Renderer
import com.rabbit.gui.render.TextAlignment
import ru.redenergy.report.common.BlockedPlayer

class UnblockPlayerComponent(x: Int, y: Int, width: Int, height: Int, player: BlockedPlayer?):
                                                                            GuiWidget(x, y, width, height)  {
    var player: BlockedPlayer? = null
        set(value){
            field = value
            if(field != null)
                textLabel?.text = "Are you sure want to unblock ${field!!.name}?"
        }

    var buttonYes: Button? = null
    var buttonNo: Button? = null
    var textLabel: TextLabel? = null
    var listener: ((BlockedPlayer?, Boolean) -> Unit)? = null

    init {
        this.player = player
    }

    var enabled = true
        set(value){
            field = value
            buttonNo?.setIsEnabled(field)
            buttonNo?.setIsVisible(field)
            buttonYes?.setIsEnabled(field)
            buttonYes?.setIsVisible(field)
            textLabel?.setIsVisible(field)
        }

    override fun setup() {
        super.setup()
        buttonYes = Button(this.x + width / 4 - width / 8, this.y + height / 3 * 2, width / 4 - 5, 20, "Yes")
                            .setClickListener { listener?.invoke(player, true) }
        buttonNo = Button(this.x + width / 4 * 3 - width / 8, this.y + height / 3 * 2, width / 4 - 5, 20, "No")
                            .setClickListener { listener?.invoke(player, false) }
        textLabel = TextLabel(this.x + width / 2 - width / 8 * 3, this.y + height / 3, width / 8 * 6, 20, "")
                            .setMultilined(true).setTextAlignment(TextAlignment.CENTER)
        registerComponent(buttonYes)
        registerComponent(buttonNo)
        registerComponent(textLabel)
        buttonNo?.setIsEnabled(enabled)
        buttonNo?.setIsVisible(enabled)
        buttonYes?.setIsEnabled(enabled)
        buttonYes?.setIsVisible(enabled)
        textLabel?.setIsVisible(enabled)
    }

    override fun onDraw(mouseX: Int, mouseY: Int, partialTicks: Float) {
        if(enabled) {
            Renderer.drawRect(getX() - 1, getY() - 1, getX() + this.width + 1, getY() + this.height + 1, -6250336)
            Renderer.drawRect(getX(), getY(), getX() + this.width, getY() + this.height, -0xFFFFFF - 1)
        }
        super.onDraw(mouseX, mouseY, partialTicks)
    }
}