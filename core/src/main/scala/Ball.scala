package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

/** Represents a ball on the pool table */
class Ball(var x: Double, var y: Double, val number: Int) extends Shape with Renderable {

  val radius = 0.028575f //Official billiard ball dimensions in meters

  def render(renderer: ShapeRenderer, scale: Double): Unit = {
    renderer.setColor(Styles.BallColor)
    renderer.set(ShapeType.Filled)
    renderer.circle(x * scale, y * scale, radius * scale)
  }

}
