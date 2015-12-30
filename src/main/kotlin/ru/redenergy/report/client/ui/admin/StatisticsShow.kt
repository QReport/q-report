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
        registerComponent(TextLabel(this.width / 3 + this.width / 20, this.height / 3, this.width / 3, 20, I18n.format("show.stats.avgtime", formatAverageResponseTime())))

        registerComponent(MultiTextbox(this.width / 3 + this.width / 20 - 5, this.height / 3 + 30, this.width / 3, this.height / 7)
                .setId("active_users")
                .setBackgroundVisibility(false)
                .setIsEnabled(false))
        updateActiveUsersLabel()

        registerComponent(PieChart(this.width / 3 + this.width / 20, this.height / 2 + this.height / 10, (this.width / 7 + this.height / 5) / 2,
                QReportClient.syncedStats.tickets.values.map { it.toDouble() } .toDoubleArray(),
                QReportClient.syncedStats.tickets.values.toList().map { it.toString() }.toTypedArray()))

        val colorLabelsX = this.width / 2 + 25
        val colorLabelsY = this.width / 18 + this.height / 2;
        val colorShapeWidth = this.width / 60
        val colorShapeHeight = this.height / 50
        val colorTitles = QReportClient.syncedStats.tickets.keys.toList().map { it.translation }

        registerComponent(Shape(colorLabelsX, colorLabelsY + this.height / 25, colorShapeWidth, colorShapeHeight, ShapeType.RECT, Color.BLUE))
        registerComponent(Shape(colorLabelsX, colorLabelsY + this.height / 12, colorShapeWidth, colorShapeHeight, ShapeType.RECT, Color.RED))
        registerComponent(Shape(colorLabelsX, colorLabelsY + this.height / 8, colorShapeWidth, colorShapeHeight, ShapeType.RECT, Color.ORANGE))
        registerComponent(Shape(colorLabelsX, colorLabelsY + this.height / 6, colorShapeWidth, colorShapeHeight, ShapeType.RECT, Color.MAGENTA))

        registerComponent(TextLabel(colorLabelsX + colorShapeWidth, colorLabelsY + this.height / 25, 200, 20, " - " + colorTitles[0]))
        registerComponent(TextLabel(colorLabelsX + colorShapeWidth, colorLabelsY + this.height / 12, 200, 20, " - " + colorTitles[1]))
        registerComponent(TextLabel(colorLabelsX + colorShapeWidth, colorLabelsY + this.height / 8, 200, 20, " - " + colorTitles[2]))
        registerComponent(TextLabel(colorLabelsX + colorShapeWidth, colorLabelsY + this.height / 6, 200, 20, " - " + colorTitles[3]))

        registerComponent(Button(this.width / 3, this.height / 10 * 9, this.width / 3, 20, I18n.format("show.stats.back")))

    }

    fun formatAverageResponseTime(): String = DurationFormatUtils.formatDuration(QReportClient.syncedStats.averageTime,  "HH:mm")

    fun updateActiveUsersLabel(){
        val output = StringBuilder()
        with(output){
            append("${I18n.format("show.stats.activeusers")} \n")
            QReportClient.syncedStats.activeUsers.forEach {
                append("${I18n.format("show.stats.activeusers.entry", it.key, it.value)}\n")
            }
        }
        findComponentById<MultiTextbox>("active_users").setText(output.toString())
    }


}