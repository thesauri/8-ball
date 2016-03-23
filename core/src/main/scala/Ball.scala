package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

/** Represents a ball on the pool table */
class Ball(var x: Float, var y: Float, val number: Int) extends Vector2D(x, y) with Shape with Renderable {

  val radius = 0.028575f //Official billiard ball dimensions in meters

  def render(renderer: ShapeRenderer, scale: Float): Unit = {
    renderer.setColor(Styles.BallColor)
    renderer.set(ShapeType.Filled)
    renderer.circle(x * scale, y * scale, radius * scale)
  }

}
