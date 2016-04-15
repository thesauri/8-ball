package com.walter.eightball

/** Represents a vector in a 2D space */
trait Vector3D {
  var x: Float
  var y: Float
  var z: Float
  
  /** Returns the sum of two vectors */
  def +(v: Vector3D) = ???
  
  /** Adds a vector to this vector
   *  
   *  @param v the vector to add
   *  @return the vector itself
   */
  def +=(v: Vector3D): Vector3D = ???
  
  /** Returns the vector multiplied with a constant */
  def *(c: Float): Vector2D = ???
  
  override def equals(v: Any): Boolean = v match {
    case v: Vector3D => ???
    case _ => false
  }
  
  override def toString: String = s"(${x}x${y}x${z})"
 
}

object Vector3D {
  class V3D(var x: Float, var y: Float, var z: Float) extends Vector3D
  
  def apply(x: Float, y: Float, z: Float): Vector3D = {
    new V3D(x, y, z)
  }
  
  /** Support Vector operations on floats */
  implicit class FloatVector3DSupport(f: Float) {
    
    def *(v: Vector3D): Vector3D = ???
    
  }
}