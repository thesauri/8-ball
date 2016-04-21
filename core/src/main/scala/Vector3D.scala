package com.walter.eightball

import scala.math._

/** Represents a vector in a 3D space */
trait Vector3D {
  var x: Float
  var y: Float
  var z: Float
  
  /** Returns the sum of two vectors */
  def +(v: Vector3D): Vector3D = Vector3D(x + v.x, y + v.y, z + v.z)
  
  /** Returns the difference of two vectors */
  def -(v: Vector3D): Vector3D = ???
  
  /** Adds a vector to this vector
   *  
   *  @param v the vector to add
   *  @return the vector itself
   */
  def +=(v: Vector3D): Vector3D = {
    x += v.x
    y += v.y
    z += v.z
    this
  }
  
  /** Returns the vector multiplied with a constant */
  def *(c: Float): Vector3D = Vector3D(x * c, y * c, z * c)
  
  /** Returns the cross product between this and another vector */
  def cross(v: Vector3D): Vector3D =
    Vector3D(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x)
  
  /** Returns the norm (length) of the vector */
  def norm: Float = sqrt(pow(x.toDouble, 2) + pow(y.toDouble, 2) + pow(z.toDouble, 2)).toFloat
    
  /** Returns a normalized version of the vector */
  def normalized: Vector3D = (1 / norm) * this
  
  override def equals(v: Any): Boolean = v match {
    case v: Vector3D => (x == v.x) && (y == v.y) && (z == v.z)
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
    
    def *(v: Vector3D): Vector3D = v * f
    
  }
}