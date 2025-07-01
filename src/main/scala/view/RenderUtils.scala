package view

import java.awt.{BasicStroke, Color, Font, Graphics2D, RenderingHints}
import java.awt.geom.{Ellipse2D, Rectangle2D}
import config.UIConfig.*
import model.Match.Position

object RenderUtils:

  def setupGraphics(g: Graphics2D): Unit =
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

  def drawCenteredOval(g: Graphics2D, position: Position, size: Int, color: Color, strokeWidth: Int = 1): Unit =
    val x     = position.x - size / 2
    val y     = position.y - size / 2
    val shape = new Ellipse2D.Double(x, y, size, size)

    g.setColor(color)
    g.fill(shape)

    if strokeWidth > 0 then
      g.setColor(Colors.goalColor)
      g.setStroke(new BasicStroke(strokeWidth))
      g.draw(shape)

  def drawCenteredRect(g: Graphics2D, position: Position, size: Int, color: Color, strokeWidth: Int = 1): Unit =
    val x     = position.x - size / 2
    val y     = position.y - size / 2
    val shape = new Rectangle2D.Double(x, y, size, size)

    g.setColor(color)
    g.fill(shape)

    if strokeWidth > 0 then
      g.setColor(Colors.goalColor)
      g.setStroke(new BasicStroke(strokeWidth))
      g.draw(shape)

  def drawText(g: Graphics2D, text: String, x: Int, y: Int, color: Color, fontSize: Int = 12): Unit =
    g.setColor(color)
    g.setFont(new Font("Arial", Font.BOLD, fontSize))
    g.drawString(text, x, y)

  def drawText(g: Graphics2D, text: String, x: Int, y: Int, color: Color, font: Font): Unit =
    g.setColor(color)
    g.setFont(font)
    g.drawString(text, x, y)
