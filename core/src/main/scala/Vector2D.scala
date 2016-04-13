package com.walter.eightball

/** Represents a vector in a 2D space */
trait Vector2D {
  var x: Float
  var y: Float
  
  /** Returns the sum of two vectors */
  def +(v: Vector2D) = Vector2D(x + v.x, y + v.y)
  
  /** Adds a vector to this vector
   *  
   *  @param v the vector to add
   *  @return the vector itself
   */
  def +=(v: Vector2D): Vector2D = {
    x += v.x
    y += v.y
    this
  }
  
  override def equals(v: Any): Boolean = v match {
    case v: Vector2D => (x == v.x) && (y == v.y)
    case _ => false
  }
  
  override def toString: String = s"(${x}x${y})" 
}

object Vector2D {
  class V2D(var x: Float, var y: Float) extends Vector2D
  
  def apply(x: Float, y: Float): Vector2D = {
    new V2D(x, y)
  }
}