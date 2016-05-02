package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

/** Represents a ball on the pool table
 *  
 *  Balls implement 2D vectors that define their position, Shapes to give them
 *  a width and height, and Renderable to handle the rendering when called.
 *  The numbering of the balls is as following:

      0.  The cue balls
      1.  Yellow
      2.  Blue
      3.  Red
      4.  Purple
      5.  Orange
      6.  Green
      7.  Brown
      8.  Black
      9.  Yellow and white
      10.  Blue and white
      11.  Red and white
      12.  Purple and white
      13.  Orange and white
      14.  Green and white
      15.  Brown and white */
class Ball(var x: Float, var y: Float, var z: Float, val number: Int) extends Vector3D with Renderable {

  val mass = Ball.Mass
  val radius = Ball.Radius
  var velocity = Vector3D(0f, 0f, 0f)
  var angularVelocity = Vector3D(0f, 0f, 0f)
  var state = BallState.Sliding
  
  def render(renderer: ShapeRenderer, scale: Float): Unit = {
    renderer.setColor(Styles.BallColor)
    renderer.set(ShapeType.Filled)
    renderer.circle(x * scale, y * scale, radius * scale)
  }

}

object Ball {

  val Mass = 0.16f //In kg according to the WPA spec
  val Radius = 0.028575f //In m according to the WPA spec

}