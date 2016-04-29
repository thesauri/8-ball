package com.walter.eightball
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class CueStick(var pointAt: Vector3D, var distance: Float, var rotationDegrees: Float) extends Renderable {

  /** Renders a stick pointing at the given point at the given distance */
  override def render(renderer: ShapeRenderer, scale: Float): Unit = {
    renderer.identity()
    renderer.translate(scale * pointAt.x, scale * pointAt.y, 0f)
    renderer.rotate(0f, 0f, 1f, rotationDegrees)
    renderer.translate(scale * -distance, 0f, 0f)
    renderer.setColor(Styles.CueColor)
    renderer.rect(scale * -CueStick.Width, scale * -CueStick.Height / 2f, scale * CueStick.Width, scale * CueStick.Height)
    renderer.identity()
  }

}

object CueStick {
  val Height: Float = 0.03f
  val Width: Float = 1.5f
}