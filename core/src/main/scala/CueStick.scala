package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/** Represents the cue stick in the game */
@SerialVersionUID(1L)
class CueStick(var pointAt: Vector3D, var distance: Float, var rotationDegrees: Float) extends Serializable {

  /** Renders a stick pointing at the given point at the given distance
    *
    * @param renderer The shape renderer to use
    * @param scale    Screen pixels per in-game meter
    */
  def render(renderer: ShapeRenderer, scale: Float): Unit = {
    renderer.identity()
    renderer.translate(scale * pointAt.x, scale * pointAt.y, 0f)
    renderer.rotate(0f, 0f, 1f, rotationDegrees)
    renderer.translate(scale * -distance, 0f, 0f)
    renderer.setColor(Styles.CueColor)
    renderer.rect(scale * -CueStick.Width, scale * -CueStick.Height / 2f, scale * CueStick.Width, scale * CueStick.Height)
    renderer.identity()
  }

}

/** Companion object to specify the dimensions of the cue stick */
object CueStick {
  val Height: Float = 0.03f
  val Width: Float = 1.5f
}