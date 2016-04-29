package com.walter.eightball
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class CueStick(var pointAt: Vector3D, var distance: Float) extends Renderable {

  /** Renders a stick pointing at the given point at the given distance */
  override def render(renderer: ShapeRenderer, scale: Float): Unit = {
    ???
  }

}

object CueStick {
  val Height: Float = 0.03f
  val Width: Float = 1.5f
}