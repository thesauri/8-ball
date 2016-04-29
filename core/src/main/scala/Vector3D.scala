package com.walter.eightball

import com.badlogic.gdx.math.Vector3

import scala.math._

/** Represents a vector in a 3D space */
trait Vector3D {
  var x: Float
  var y: Float
  var z: Float
  
  /** Returns the sum of two vectors */
  def +(v: Vector3D): Vector3D = Vector3D(x + v.x, y + v.y, z + v.z)
  
  /** Returns the difference of two vectors */
  def -(v: Vector3D): Vector3D = Vector3D(x - v.x, y - v.y, z - v.z)
  
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
  
  /** Subtracts this vector with the values of another vector
   *  
   *  @param v the vector to substract
   *  @return the vector itself
   */
  def -=(v: Vector3D): Vector3D = {
    x -= v.x
    y -= v.y
    z -= v.z
    this
  }
  
  /** Returns the vector multiplied with a constant */
  def *(c: Float): Vector3D = Vector3D(x * c, y * c, z * c)

  /** Returns the angle formed by the x and y axes relative to (1,0) */
  def angle2d: Float = (toDegrees(atan2(y, x)).toFloat + 360f) % 360f
  
  /** Returns the cross product between this and another vector */
  def cross(v: Vector3D): Vector3D =
    Vector3D(y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x)
  
  /** Returns the dot product of this vector with another vector */
  def dot(v: Vector3D): Float = x * v.x + y * v.y + z * v.z
  
  /** Returns the norm (length) of the vector */
  def len: Float = sqrt(pow(x.toDouble, 2) + pow(y.toDouble, 2) + pow(z.toDouble, 2)).toFloat

  /** Returns a normalized version of the vector */
  def normalized: Vector3D = {
    val curNorm = len
    if (curNorm == 0f) {
      this
    } else {
      (1 / curNorm) * this
    }
  }
  
  override def equals(v: Any): Boolean = v match {
    case v: Vector3D => (x == v.x) && (y == v.y) && (z == v.z)
    case _ => false
  }
  
  override def toString: String = s"(${x},${y},${z})"
 
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

  /** Implicit conversion of custom Vector3D class to libgdx Vector3 class */
  implicit def vector3DtoVector3(v: Vector3D): Vector3 = new Vector3(v.x, v.y, v.z)

  /** Implicit conversion of libgdx Vector3 class to custom Vector3D class */
  implicit def vector3toVector3D(v: Vector3): Vector3D = Vector3D(v.x, v.y, v.z)
}