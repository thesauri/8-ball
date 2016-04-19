package com.walter.eightball

import org.scalatest._

class Vector3DTest extends FlatSpec with Matchers {

  "+" should "return the sum of two vectors" in {
    val v = Vector3D(2f, 3f, -2f) + Vector3D(10f, 2f, 2f)
    v should be (Vector3D(12f, 5f, 0f))
  }
  
  "+=" should "add a vector to an existing vector" in {
    val v = Vector3D(10f, 2f, 10f)
    val expectedResult = Vector3D(12f, 7f, 5f)
    (v += Vector3D(2f, 5f, -5f)) should be (expectedResult)
    v should be (expectedResult)
  }
  
  "*" should "return the vector multiplied by a constant" in {
    (Vector3D(12f, 6f, 4f) * 0.25f) should be (Vector3D(3f, 1.5f, 1f))
  }
  
  "float * vector" should "be supported" in {
    (0.25f * Vector3D(12f, 6f, 4f)) should be (Vector3D(3f, 1.5f, 1f))
  }
  
  "vector cross vector" should "return the cross product between the vectors" in {
    val v1 = Vector3D(12f, -27f, 29f)
    val v2 = Vector3D(48f, -127f, 13f)
    (v1 cross v2) should be (Vector3D(3332f, 1236f, -228f))
    val v3 = Vector3D(-19f, 2f, 0f)
    val v4 = Vector3D(17f, -127f, 7f)
    (v3 cross v4) should be (Vector3D(14f, 133f, 2379f))
  }
}