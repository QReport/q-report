package ru.redenergy.report.client.ui.admin

import com.rabbit.gui.background.DefaultBackground
import com.rabbit.gui.component.control.Button
import com.rabbit.gui.component.control.MultiTextbox
import com.rabbit.gui.component.display.Shape
import com.rabbit.gui.component.display.ShapeType
import com.rabbit.gui.component.display.TextLabel
import com.rabbit.gui.component.display.graph.PieChart
import com.rabbit.gui.render.TextAlignment
import com.rabbit.gui.show.Show
import net.minecraft.client.resources.I18n
import org.apache.commons.lang3.time.DurationFormatUtils
import ru.redenergy.report.client.QReportClient
import java.awt.Color

class StatisticsShow: Show() {

    init {
        background = DefaultBackground()
    }

    override fun setup() {
        super.setup()
        registerComponent(TextLabel(this.width / 3, this.height / 5, this.width / 3, 20, I18n.format("show.stats.title"), TextAlignment.CENTER))

        val avgTime = if(QReportClient.syncedStats.averageTime == -1L) I18n.format("show.stats.unknown")
                        else formatAverageResponseTime()
        registerComponent(TextLabel(this.width / 3 + this.width / 20, this.height / 3, this.width / 3, 20, I18n.format("show.stats.avgtime", avgTime)))

        registerComponent(MultiTextbox(this.width / 3 + this.width / 20 - 5, this.height / 3 + 30, this.width / 3, this.height / 7)
                .setId("active_users")
                .setBackgroundVisibility(false)
                .setIsEnabled(false))
        updateActiveUsersLabel()

        val data = if(QReportClient.syncedStats.tickets.values.sum() == 0) doubleArrayOf(1.0, 1.0, 1.0, 1.0)
                        else QReportClient.syncedStats.tickets.values.map { it.toDouble() } .toDoubleArray()

        registerComponent(PieChart(this.width / 3 + this.width / 20, this.height / 2 + this.height / 10, (this.width / 7 + this.height / 5) / 2,
                data,
                QReportClient.syncedStats.tickets.values.toList().map { it.toString() }.toTypedArray()))

        val colorLabelsX = this.width / 2 + 25
        val colorLabelsY = this.width / 18 + this.height / 2;
        val colorShapeWidth = this.width / 60
        val colorShapeHeight = this.height / 50
        val colorTitles = QReportClient.syncedStats.tickets.keys.toList().map { I18n.format(it.translateKey) }

        for((i, color) in arrayOf(Color.BLUE, Color.RED, Color.ORANGE, Color.MAGENTA).withIndex()){
            registerComponent(TextLabel(colorLabelsX + colorShapeWidth, colorLabelsY + this.height / 6 - this.height / 20 * i, 200, 20, " - ${colorTitles[i]}"))
            registerComponent(Shape(colorLabelsX, colorLabelsY + this.height / 6 - this.height / 20 * i, colorShapeWidth, colorShapeHeight, ShapeType.RECT, color))
        }

        registerComponent(Button(this.width / 3, this.height / 10 * 9, this.width / 3, 20, I18n.format("show.stats.back"))
                            .setClickListener { this.getStage().displayPrevious() })

    }

    fun formatAverageResponseTime(): String = DurationFormatUtils.formatDuration(QReportClient.syncedStats.averageTime,  "HH:mm")

    fun updateActiveUsersLabel(){
        val output = StringBuilder()
        with(output){
            if(QReportClient.syncedStats.activeUsers.size > 0) {
                append("${I18n.format("show.stats.activeusers")} \n")
                QReportClient.syncedStats.activeUsers.forEach {
                    append("${it.key} - ${it.value} ${declensionTicketsLocales(it.value)}\n")
                }
            }
        }
        findComponentById<MultiTextbox>("active_users").setText(output.toString())
    }

    fun declensionTicketsLocales(amount: Int): String{
        val remainder = amount % 10
        val secondRemainder = (remainder / 10) % 10
        if((remainder == 1) && (secondRemainder != 1))
            return I18n.format("ticket.one")
        else if ((remainder >= 2) && (remainder <= 4) && (secondRemainder != 1))
            return I18n.format("ticket.few")
        else
            return I18n.format("ticket.many")
    }

}