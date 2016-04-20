package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

/** Represents a ball on the pool table
 *  
 *  Balls implement 2D vectors that define their position, Shapes to give them
 *  a width and height, and Renderable to handle the rendering when called. */
class Ball(var x: Float, var y: Float, var z: Float, val number: Int) extends Vector3D with Renderable {

  val mass = 0.16f //In kg according to the WPA spec
  val radius = 0.028575f //In m according to the WPA spec
  var velocity = Vector3D(0f, 0f, 0f)
  var angularVelocity = Vector3D(0f, 0f, 0f)
  var state = BallState.Sliding
  
  def render(renderer: ShapeRenderer, scale: Float): Unit = {
    renderer.setColor(Styles.BallColor)
    renderer.set(ShapeType.Filled)
    renderer.circle(x * scale, y * scale, radius * scale)
  }

}
