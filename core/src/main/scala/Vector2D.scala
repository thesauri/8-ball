package com.walter.eightball

/** Represents a vector in a 2D space */
trait Vector2D {
  var x: Float
  var y: Float
}

object Vector2D {
  class V2D(var x: Float, var y: Float) extends Vector2D
  
  def apply(x: Float, y: Float): Vector2D = {
    new V2D(x, y)
  }
}